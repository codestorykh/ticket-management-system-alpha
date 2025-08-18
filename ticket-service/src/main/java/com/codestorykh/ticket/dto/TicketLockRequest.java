package com.codestorykh.ticket.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class TicketLockRequest {

    private Long eventId;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;

    private String userId;

    @Min(value = 1, message = "Lock duration must be at least 1 minute")
    @Max(value = 30, message = "Lock duration cannot exceed 30 minutes")
    private Integer lockDuration = 15; // Default to 15 minutes
}
