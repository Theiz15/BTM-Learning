package com.learning.btmlearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentResponse {
    private String fileName;
    private String fileId;
    private long fileSize;
    private int totalPages;
}
