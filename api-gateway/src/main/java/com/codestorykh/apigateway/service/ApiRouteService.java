package com.codestorykh.apigateway.service;

import com.codestorykh.apigateway.dto.RouteApiRequest;
import com.codestorykh.apigateway.dto.RouteApiResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiRouteService {

    Mono<RouteApiResponse> create(RouteApiRequest request);
    Mono<RouteApiResponse> update(Long id, RouteApiRequest request);
    Mono<RouteApiResponse> findById(Long id);
    Flux<RouteApiResponse> findAll();
    Mono<Void> deleteById(Long id);
    Mono<Void> deleteAll();

}
