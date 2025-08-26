package com.codestorykh.payment.dto;

import com.codestorykh.payment.enumz.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
}
