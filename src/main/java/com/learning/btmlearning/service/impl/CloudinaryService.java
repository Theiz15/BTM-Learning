package com.learning.btmlearning.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder){
        validateImageFile(file);

        try {
            Map uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "transformation", new Transformation().width(800).height(800).crop("limit").fetchFormat("webp")
            );

            Map result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            String secureUrl = result.get("secure_url").toString() ;
            return secureUrl;
        } catch (IOException e) {
            throw new AppException(ErrorCode.CANNOT_UPLOAD_IMAGE);
        }
    }

    public Map<String, Object> uploadCourseThumbnail(MultipartFile file, Long courseId) throws IOException {
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
        return Map.of(
                "secure_url", result.get("secure_url"),
                "public_id", result.get("public_id")
        );
    }

    public Map<String, Object> uploadVideo(MultipartFile file, Long lessonId) throws IOException {
        if (!file.getContentType().startsWith("video/")) {
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
        return Map.of(
                "secure_url", result.get("secure_url"),     // link HLS m3u8
                "public_id", result.get("public_id"),
                "duration", result.get("duration")
        );
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