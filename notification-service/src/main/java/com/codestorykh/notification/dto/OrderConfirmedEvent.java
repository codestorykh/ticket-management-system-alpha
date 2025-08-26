package com.codestorykh.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmedEvent {

    private Long orderId;
    private String username;
    private String email;
    private String phoneNumber;
    private String eventTitle;
    private Integer quantity;
    private BigDecimal amount;
    private String eventLocation;
    private LocalDateTime eventDate;
    private LocalDateTime orderDate;
}
