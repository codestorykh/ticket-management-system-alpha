package com.codestorykh.notification.dto;

import com.codestorykh.notification.enumz.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String username;
    private String email;
    private Long orderId;
    private String eventType;
    private NotificationType notificationType;
    private String recipient;
    private String subject;
    private String message;
}
