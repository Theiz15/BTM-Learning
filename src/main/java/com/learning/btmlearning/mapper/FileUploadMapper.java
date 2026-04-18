package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.FileUploadResponse;
import com.learning.btmlearning.entity.FileUpload;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface FileUploadMapper {
    FileUploadResponse toFileUploadResponse(FileUpload fileUpload);
}
