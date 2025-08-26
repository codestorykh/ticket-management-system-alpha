package com.codestorykh.payment.service;

import com.codestorykh.payment.dto.PaymentRequest;

public interface PaymentGatewayService {

    boolean processPayment(PaymentRequest paymentRequest);
}
