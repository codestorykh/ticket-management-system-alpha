package com.codestorykh.order.dto;

import com.codestorykh.order.enumz.PaymentMethod;
import com.codestorykh.order.enumz.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private Long orderId;

    private String username;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    private String currency;

    private String description;
}
