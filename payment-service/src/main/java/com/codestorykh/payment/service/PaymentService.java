package com.codestorykh.payment.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.payment.dto.PaymentRequest;

public interface PaymentService {    // get by transaction id
    // get by payment id

    // refund payment

    ResponseErrorTemplate processPayment(PaymentRequest paymentRequest);


}
