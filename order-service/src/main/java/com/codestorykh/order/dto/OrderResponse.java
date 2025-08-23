package com.codestorykh.order.dto;

import com.codestorykh.order.enumz.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {

    private Long eventId;
    private Long ticketId;
    private Integer quantity;
    private BigDecimal amount;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private String paymentId;
}
