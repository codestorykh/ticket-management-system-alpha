package com.codestorykh.notification.service;

import com.codestorykh.notification.dto.NotificationRequest;
import com.codestorykh.notification.dto.NotificationResponse;
import com.codestorykh.notification.dto.OrderConfirmedEvent;

public interface NotificationService {

     void handlerOrderConfirmationEvent(OrderConfirmedEvent confirmedEvent);

     NotificationResponse sendNotification(NotificationRequest notificationRequest);
}
