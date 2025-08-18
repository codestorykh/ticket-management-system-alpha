package com.codestorykh.ticket.controller;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.ticket.dto.TicketLockRequest;
import com.codestorykh.ticket.dto.TicketRequest;
import com.codestorykh.ticket.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping()
    public ResponseEntity<ResponseErrorTemplate> create(@RequestBody TicketRequest ticketRequest) {
        log.info("Intercept create new ticket with req: {}", ticketRequest);
        return ResponseEntity.ok(ticketService.createTicket(ticketRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> getById(@PathVariable Long id) {
        log.info("Intercept get ticket by id: {}", id);
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping("lock")
    public ResponseEntity<ResponseErrorTemplate> lockTicket(@RequestBody TicketLockRequest ticketLockRequest) {
        log.info("Intercept lock by event id: {}", ticketLockRequest.getEventId());
        return ResponseEntity.ok(ticketService.lockTicket(ticketLockRequest));
    }

    @GetMapping("unlock/{eventId}/{quantity}")
    public ResponseEntity<Void> unlockTicket(@PathVariable Long eventId, @PathVariable Integer quantity) {
        log.info("Intercept unlock by event id: {}, quantity: {}", eventId, quantity);
        ticketService.unlockTicket(eventId, quantity);
        return ResponseEntity.ok().build();
    }
}
