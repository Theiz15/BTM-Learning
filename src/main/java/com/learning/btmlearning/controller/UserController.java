package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.ChangePasswordRequest;
import com.learning.btmlearning.dto.request.UpdateProfileRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.service.IUserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
}
