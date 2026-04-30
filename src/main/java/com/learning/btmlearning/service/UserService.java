package com.learning.btmlearning.service;

import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.dto.request.ChangePasswordRequest;
import com.learning.btmlearning.dto.request.ChangeRoleRequest;
import com.learning.btmlearning.dto.request.UpdateProfileRequest;
import com.learning.btmlearning.dto.response.UserAdminResponse;
import com.learning.btmlearning.dto.response.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserProfile getUserProfile();
    UserProfile updateUserProfile(UpdateProfileRequest request);
    void changePassword( ChangePasswordRequest request);
    UserProfile uploadAvatar(MultipartFile file);
    UserProfile registerAsInstructor();
    UserProfile getUserCourses();
    Page<UserAdminResponse> getAllUsers(UserRole role,
                                        Boolean isActive,
                                        String keyword,
                                        Pageable pageable);
    void toggleActive(Long id);
    UserProfile changeRole(Long id , ChangeRoleRequest rq);
}