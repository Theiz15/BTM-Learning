package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.ChangePasswordRequest;
import com.learning.btmlearning.dto.request.UpdateProfileRequest;
import com.learning.btmlearning.dto.response.UserProfile;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    UserProfile getUserProfile();
    UserProfile updateUserProfile(UpdateProfileRequest request);
    void changePassword( ChangePasswordRequest request);
    UserProfile uploadAvatar(MultipartFile file);
    UserProfile getUserCourses();
}
