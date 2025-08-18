package com.codestorykh.ticket.mapper;

import com.codestorykh.ticket.dto.TicketRequest;
import com.codestorykh.ticket.dto.TicketResponse;
import com.codestorykh.ticket.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Ticket toEntity(TicketRequest request);

    TicketResponse toResponse(Ticket ticket);
}
