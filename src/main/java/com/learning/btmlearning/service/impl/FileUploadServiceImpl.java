package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.response.PdfContentResponse;
import com.learning.btmlearning.dto.response.UploadDocumentResponse;
import com.learning.btmlearning.entity.FileUpload;
import com.learning.btmlearning.repository.FileUploadRepository;
import com.learning.btmlearning.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    private final FileUploadRepository fileUploadRepository;
    private final String UPLOAD_DIR = "uploads";

    public UploadDocumentResponse uploadPdf(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must be not empty");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF (.pdf) are accepted!");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileId = UUID.randomUUID() + "_" + originalName;
        Path destination = uploadPath.resolve(fileId);

        file.transferTo(destination.toFile());
        log.info("Save file: {}", destination.toAbsolutePath());

        int totalPages = 0;
        try (PDDocument doc = Loader.loadPDF(destination.toFile())) {
            totalPages = doc.getNumberOfPages();
        }

        FileUpload fileUpload = FileUpload.builder()
                .fileType("pdf")
                .folderName(UPLOAD_DIR)
                .filePath(destination.toAbsolutePath().toString())
                .build();

        fileUploadRepository.save(fileUpload);

        return UploadDocumentResponse.builder()
                .fileName(originalName)
                .fileId(fileId)
                .fileSize(file.getSize())
                .totalPages(totalPages)
                .build();
    }


    public PdfContentResponse readPdf(String fileId) {
        File pdfFile = resolvePdfFile(fileId);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDDocumentInformation info = document.getDocumentInformation();
            int totalPages = document.getNumberOfPages();

            PDFTextStripper stripper = new PDFTextStripper();
            Map<Integer, String> pageContents = new LinkedHashMap<>();
            StringBuilder fullText = new StringBuilder();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document);
                pageContents.put(page, pageText.trim());
                fullText.append(pageText);
            }

            return PdfContentResponse.builder()
                    .fileName(fileId.contains("_") ? fileId.substring(fileId.indexOf("_") + 1) : fileId)
                    .totalPages(totalPages)
                    .author(info.getAuthor())
                    .title(info.getTitle())
                    .subject(info.getSubject())
                    .creator(info.getCreator())
                    .fullText(fullText.toString().trim())
                    .pageContents(pageContents)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String readPdfPage(String fileId, int pageNumber) {
        File pdfFile = resolvePdfFile(fileId);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int totalPages = document.getNumberOfPages();

            if (pageNumber < 1 || pageNumber > totalPages) {
                throw new IllegalArgumentException(
                        "Invalid page count.The file has " + totalPages + " pages (1 - " + totalPages + ")"
                );
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            return stripper.getText(document).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File resolvePdfFile(String fileId) {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileId);
        File pdfFile = filePath.toFile();

        if (!pdfFile.exists()) {
            throw new IllegalArgumentException("File with ID not found: " + fileId);
        }
        return pdfFile;
    }
}

