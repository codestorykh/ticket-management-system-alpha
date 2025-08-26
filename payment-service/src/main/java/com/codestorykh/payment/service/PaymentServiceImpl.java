package com.codestorykh.payment.service;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.payment.dto.PaymentRequest;
import com.codestorykh.payment.dto.PaymentResponse;
import com.codestorykh.payment.entity.Payment;
import com.codestorykh.payment.enumz.PaymentStatus;
import com.codestorykh.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayService paymentGatewayService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentGatewayService paymentGatewayService) {
        this.paymentRepository = paymentRepository;
        this.paymentGatewayService = paymentGatewayService;
    }

    @Override
    public ResponseErrorTemplate processPayment(PaymentRequest paymentRequest) {
        Payment payment = mapToPayment(paymentRequest);
        payment.setPaymentStatus(PaymentStatus.FAILED);

        boolean paymentSuccess = paymentGatewayService.processPayment(paymentRequest);

        if(paymentSuccess) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(paymentRequest.getUsername()+"_"+ UUID.randomUUID());
        }

        paymentRepository.save(payment);

        PaymentResponse paymentResponse = mapToPaymentResponse(payment);
        paymentResponse.setTransactionId(payment.getTransactionId());
        paymentResponse.setPaymentId(payment.getId());
        paymentResponse.setOrderId(paymentRequest.getOrderId());

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                paymentResponse,
                false);
    }


    private Payment mapToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .username(paymentRequest.getUsername())
                .orderId(paymentRequest.getOrderId())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentStatus(paymentRequest.getPaymentStatus())
                .description(paymentRequest.getDescription())
                .paymentDate(LocalDateTime.now())
                .build();
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
