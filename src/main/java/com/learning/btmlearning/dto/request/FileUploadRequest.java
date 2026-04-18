package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class FileUploadRequest {
    @NotNull(message = "File type must be not null")
    private String fileType;

    @NotBlank(message = "Folder name must be not null")
    private String folderName;

    List<MultipartFile> files = new ArrayList<>();
}
