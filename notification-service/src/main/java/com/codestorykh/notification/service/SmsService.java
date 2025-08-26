package com.codestorykh.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SmsService {

    public boolean sendSms(String phoneNumber, String message) {
      try{
          // Simulate sending SMS
          log.info("Sending SMS to {}: {}", phoneNumber, message);
          return StringUtils.hasText(message) && StringUtils.hasText(phoneNumber); // Assume SMS is sent successfully
      }catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
      }
    }

    public String generateOrderConfirmationSmsBody(String eventTitle, LocalDateTime eventDate,
                                                   String eventLocation, Integer quantity){

        return String.format("""
                Your ticket order for '%s' on %s at %s has been confirmed. Quantity: %d. Thank you for choosing our service!
                """, eventTitle, eventDate.toString(), eventLocation, quantity);
    }

}
