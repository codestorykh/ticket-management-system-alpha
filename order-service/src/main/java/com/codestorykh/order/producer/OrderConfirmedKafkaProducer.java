package com.codestorykh.order.producer;

import com.codestorykh.order.dto.OrderConfirmedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConfirmedKafkaProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderConfirmedKafkaProducer(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${notification.topic.order-confirmed}")
    private String notificationTopic;

    public void send(OrderConfirmedEvent orderConfirmedEvent) {
        try{
            kafkaTemplate.send(notificationTopic, objectMapper.writeValueAsString(orderConfirmedEvent));
            log.info("Sent order confirmed event: {}", orderConfirmedEvent);
        } catch (Exception e) {
            log.error("Failed to send order confirmed event: {}", orderConfirmedEvent, e);
        }
    }

}
