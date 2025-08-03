package com.codestorykh.apigateway.controller;

import com.codestorykh.apigateway.dto.RouteApiRequest;
import com.codestorykh.apigateway.dto.RouteApiResponse;
import com.codestorykh.apigateway.exception.ApiResponse;
import com.codestorykh.apigateway.service.ApiRouteService;
import com.codestorykh.apigateway.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/routes",
produces = MediaType.APPLICATION_JSON_VALUE,
consumes = MediaType.APPLICATION_JSON_VALUE)
public class ApiRouteController {

    private final ApiRouteService apiRouteService;
    private final GatewayRouteService gatewayRouteService;

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponse<Void>> refreshRoutes() {
         gatewayRouteService.refreshRoutes();
         return Mono.empty();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponse<RouteApiResponse>> create(@RequestBody RouteApiRequest apiRequest) {
        return apiRouteService.create(apiRequest)
                .map(ApiResponse::success);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponse<RouteApiResponse>> update(@PathVariable Long id, @RequestBody RouteApiRequest apiRequest) {
        return apiRouteService.update(id, apiRequest)
                .map(ApiResponse::success);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponse<RouteApiResponse>> getById(@PathVariable Long id) {
        return apiRouteService.findById(id)
                .map(ApiResponse::success);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponse<List<RouteApiResponse>>> getAll() {
        return apiRouteService.findAll()
                .collectList()
                .map(ApiResponse::success);
    }

}
