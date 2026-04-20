package com.learning.btmlearning.controller;

import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.dto.request.ChangePasswordRequest;
import com.learning.btmlearning.dto.request.ChangeRoleRequest;
import com.learning.btmlearning.dto.request.UpdateProfileRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.UserAdminResponse;
import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.service.IUserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class UserController {
    IUserService userService;

    @GetMapping("/my-info")
    public ApiResponse<UserProfile> myInfo() {
        return ApiResponse.<UserProfile>builder()
                .message("get my information successfully")
                .result(userService.getUserProfile())
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfile> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        return ApiResponse.<UserProfile>builder()
                .message("Successfully updated profile")
                .result(userService.updateUserProfile(request))
                .build();
    }

    @PatchMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .message("Successfully changed password")
                .build();
    }

    @PostMapping("/avatar")
    public ApiResponse<UserProfile> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<UserProfile>builder()
                .message("Successfully uploaded avatar")
                .result(userService.uploadAvatar(file))
                .build();
    }

    @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<UserAdminResponse>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ApiResponse.<Page<UserAdminResponse>>builder()
                .message("Get all users successfully")
                .result(userService.getAllUsers(role, isActive, keyword, pageable))
                .build();
    }

    @PatchMapping("/{id}/toggle-active")
        @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> toggleActive(@PathVariable Long id) {
        userService.toggleActive(id);
        return ApiResponse.<Void>builder()
                .message("Updated active user")
                .build();
    }

    @PatchMapping("/{id}/role")
        @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> changeRole(
            @PathVariable Long id,
            @RequestBody @Valid ChangeRoleRequest request) {
        userService.changeRole(id, request);
        return ApiResponse.<Void>builder()
                .message("Change role successfully")
                .build();
    }
}
