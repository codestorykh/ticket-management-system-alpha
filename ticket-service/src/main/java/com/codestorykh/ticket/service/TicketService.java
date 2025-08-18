package com.codestorykh.ticket.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.ticket.dto.TicketLockRequest;
import com.codestorykh.ticket.dto.TicketRequest;
import com.codestorykh.ticket.dto.TicketResponse;

public interface TicketService {

    ResponseErrorTemplate createTicket(TicketRequest ticketRequest);

    ResponseErrorTemplate getTicketById(Long ticketId);

    ResponseErrorTemplate lockTicket(TicketLockRequest ticketLockRequest);

    void unlockTicket(Long eventId, Integer quantity);

}
