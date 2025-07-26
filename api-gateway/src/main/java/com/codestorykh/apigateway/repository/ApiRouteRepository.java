package com.codestorykh.apigateway.repository;

import com.codestorykh.apigateway.entity.ApiRoute;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiRouteRepository extends R2dbcRepository<ApiRoute, Long> {

    Mono<ApiRoute> findFirstById(Long id);

    Mono<Void> deleteById(Long id);

    @Query("""
    UPDATE api_routes SET uri = :uri, path = :path, method = :method, description = :description,
              group_code = :groupCode, rate_limit = :rateLimit, rate_limit_duration = :rateLimitDuration,
              status = :status, updated_at = NOW(), updated_by = :updatedBy
     WHERE id = :id RETURNING *
""")
    Mono<ApiRoute> update(Long id, String uri, String path, String method, String description,
                          String groupCode, Integer rateLimit, Integer rateLimitDuration, String status,
                          String updatedBy);

    @Query("SELECT * FROM api_routes WHERE path = :path AND method = :method LIMIT 1")
    Mono<ApiRoute> findFirstByPathAndMethod(String path, String method);
}
