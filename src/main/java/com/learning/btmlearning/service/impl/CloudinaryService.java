package com.learning.btmlearning.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.learning.btmlearning.dto.request.FileUploadRequest;
import com.learning.btmlearning.dto.response.FileUploadResponse;
import com.learning.btmlearning.entity.FileUpload;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.FileUploadMapper;
import com.learning.btmlearning.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final FileUploadRepository fileUploadRepository;
    private final FileUploadMapper fileUploadMapper;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_RETRY = 3;

    public String uploadImage(MultipartFile file, String folder){
        validateImageFile(file);

        try {
            Map uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "transformation", new Transformation().width(800).height(800).crop("limit").fetchFormat("webp")
            );

            Map result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new AppException(ErrorCode.CANNOT_UPLOAD_IMAGE);
        }
    }

    public FileUploadResponse uploadCourseThumbnail(MultipartFile file, Long courseId) throws IOException {
        validateImageFile(file);

        Map uploadParams = ObjectUtils.asMap(
                "folder", "courses/thumbnails",
                "public_id", "course_" + courseId,
                "resource_type", "image",
                "transformation", new Transformation()
                        .width(800)
                        .height(450)
                        .crop("fill")
                        .fetchFormat("webp")
                        .quality("auto")
        );

        Map result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        FileUpload fileUpload = FileUpload.builder()
                .folderName(result.get("folder").toString())
                .filePath(result.get("secure_url").toString())
                .fileType(result.get("resource_type").toString())
                .fileName(result.get("public_id").toString())
                .build();

        return fileUploadMapper.toFileUploadResponse(fileUploadRepository.save(fileUpload));
    }

    public FileUploadResponse uploadVideo(MultipartFile file, Long lessonId) throws IOException {
        if (file.getContentType() == null || !file.getContentType().startsWith("video/")) {
            throw new IllegalArgumentException("File phải là video");
        }

        Map uploadParams = ObjectUtils.asMap(
                "folder", "courses/videos",
                "public_id", "lesson_" + lessonId,
                "resource_type", "video",
                "eager", "f_auto,q_auto:good",   // tạo HLS tự động
                "eager_async", true
        );

        Map result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        FileUpload fileUpload = FileUpload.builder()
                .fileName(result.get("public_id").toString())
                .fileType(result.get("resource_type").toString())
                .filePath(result.get("secure_url").toString())
                .folderName(result.get("folder").toString())
                .build();

        return fileUploadMapper.toFileUploadResponse(fileUploadRepository.save(fileUpload));
    }

    @Transactional
    public List<FileUploadResponse> uploadFile(FileUploadRequest request) {
        List<FileUpload> savedFiles = new ArrayList<>();
        List<String> uploadedPublicIds = new ArrayList<>();

        try {
            for (MultipartFile file : request.getFiles()) {

                validateFile(file);

                String originalName = file.getOriginalFilename();
                String baseName = extractBaseName(originalName);
                String publicId = reNameFile(baseName);

                Map<String, Object> options = Map.of(
                        "public_id", publicId,
                        "folder", request.getFolderName(),
                        "resource_type", "image",
                        "overwrite", false
                );

                Map uploadResult = uploadWithRetry(file, options);

                uploadedPublicIds.add(uploadResult.get("public_id").toString());

                FileUpload entity = FileUpload.builder()
                        .fileType(request.getFileType())
                        .fileName(uploadResult.get("public_id").toString())
                        .filePath(uploadResult.get("secure_url").toString())
                        .folderName(request.getFolderName())
                        .build();

                savedFiles.add(entity);
            }

            return fileUploadRepository.saveAll(savedFiles)
                    .stream()
                    .map(fileUploadMapper::toFileUploadResponse)
                    .toList();

        } catch (Exception e) {
            log.error("Upload failed, rolling back Cloudinary...", e);

            rollbackCloudinary(uploadedPublicIds);

            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    // ================= VALIDATE =================
    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    // ================= RETRY =================
    private Map uploadWithRetry(MultipartFile file, Map options) throws Exception {

        int attempt = 0;

        while (attempt < MAX_RETRY) {
            try {
                return doUpload(file, options);
            } catch (IOException e) {
                attempt++;
                log.warn("Upload attempt {} failed", attempt);

                if (attempt >= MAX_RETRY) {
                    throw e;
                }

                Thread.sleep(1000 * attempt); // exponential backoff
            }
        }

        throw new RuntimeException("Upload failed after retry");
    }

    // ================= REAL UPLOAD =================
    private Map doUpload(MultipartFile file, Map options) throws IOException {

        File tempFile = File.createTempFile("upload-", ".pdf");

        try {
            file.transferTo(tempFile);
            return cloudinary.uploader().upload(tempFile, options);
        } finally {
            tempFile.delete();
        }
    }

    // ================= ROLLBACK =================
    private void rollbackCloudinary(List<String> publicIds) {

        for (String publicId : publicIds) {
            try {
                cloudinary.uploader().destroy(publicId,
                        Map.of("resource_type", "raw"));
            } catch (Exception e) {
                log.error("Failed to rollback file: {}", publicId, e);
            }
        }
    }

    // ================= HELPER =================
    private String extractBaseName(String originalName) {
        if (originalName == null) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }

        int dotIndex = originalName.lastIndexOf(".");
        return (dotIndex != -1)
                ? originalName.substring(0, dotIndex)
                : originalName;
    }

    private String reNameFile(String baseName) {
        int count = fileUploadRepository.countByFileNameStartingWith(baseName);

        if (count == 0) {
            return baseName;
        }

        return baseName + "_" + (count + 1);
    }

    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là ảnh (jpg, png, webp)");
        }
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }
    }
}