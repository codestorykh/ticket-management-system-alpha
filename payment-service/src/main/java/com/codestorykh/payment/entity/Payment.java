package com.codestorykh.payment.entity;

import com.codestorykh.common.entity.BaseEntity;
import com.codestorykh.payment.enumz.PaymentMethod;
import com.codestorykh.payment.enumz.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tt_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "description")
    private String description;
}
