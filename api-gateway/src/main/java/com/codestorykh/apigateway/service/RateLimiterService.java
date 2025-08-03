package com.codestorykh.apigateway.service;

import com.codestorykh.apigateway.repository.ApiRouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Service
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;

    private final ApiRouteRepository apiRouteRepository;


    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
                    "local limit = tonumber(ARGV[1]) " +
                    "local window = tonumber(ARGV[2]) " +
                    "local current = redis.call('INCR', key) " +
                    "if current == 1 then " +
                    "    redis.call('EXPIRE', key, window) " +
                    "end " +
                    "if current > limit then " +
                    "    return 0 " +
                    "else " +
                    "    return 1 " +
                    "end";

    public RateLimiterService(StringRedisTemplate stringRedisTemplate,
                              ApiRouteRepository apiRouteRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.apiRouteRepository = apiRouteRepository;
    }


    public Mono<Boolean> verifyRatingLimit(String path, String method, String identifier) {

        final  String normalizedPath = normalizePath(path);
        return apiRouteRepository.findFirstByPathAndMethod(normalizedPath, method)
                .flatMap(routeConfig -> {
                    if (routeConfig.getRateLimit() == null) {
                        return Mono.just(true); // No rate limit for this route
                    }

                    // Construct Redis key (identifier:path:method)
                    log.info("Rate limiting for path: {}, method: {}, identifier: {}", path, method, identifier);
                    String redisKey = String.format("%s:%s:%s", identifier, path, method);
                    log.info("Redis key: {}", redisKey);

                    // Execute Lua script to enforce rate limit
                    return Mono.fromCallable(() -> stringRedisTemplate.execute(
                            RedisScript.of(LUA_SCRIPT, Long.class),
                            Collections.singletonList(redisKey),
                            routeConfig.getRateLimit().toString(),
                            routeConfig.getRateLimitDuration().toString()
                    )).map(result -> result != null && result == 1);
                })
                .defaultIfEmpty(true); // Default to true if no route configuration is found
    }

    /**
     * Normalize the request path to match the database configuration.
     * Replace dynamic segments with placeholders (e.g., posts/1 -> posts/{id}).
     */
    private String normalizePath(String path) {
        // Define your dynamic path patterns and replace them with placeholders
        return path.replaceAll("/posts/\\d+", "/posts/{id}")
                .replaceAll("/users/\\d+", "/users/{id}");
    }
}
