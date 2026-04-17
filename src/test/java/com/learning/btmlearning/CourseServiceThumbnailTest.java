package com.learning.btmlearning;

import com.learning.btmlearning.entity.Category;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.CategoryService;
import com.learning.btmlearning.service.CloudinaryService;
import com.learning.btmlearning.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceThumbnailTest {

    @Mock
    CourseRepository courseRepository;

    @Mock
    CategoryService categoryService;

    @Mock
    CloudinaryService cloudinaryService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CourseService courseService;

    @Test
    void uploadThumbnail_validPng_uploadsAndUpdatesCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("t");

        Category category = new Category();
        category.setId(10);
        category.setName("cat");
        course.setCategory(category);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cloudinaryService.uploadImage(any(), anyString())).thenReturn("https://cdn.example/thumb.png");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "thumb.png",
                "image/png",
                minimalPngBytes()
        );

        var resp = courseService.uploadThumbnail(1L, file);
        assertEquals("https://cdn.example/thumb.png", resp.getThumbnailUrl());
        verify(cloudinaryService).uploadImage(any(), anyString());
    }

    @Test
    void uploadThumbnail_invalidType_throws() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("t");

        Category category = new Category();
        category.setId(10);
        category.setName("cat");
        course.setCategory(category);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "thumb.txt",
                "text/plain",
                "hello".getBytes()
        );

        AppException ex = assertThrows(AppException.class, () -> courseService.uploadThumbnail(1L, file));
        assertEquals(ErrorCode.THUMBNAIL_INVALID_MIME_TYPE, ex.getErrorCode());
        verifyNoInteractions(courseRepository, cloudinaryService);
    }

    private byte[] minimalPngBytes() {
        // 1x1 transparent PNG
        return new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
                (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41,
                0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
                0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4,
                0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
    }
}
