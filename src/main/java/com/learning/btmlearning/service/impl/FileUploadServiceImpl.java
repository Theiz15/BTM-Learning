package com.learning.btmlearning.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.learning.btmlearning.dto.request.FileUploadRequest;
import com.learning.btmlearning.dto.response.FileUploadResponse;
import com.learning.btmlearning.entity.FileUpload;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.FileUploadMapper;
import com.learning.btmlearning.repository.FileUploadRepository;
import com.learning.btmlearning.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadServiceImpl implements FileUploadService {
    private final FileUploadRepository fileUploadRepository;
    private final FileUploadMapper fileUploadMapper;
    private final Cloudinary cloudinary;

    @Override
    public List<FileUploadResponse> uploadFile(FileUploadRequest request) {

        List<FileUpload> files = new ArrayList<>();

        for (MultipartFile file : request.getFiles()) {
            try {
                if (file.isEmpty()) {
                    throw new AppException(ErrorCode.FILE_EMPTY);
                }

                String originalName = file.getOriginalFilename();
                int dotIndex = Objects.requireNonNull(originalName).lastIndexOf(".");
                String baseName  = (dotIndex != -1) ? originalName.substring(0, dotIndex) : originalName;

                String publicId = reNameFile(baseName);

                Map<?, ?> options = ObjectUtils.asMap(
                        "public_id", publicId,
                        "folder", request.getFolderName(),
                        "resource_type", "auto",
                        "overwrite", false
                );

                Map<?, ?> result = cloudinary.uploader()
                        .upload(file.getBytes(), options);

                FileUpload fileUpload = FileUpload.builder()
                        .fileType(request.getFileType())
                        .fileName(result.get("public_id").toString())
                        .filePath(result.get("secure_url").toString())
                        .folderName(request.getFolderName())
                        .build();

                files.add(fileUpload);

            } catch (IOException e) {
                log.error("Upload failed: {}", e.getMessage(), e);
                throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
            }
        }

        return fileUploadRepository.saveAll(files).stream()
                .map(fileUploadMapper::toFileUploadResponse)
                .collect(Collectors.toList());
    }

    private String reNameFile(String baseName) {
        int count = fileUploadRepository.countByFileNameStartingWith(baseName);

        if (count == 0) {
            return baseName;
        }

        return baseName + "_" + (count + 1);
    }
}
