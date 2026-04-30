package com.learning.btmlearning.service;

import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.dto.request.NotificationBroadcastRequest;
import com.learning.btmlearning.dto.request.NotificationCreationRequest;
import com.learning.btmlearning.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(NotificationCreationRequest request);

    int broadcast(NotificationBroadcastRequest request) ;

    List<NotificationResponse> getMyNotifications() ;

    long getMyUnreadCount() ;

    List<NotificationResponse> getNotificationsByUser(Long userId) ;

    NotificationResponse markAsRead(Long notificationId) ;

    void notifyUser(Long userId, String title, String message, NotificationType type) ;
}
