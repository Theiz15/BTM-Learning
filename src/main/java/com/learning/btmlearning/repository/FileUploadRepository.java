package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    int countByFileNameStartingWith(String baseName);
}
