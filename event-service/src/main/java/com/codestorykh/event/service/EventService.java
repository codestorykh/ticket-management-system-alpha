package com.codestorykh.event.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.event.dto.EventRequest;

public interface EventService {

    ResponseErrorTemplate create(EventRequest request);
    ResponseErrorTemplate update(Long id, EventRequest request);
    ResponseErrorTemplate getById(Long id);
    void delete(Long id);
}
