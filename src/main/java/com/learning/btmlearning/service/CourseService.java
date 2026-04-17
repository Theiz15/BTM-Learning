package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.CourseCreationRequest;
import com.learning.btmlearning.dto.response.CourseRatingSummaryResponse;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.entity.Category;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {
    private static final Pattern NON_LATIN = Pattern.compile("[^a-z0-9-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    CourseRepository courseRepository;
    CategoryService categoryService;
    CloudinaryService cloudinaryService;
    UserRepository userRepository;

    @NonFinal
    @Value("${cloudinary.folder.course-thumbnails:course-thumbnails}")
    String courseThumbnailsFolder = "course-thumbnails";

    @Transactional
    public CourseResponse createCourse(CourseCreationRequest request) {
        Category category = categoryService.findCategory(request.getCategoryId());
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setSlug(generateUniqueSlug(request.getTitle()));
        course.setDescription(request.getDescription());
        course.setCategory(category);
        course.setInstructor(instructor);
        course.setAverageRating(0.0);
        course.setRatingCount(0);

        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    @Transactional
    public CourseResponse updateThumbnail(Long courseId, String thumbnailUrl) {
        Course course = findCourse(courseId);
        course.setThumbnailUrl(thumbnailUrl);
        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    @Transactional
    public CourseResponse uploadThumbnail(Long courseId, MultipartFile file) {
        validateThumbnailFile(file);

        String thumbnailUrl = cloudinaryService.uploadImage(file, courseThumbnailsFolder);
        return updateThumbnail(courseId, thumbnailUrl);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateThumbnailFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.THUMBNAIL_INVALID_MIME_TYPE);
        }

        long maxBytes = 5L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new AppException(ErrorCode.THUMBNAIL_FILE_TOO_LARGE);
        }

        try {
            String detected = new Tika().detect(file.getInputStream());
            if (!isSupportedImageMime(detected)) {
                throw new AppException(ErrorCode.THUMBNAIL_INVALID_MIME_TYPE);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.THUMBNAIL_INVALID_MIME_TYPE);
        }
    }

    private boolean isSupportedImageMime(String mime) {
        if (mime == null) return false;
        return mime.equalsIgnoreCase("image/jpeg")
                || mime.equalsIgnoreCase("image/png")
                || mime.equalsIgnoreCase("image/webp");
    }

    @Transactional(readOnly = true)
    public CourseRatingSummaryResponse getCourseRating(Long courseId) {
        Course course = findCourse(courseId);
        return CourseRatingSummaryResponse.builder()
                .courseId(course.getId())
                .averageRating(course.getAverageRating() != null ? course.getAverageRating() : 0.0)
                .ratingCount(course.getRatingCount() != null ? course.getRatingCount() : 0)
                .build();
    }

    @Transactional
    public void updateCourseRating(Long courseId, double averageRating, long ratingCount) {
        Course course = findCourse(courseId);
        course.setAverageRating(averageRating);
        course.setRatingCount(Math.toIntExact(ratingCount));
        courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Course findCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .averageRating(course.getAverageRating() != null ? course.getAverageRating() : 0.0)
                .ratingCount(course.getRatingCount() != null ? course.getRatingCount() : 0)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int suffix = 1;

        while (courseRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix++;
        }

        return slug;
    }

    private String toSlug(String input) {
        String safe = input == null ? "" : input;
        String nowhitespace = WHITESPACE.matcher(safe.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}+", "");
        String slug = withoutDiacritics.toLowerCase(Locale.ROOT);
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = slug.replaceAll("-+", "-");
        slug = slug.replaceAll("^-+|-+$", "");
        return slug.isBlank() ? "course" : slug;
    }
}
