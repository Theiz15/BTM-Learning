package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.ChangePasswordRequest;
import com.learning.btmlearning.dto.request.UpdateProfileRequest;
import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.UserMapper;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserServiceImpl implements IUserService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserMapper userMapper;
    CloudinaryService cloudinaryService;

    @Override
    public UserProfile getUserProfile() {
        User user = getCurrentUser();
        return userMapper.toUserProfile(user);
    }

    @Override
    public UserProfile updateUserProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        user.setFullName(request.getFullName());
        userRepository.save(user);
        return userMapper.toUserProfile(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (user.getPasswordHash() == null){
            throw new AppException(ErrorCode.CANNOT_CHANGE_PASSWORD);
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MERGE);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserProfile uploadAvatar(MultipartFile file) {
        User user = getCurrentUser();

        String imageUrl = cloudinaryService.uploadImage(file,"btm-learning/avatars") ;
        user.setAvatarUrl(imageUrl);
        userRepository.save(user);
        return userMapper.toUserProfile(user);
    }

    @Override
    public UserProfile getUserCourses() {
        return null;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
