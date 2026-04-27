package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.dto.request.ChangePasswordRequest;
import com.learning.btmlearning.dto.request.ChangeRoleRequest;
import com.learning.btmlearning.dto.request.UpdateProfileRequest;
import com.learning.btmlearning.dto.response.UserAdminResponse;
import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.UserMapper;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.IUserService;
import com.learning.btmlearning.utils.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserServiceImpl implements IUserService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserMapper userMapper;
    CloudinaryService cloudinaryService;
    RedisTemplate<String, Object> redisTemplate;
    SecurityUtil securityUtil;

    @Override
    public UserProfile getUserProfile() {
        User user = securityUtil.getCurrentUser();
        return userMapper.toUserProfile(user);
    }

    @Override
    public UserProfile updateUserProfile(UpdateProfileRequest request) {
        User user = securityUtil.getCurrentUser();

        user.setBio(request.getBio());
        user.setFullName(request.getFullName());
        userRepository.save(user);
        return userMapper.toUserProfile(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = securityUtil.getCurrentUser();

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
        User user = securityUtil.getCurrentUser();

        String imageUrl = cloudinaryService.uploadImage(file,"btm-learning/avatars") ;
        user.setAvatarUrl(imageUrl);
        userRepository.save(user);
        return userMapper.toUserProfile(user);
    }

    @Override
    public UserProfile registerAsInstructor() {
        User user = securityUtil.getCurrentUser();

        if (user.getRole() == UserRole.INSTRUCTOR || user.getRole() == UserRole.ADMIN) {
            throw new AppException(ErrorCode.USER_ALREADY_INSTRUCTOR);
        }

        user.setRole(UserRole.INSTRUCTOR);
        userRepository.save(user);

        // Force re-login so client receives a JWT containing the new role claim.
        redisTemplate.delete("refresh:" + user.getId());

        log.info(">>> User {} upgraded role to INSTRUCTOR", user.getEmail());
        return userMapper.toUserProfile(user);
    }

    @Override
    public UserProfile getUserCourses() {
        return null;
    }

    @Override
    public Page<UserAdminResponse> getAllUsers(UserRole role, Boolean isActive, String keyword, Pageable pageable) {

        Page<User> users = userRepository.searchUsers(role,isActive,keyword,pageable);

        return users.map(userMapper::toAdminResponse);
    }

    @Override
    public void toggleActive(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        if (!user.getIsActive()){
            redisTemplate.delete("refresh"+id);
        }
    }

    @Override
    public UserProfile changeRole(Long id, ChangeRoleRequest rq) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        Long userId = securityUtil.getCurrentUser().getId();
        if (id.equals(userId)){
            throw new AppException(ErrorCode.CANNOT_CHANGE_ROLE);
        }

        user.setRole(rq.getRole());
        userRepository.save(user);
        log.info(">>> Admin {} đã đổi quyền user {} thành {}", userId, user.getEmail(), rq.getRole());
        return userMapper.toUserProfile(user);
    }
}
