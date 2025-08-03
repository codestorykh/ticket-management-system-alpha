# API Gateway Enhanced Logging

This document describes the enhanced logging functionality implemented in the API Gateway module.

## Overview

The API Gateway now includes comprehensive request and response logging with the following features:

- Request and response logging with customizable formats
- Performance monitoring and slow request detection
- Correlation ID tracking for request tracing
- Sensitive data masking
- Request metrics collection
- Error logging with stack traces
- Configurable logging levels and thresholds

## Components

### 1. Logging Filters

#### LoggingFilter
- Main logging filter that handles basic request/response logging
- Captures request bodies when configured
- Logs response status and headers
- Handles error scenarios

#### RequestLoggingFilter
- Specialized filter for request logging
- Captures and logs request bodies
- Adds request ID and timestamp to exchange attributes

#### ResponseLoggingFilter
- Specialized filter for response logging
- Logs response status, headers, and duration
- Handles different log levels based on response status

#### PerformanceLoggingFilter
- Monitors request performance
- Logs slow requests (configurable threshold)
- Provides performance metrics

#### CorrelationIdFilter
- Adds correlation IDs to requests
- Supports custom correlation ID headers
- Generates unique IDs when not provided

#### RequestMetricsFilter
- Captures additional request metrics
- Logs client IP, user agent, request size
- Provides detailed request information

### 2. Configuration

#### GatewayLoggingProperties
Configuration properties for all logging features:

```yaml
gateway:
  logging:
    enabled: true
    level: INFO
    log-request-body: true
    log-response-body: true
    log-headers: true
    mask-sensitive-headers: true
    max-body-length: 1000
    log-performance: true
    log-error-stack-traces: true
    include-request-id: true
    log-slow-requests: true
    slow-request-threshold: 1000
    log-request-metrics: true
    log-response-metrics: true
    enable-correlation-id: true
    correlation-id-header: X-Correlation-ID
    log-user-agent: true
    log-client-ip: true
    log-request-size: true
    log-response-size: true
    excluded-paths:
      - /health
      - /actuator/health
      - /actuator/info
```

### 3. Utility Classes

#### GatewayLoggingUtils
Provides utility methods for:
- Request ID generation
- Header formatting with sensitive data masking
- Body truncation
- Timestamp formatting
- Log message creation

#### GatewayLoggingService
Centralized logging service that provides:
- Request logging with and without body
- Response logging
- Error logging
- Performance logging
- Body validation and truncation

## Log Formats

### Request Log Format
```
REQUEST: {method} {uri} - Headers: {headers} - Body: {body} - Duration: {duration}ms - Timestamp: {timestamp}
```

### Response Log Format
```
RESPONSE: {status} - Headers: {headers} - Body: {body} - Duration: {duration}ms - Timestamp: {timestamp}
```

### Error Log Format
```
ERROR: {method} {uri} - Error: {error} - Duration: {duration}ms - Timestamp: {timestamp}
```

## Features

### 1. Request/Response Logging
- Logs HTTP method, URI, headers, and body
- Configurable body logging (enabled/disabled)
- Body truncation for large requests
- Sensitive header masking

### 2. Performance Monitoring
- Request duration tracking
- Slow request detection
- Performance metrics logging
- Configurable thresholds

### 3. Correlation ID
- Automatic correlation ID generation
- Support for custom correlation ID headers
- Request tracing across services

### 4. Request Metrics
- Client IP detection
- User agent logging
- Request size tracking
- Additional metadata collection

### 5. Error Handling
- Comprehensive error logging
- Stack trace logging (configurable)
- Error categorization by HTTP status

### 6. Security
- Sensitive data masking
- Configurable sensitive headers
- Body content type validation
- Excluded paths for health checks

## Usage

The logging system is automatically enabled when the application starts. All filters are registered as Spring beans and will process requests based on the configuration.

### Example Log Output

```
[abc12345] REQUEST: POST /api/users - Headers: {"Content-Type":"application/json","Authorization":"***"} - Body: {"name":"John Doe","email":"john@example.com"} - Duration: 0ms - Timestamp: 2024-01-15 10:30:45.123
[abc12345] REQUEST METRICS: {clientIP=192.168.1.100, userAgent=Mozilla/5.0, requestSize=89}
[abc12345] RESPONSE: 200 OK - Headers: {"Content-Type":"application/json"} - Body: *** - Duration: 150ms - Timestamp: 2024-01-15 10:30:45.273
[abc12345] PERFORMANCE: POST /api/users - Status: 200 OK - Duration: 150ms
```

## Configuration Options

### Basic Logging
- `enabled`: Enable/disable all logging
- `level`: Logging level (INFO, DEBUG, etc.)
- `log-request-body`: Enable request body logging
- `log-response-body`: Enable response body logging
- `log-headers`: Enable header logging

### Performance
- `log-performance`: Enable performance monitoring
- `log-slow-requests`: Enable slow request detection
- `slow-request-threshold`: Threshold for slow requests (ms)

### Security
- `mask-sensitive-headers`: Enable sensitive header masking
- `sensitive-headers`: List of headers to mask
- `excluded-paths`: Paths to exclude from logging

### Metrics
- `log-request-metrics`: Enable request metrics
- `log-response-metrics`: Enable response metrics
- `log-client-ip`: Enable client IP logging
- `log-user-agent`: Enable user agent logging

### Correlation
- `enable-correlation-id`: Enable correlation ID
- `correlation-id-header`: Custom correlation ID header name

## Best Practices

1. **Performance**: Be cautious with body logging in production as it can impact performance
2. **Security**: Always mask sensitive headers and data
3. **Storage**: Consider log rotation and retention policies
4. **Monitoring**: Use the performance metrics to monitor application health
5. **Correlation**: Use correlation IDs for distributed tracing

## Troubleshooting

### Common Issues

1. **High Memory Usage**: Reduce `max-body-length` or disable body logging
2. **Performance Impact**: Disable unnecessary logging features
3. **Log Volume**: Adjust log levels and exclude health check paths
4. **Missing Correlation IDs**: Check correlation ID configuration

### Debug Mode

Enable debug logging to see detailed filter execution:

```yaml
logging:
  level:
    com.codestorykh.apigateway.filter: DEBUG
    com.codestorykh.apigateway.logging: DEBUG
``` 