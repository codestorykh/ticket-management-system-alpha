package com.codestorykh.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {

    private Long eventId;
    private Long ticketId;
    private Integer quantity;
    private BigDecimal amount;
}
