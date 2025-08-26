package com.codestorykh.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class EmailService {

    public boolean sendEmail(String to, String subject, String body) {
       try{
           if(to.contains("invalid") || to.contains("fail")) {
              log.error("Invalid email address: {}", to);
              return false;
           }

           log.info("Successfully sent email to {}: Subject: {}, Body: {}", to, subject, body);
              return true;
       }catch (Exception e) {
           log.error("Failed to send email to {}: {}", to, e.getMessage());
           return false;
       }
    }

    public String generateOrderConfirmationEmailBody(String username, String eventTitle, LocalDateTime eventDate,
                                                     String location, Integer quantity, BigDecimal amount) {

        return String.format("""
                Dear %s,
                
                Your ticket order has been confirmed!
                
                Event Details:
                - Event: %s
                - Date: %s
                - Location: %s
                - Quantity: %d
                - Total Amount: $%.2f
                
                Your tickets will be sent to you shortly.
                
                Thank you for choosing our service!
                
                Best regards,
                Ticket Management Team
                """, username, eventTitle, eventDate.toString(), location, quantity, amount);
    }
}
