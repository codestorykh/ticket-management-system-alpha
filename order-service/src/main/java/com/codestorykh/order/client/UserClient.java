package com.codestorykh.order.client;

import com.codestorykh.order.dto.TokenVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Value("${service.user.url:http://localhost:8081}")
    private String userServiceUrl;

    public Mono<TokenVerificationResponse> verifyToken(String token) {
        return webClient.post()
                .uri(userServiceUrl + "/api/public/users/verify-token")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokenVerificationResponse.class)
                .map(response -> {
                    log.debug("Token verification response: {}", response);
                    return response;
                })
                .onErrorResume(throwable -> {
                    log.error("Error calling user service for token verification: {}", throwable.getMessage());
                    return Mono.just(new TokenVerificationResponse());
                });
    }
}
