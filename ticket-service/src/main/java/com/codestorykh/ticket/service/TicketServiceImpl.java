package com.codestorykh.ticket.service;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.ticket.client.EventClient;
import com.codestorykh.ticket.dto.TicketLockRequest;
import com.codestorykh.ticket.dto.TicketRequest;
import com.codestorykh.ticket.dto.TicketResponse;
import com.codestorykh.ticket.entity.Ticket;
import com.codestorykh.ticket.enumz.TicketStatus;
import com.codestorykh.ticket.mapper.TicketMapper;
import com.codestorykh.ticket.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService{

    private final RedisTemplate<String, String> redisTemplate;
    private final TicketMapper ticketMapper;
    private final EventClient eventClient;
    private final TicketRepository ticketRepository;

    public TicketServiceImpl(RedisTemplate<String, String> redisTemplate,
                             TicketMapper ticketMapper, EventClient eventClient,
                             TicketRepository ticketRepository) {
        this.redisTemplate = redisTemplate;
        this.ticketMapper = ticketMapper;
        this.eventClient = eventClient;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public ResponseErrorTemplate createTicket(TicketRequest ticketRequest) {

        Optional<Ticket> ticketInDb = ticketRepository.findFirstBySeatNumberAndEventId(
                ticketRequest.getSeatNumber(), ticketRequest.getEventId());
        if(ticketInDb.isPresent()) {
            return new ResponseErrorTemplate(
                    ApiConstant.SEAT_NUMBER_ALREADY_EXISTS.getDescription(),
                    ApiConstant.SEAT_NUMBER_ALREADY_EXISTS.getKey(),
                    new EmptyObject(),
                    true);
        }

        var eventResponse = eventClient.getEventById(ticketRequest.getEventId());
        if(eventResponse == null || eventResponse.isError() || "404".equalsIgnoreCase(eventResponse.code())) {
            return new ResponseErrorTemplate(
                    ApiConstant.EVENT_NOT_FOUND.getDescription(),
                    ApiConstant.EVENT_NOT_FOUND.getKey(),
                    new EmptyObject(),
                    true);
        }

        Ticket ticket = ticketMapper.toEntity(ticketRequest);
        ticket.setEventId(ticketRequest.getEventId());
        ticketRepository.save(ticket);

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                ticketMapper.toResponse(ticket),
                false
        );
    }

    @Override
    public ResponseErrorTemplate getTicketById(Long ticketId) {
       Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        return ticket.map(value -> new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                ticketMapper.toResponse(value),
                false
        )).orElseGet(() -> new ResponseErrorTemplate(
                ApiConstant.TICKET_NOT_FOUND.getDescription(),
                ApiConstant.TICKET_NOT_FOUND.getKey(),
                new EmptyObject(),
                true
        ));

    }

    @Override
    public ResponseErrorTemplate lockTicket(TicketLockRequest ticketLockRequest) {

        var lockKey = "ticket:lock:" + ticketLockRequest.getEventId()  + ticketLockRequest.getUserId();
        String lockValue = ticketLockRequest.getEventId()+"_"+ UUID.randomUUID();

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, Duration.ofMinutes(ticketLockRequest.getLockDuration()));
        if(Boolean.FALSE.equals(lockAcquired)) {
            return new ResponseErrorTemplate(
                    ApiConstant.TICKET_LOCKED.getDescription(),
                    ApiConstant.TICKET_LOCKED.getKey(),
                    new EmptyObject(),
                    true
            );
        }

        try {
            List<Ticket> availableTickets = ticketRepository.findAllByEventIdAndTicketStatus(
                    ticketLockRequest.getEventId(), TicketStatus.AVAILABLE);
            if (availableTickets.size() < ticketLockRequest.getQuantity()) {
                return new ResponseErrorTemplate(
                        ApiConstant.TICKET_NOT_AVAILABLE.getDescription(),
                        ApiConstant.TICKET_NOT_AVAILABLE.getKey(),
                        new EmptyObject(),
                        true
                );
            }
            List<Ticket> ticketsToLock = availableTickets.subList(0, ticketLockRequest.getQuantity());
            ticketsToLock.forEach(
                    ticket -> {
                        ticket.setTicketStatus(TicketStatus.LOCKED);
                        ticket.setLockedBy(ticketLockRequest.getUserId());
                        ticket.setLockedUntil(LocalDateTime.now().plusMinutes(ticketLockRequest.getLockDuration()));
                    }
            );

            List<TicketResponse> tickets = ticketRepository.saveAll(ticketsToLock).stream()
                    .map(ticketMapper::toResponse)
                    .toList();
            return new ResponseErrorTemplate(
                    ApiConstant.SUCCESS.getDescription(),
                    ApiConstant.SUCCESS.getKey(),
                    tickets,
                    false
            );
        }finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public void unlockTicket(Long eventId, Integer quantity) {
        List<Ticket> lockedTickets = ticketRepository.findAllByEventIdAndTicketStatus(
                eventId, TicketStatus.LOCKED
        );

        if (lockedTickets.size() < quantity) {
            quantity = lockedTickets.size();
        }

        List<Ticket> ticketsToUnlock = lockedTickets.subList(0, quantity);

        ticketsToUnlock.forEach(
                ticket -> {
                    ticket.setTicketStatus(TicketStatus.AVAILABLE);
                    ticket.setLockedUntil(null);
                    ticket.setLockedBy(null);
                }
        );

        ticketRepository.saveAll(ticketsToUnlock);
    }
}
