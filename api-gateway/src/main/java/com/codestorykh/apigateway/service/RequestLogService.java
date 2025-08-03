package com.codestorykh.apigateway.service;

import com.codestorykh.apigateway.entity.RequestLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface RequestLogService {

    Mono<RequestLog> save(RequestLog requestLog);

} 