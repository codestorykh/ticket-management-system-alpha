package com.codestorykh.apigateway.service.impl;

import com.codestorykh.apigateway.entity.ApiRoute;
import com.codestorykh.apigateway.repository.ApiRouteRepository;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class RouteLocatorDetail implements RouteLocator {

    private final ApiRouteRepository apiRouteRepository;
    private final RouteLocatorBuilder routeLocatorBuilder;

    public RouteLocatorDetail(ApiRouteRepository apiRouteRepository,
                              RouteLocatorBuilder routeLocatorBuilder) {
        this.apiRouteRepository = apiRouteRepository;
        this.routeLocatorBuilder = routeLocatorBuilder;
    }

    @Override
    public Flux<Route> getRoutes() {
        RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();

        return apiRouteRepository.findAll()
                .map(apiRoute -> builder.route(apiRoute.getId().toString(),
                        predicateSpec -> setPredicateSpec(predicateSpec, apiRoute)))
                .collectList()
                .flatMapMany(builders -> builder.build().getRoutes());
    }

    @Override
    public Flux<Route> getRoutesByMetadata(Map<String, Object> metadata) {
        return RouteLocator.super.getRoutesByMetadata(metadata);
    }

    private Buildable<Route> setPredicateSpec(PredicateSpec predicateSpec,
                                       ApiRoute apiRoute) {
        BooleanSpec booleanSpec = predicateSpec.path(apiRoute.getPath());
        if(!apiRoute.getMethod().isBlank()) {
            booleanSpec.and().method(apiRoute.getMethod());
        }
        return booleanSpec.uri(apiRoute.getUri());
    }

}
