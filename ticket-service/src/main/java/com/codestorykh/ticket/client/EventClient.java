package com.codestorykh.ticket.client;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EventClient {

    private final WebClient webClient;

    public EventClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Value("${event.service.url}")
    private String eventServiceUrl;

    public ResponseErrorTemplate getEventById(Long eventId) {
        return webClient.get()
                .uri(eventServiceUrl+"/{id}", eventId)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(ResponseErrorTemplate.class)
                .block();
    }
}
