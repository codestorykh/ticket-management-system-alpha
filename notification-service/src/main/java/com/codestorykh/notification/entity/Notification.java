package com.codestorykh.notification.entity;

import com.codestorykh.common.entity.BaseEntity;
import com.codestorykh.notification.enumz.NotificationStatus;
import com.codestorykh.notification.enumz.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tt_notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private Long orderId;
    private String eventType;
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    private String recipient;

    private String subject;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_status")
    private NotificationStatus status;

    private String errorMessage;
}
