package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.dto.request.NotificationCreationRequest;
import com.learning.btmlearning.dto.request.NotificationBroadcastRequest;
import com.learning.btmlearning.dto.response.NotificationResponse;
import com.learning.btmlearning.entity.Notification;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.NotificationRepository;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.NotificationService;
import com.learning.btmlearning.utils.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    JavaMailSender mailSender;
    SecurityUtil securityUtil;

    @Override
    public NotificationResponse createNotification(NotificationCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Notification notification = saveNotification(user, request.getTitle(), request.getMessage(), request.getType());

        return mapToResponse(notification);
    }

    @Override
    public int broadcast(NotificationBroadcastRequest request) {
        List<User> recipients = request.getRole() == null
                ? userRepository.findByIsActiveTrue()
                : userRepository.findByRoleAndIsActiveTrue(request.getRole());

        for (User user : recipients) {
            saveNotification(user, request.getTitle(), request.getMessage(), request.getType());
        }

        return recipients.size();
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = securityUtil.getCurrentUser();
        return getNotificationsByUser(currentUser.getId());
    }

    @Override
    public long getMyUnreadCount() {
        User currentUser = securityUtil.getCurrentUser();
        return notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
    }

    @Override
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        User currentUser = securityUtil.getCurrentUser();
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isAdmin && !currentUser.getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        User currentUser = securityUtil.getCurrentUser();
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isAdmin && !notification.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    @Override
    public void notifyUser(Long userId, String title, String message, NotificationType type) {
        NotificationCreationRequest request = NotificationCreationRequest.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .build();
        createNotification(request);
    }

    private Notification saveNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        notification = notificationRepository.save(notification);
        sendNotificationEmail(user.getEmail(), user.getFullName(), title, message, type);
        return notification;
    }

    private void sendNotificationEmail(String toEmail, String fullName, String title, String message, NotificationType type) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(buildSubject(type, title));
        email.setText(buildBody(fullName, title, message, type));
        mailSender.send(email);
    }

    private String buildSubject(NotificationType type, String title) {
        return switch (type) {
            case ENROLLMENT_CONFIRMED -> "[BTM Learning] Enrollment Confirmation";
            case CERTIFICATE_ISSUED -> "[BTM Learning] Your Certificate Is Ready";
            default -> "[BTM Learning] " + title;
        };
    }

    private String buildBody(String fullName, String title, String message, NotificationType type) {
        String recipient = (fullName == null || fullName.isBlank()) ? "Learner" : fullName;
        String intro = switch (type) {
            case ENROLLMENT_CONFIRMED -> "Thank you for enrolling with BTM Learning. Your seat has been confirmed successfully.";
            case CERTIFICATE_ISSUED -> "Congratulations on your achievement. Your course certificate is now available.";
            default -> "You have a new update from BTM Learning.";
        };

        return "Hello " + recipient + ",\n\n"
                + intro + "\n\n"
                + "Subject: " + title + "\n"
                + message + "\n\n"
                + "If you need any assistance, please contact our support team.\n\n"
                + "Best regards,\n"
                + "BTM Learning Team";
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
