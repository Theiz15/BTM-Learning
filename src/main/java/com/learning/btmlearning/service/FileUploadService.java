package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.FileUploadRequest;
import com.learning.btmlearning.dto.response.FileUploadResponse;

import java.util.List;

public interface FileUploadService {
    List<FileUploadResponse> uploadFile(FileUploadRequest request);
}
