package com.codestorykh.order.client;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.order.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Value("${service.payment.url}")
    private String paymentServiceUrl;

    public Mono<ResponseErrorTemplate> processingPayment(PaymentRequest paymentRequest) {
        return webClient.post()
                .uri(paymentServiceUrl + "/pay")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(ResponseErrorTemplate.class)
                .map(response -> {
                    log.debug("Payment response: {}", response);
                    return response;
                })
                .onErrorResume(throwable -> {
                    log.error("Error calling payment service for pay: {}", throwable.getMessage());
                    return Mono.just(new ResponseErrorTemplate("Payment service error", "PAYMENT_SERVICE_ERROR", null, true));
                });
    }
}
