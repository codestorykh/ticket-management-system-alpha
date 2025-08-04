# API Gateway Token Verification

This document explains how the API Gateway verifies JWT tokens by communicating with the User Service.

## Architecture Overview

The API Gateway implements a centralized authentication system where:

1. **API Gateway** receives all incoming requests
2. **JWT Authentication Filter** intercepts requests and extracts JWT tokens
3. **User Service** validates tokens and returns user information
4. **API Gateway** forwards valid requests to appropriate microservices

## Components

### 1. User Service Token Verification Endpoint

**Endpoint**: `POST /api/public/users/verify-token`

**Headers**: 
- `Authorization: Bearer <jwt_token>`

**Response**:
```json
{
  "message": "Token is valid",
  "code": "TOKEN_VALID",
  "data": {
    "username": "john.doe",
    "authorities": ["USER", "ADMIN"],
    "valid": true
  },
  "isError": false
}
```

### 2. API Gateway JWT Authentication Filter

**Location**: `api-gateway/src/main/java/com/codestorykh/apigateway/filter/JwtAuthenticationFilter.java`

**Features**:
- Intercepts all requests except public paths
- Extracts JWT tokens from the Authorization header
- Calls User Service to verify token validity
- Returns 401 Unauthorized for invalid tokens
- Allows valid requests to proceed

### 3. Public Paths (No Authentication Required)

The following paths are excluded from authentication:

- `/api/public/users/login`
- `/api/public/users/registration`
- `/api/public/users/refreshToken`
- `/api/public/users/logout`
- `/api/public/users/verify-token`
- `/health`
- `/actuator/health`
- `/actuator/info`

## Configuration

### API Gateway Configuration

**File**: `api-gateway/src/main/resources/application.yml`

```yaml
# User service configuration
user:
  service:
    url: ${USER_SERVICE_URL:http://localhost:8081}
```

### Environment Variables

- `USER_SERVICE_URL`: URL of the User Service (default: http://localhost:8081)

## Usage

### 1. Client Authentication Flow

1. Client calls `POST /api/public/users/login` with credentials
2. User Service returns JWT token
3. Client includes token in subsequent requests: `Authorization: Bearer <token>`

### 2. API Gateway Processing

1. Request arrives at API Gateway
2. JWT Authentication Filter checks if path requires authentication
3. If authentication required:
   - Extracts token from Authorization header
   - Calls User Service to verify token
   - If valid: forwards request to target service
   - If invalid: returns 401 Unauthorized

### 3. Example Request

```bash
# Login to get token
curl -X POST http://localhost:8080/api/public/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john.doe", "password": "password123"}'

# Use token for protected endpoint
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Error Handling

### Invalid Token Response

```json
{
  "success": false,
  "errorCode": "UNAUTHORIZED",
  "message": "Invalid token",
  "data": null
}
```

### Missing Authorization Header

```json
{
  "success": false,
  "errorCode": "UNAUTHORIZED",
  "message": "Missing or invalid Authorization header",
  "data": null
}
```

## Security Considerations

1. **Token Expiration**: Tokens have a configurable expiration time
2. **Secure Communication**: User Service and API Gateway should communicate over HTTPS in production
3. **Rate Limiting**: Token verification requests are subject to rate limiting
4. **Logging**: All authentication attempts are logged for security monitoring

## Troubleshooting

### Common Issues

1. **User Service Unavailable**: Check if User Service is running and accessible
2. **Invalid Token Format**: Ensure token follows JWT format and includes "Bearer " prefix
3. **Token Expired**: Client should refresh token or re-authenticate
4. **Network Issues**: Check network connectivity between API Gateway and User Service

### Debugging

Enable debug logging in API Gateway:

```yaml
logging:
  level:
    com.codestorykh.apigateway.filter: DEBUG
```

## Future Enhancements

1. **Token Caching**: Cache valid tokens to reduce User Service calls
2. **Role-based Access Control**: Implement fine-grained authorization
3. **Token Refresh**: Automatic token refresh for long-running sessions
4. **Multi-service Authentication**: Extend to other microservices 