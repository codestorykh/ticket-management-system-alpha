package com.codestorykh.apigateway.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("request_logs")
public class RequestLog {

    @Id
    private Long id;

    @Column("request_id")
    private String requestId;

    @Column("correlation_id")
    private String correlationId;

    @Column("method")
    private String method;

    @Column("uri")
    private String uri;

    @Column("path")
    private String path;

    @Column("client_ip")
    private String clientIp;

    @Column("user_agent")
    private String userAgent;

    @Column("request_headers")
    private String requestHeaders;

    @Column("request_body")
    private String requestBody;

    @Column("request_size")
    private Long requestSize;

    @Column("response_status")
    private Integer responseStatus;

    @Column("response_headers")
    private String responseHeaders;

    @Column("duration_ms")
    private Long durationMs;

    @Column("service_name")
    private String serviceName;

    @Column("created_at")
    private LocalDateTime createdAt;
} 