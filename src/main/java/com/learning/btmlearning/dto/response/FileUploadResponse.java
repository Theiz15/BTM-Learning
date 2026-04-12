package com.learning.btmlearning.dto.response;

import lombok.Data;

@Data
public class FileUploadResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
    private String folderName;
}
