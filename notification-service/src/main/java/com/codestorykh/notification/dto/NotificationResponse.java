package com.codestorykh.notification.dto;

import com.codestorykh.notification.enumz.NotificationStatus;
import com.codestorykh.notification.enumz.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String username;
    private Long orderId;
    private String eventType;
    private NotificationType notificationType;
    private String recipient;
    private String subject;
    private String message;
    private NotificationStatus status;
}
