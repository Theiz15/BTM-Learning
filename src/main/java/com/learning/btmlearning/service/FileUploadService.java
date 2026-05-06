package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.response.PdfContentResponse;
import com.learning.btmlearning.dto.response.UploadDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {
    UploadDocumentResponse uploadPdf(MultipartFile file) throws IOException;
    PdfContentResponse readPdf(String fileId);
    String readPdfPage(String fileId, int pageNumber);
}
