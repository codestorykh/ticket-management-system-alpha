package com.codestorykh.ticket.repository;

import com.codestorykh.ticket.entity.Ticket;
import com.codestorykh.ticket.enumz.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByEventId(Long eventId);

    List<Ticket> findAllByEventIdAndTicketStatus(Long eventId, TicketStatus status);

    Optional<Ticket> findFirstBySeatNumberAndEventId(String seatNumber, Long eventId);
}
