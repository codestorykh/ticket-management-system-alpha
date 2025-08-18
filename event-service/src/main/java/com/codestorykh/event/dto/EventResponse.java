package com.codestorykh.event.dto;

import com.codestorykh.event.enumz.EventStatus;
import com.codestorykh.event.enumz.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;

    private String title;

    private String description;

    private String location;

    private LocalDateTime eventDate;

    private BigDecimal basePrice;

    private Integer capacity;

    private EventType eventType;

    private EventStatus status;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
