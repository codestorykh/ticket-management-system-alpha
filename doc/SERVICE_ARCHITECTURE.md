# Architecture Ticket Management System

## 🏗️ Overview

The Ticket Management System is built as a **microservices architecture** with 7 core services, each handling specific business domains. The system is designed for high scalability, fault tolerance, and maintainability.

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT APPLICATIONS                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Web App   │  │ Mobile App  │  │ Admin Panel │  │ Third Party │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API GATEWAY (8080)                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │ • Authentication & Authorization                                        │ │
│  │ • Rate Limiting (Redis-based)                                          │ │
│  │ • Request Routing                                                      │ │
│  │ • Load Balancing                                                       │ │
│  │ • Circuit Breaker                                                      │ │
│  │ • Request/Response Logging                                             │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
        ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
        │   USER SERVICE  │ │  EVENT SERVICE  │ │ TICKET SERVICE  │
        │     (8081)      │ │     (8082)      │ │     (8083)      │
        └─────────────────┘ └─────────────────┘ └─────────────────┘
                    │               │               │
                    └───────────────┼───────────────┘
                                    │
                                    ▼
        ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
        │  ORDER SERVICE  │ │ PAYMENT SERVICE │ │NOTIFICATION SVC │
        │     (8084)      │ │     (8085)      │ │     (8086)      │
        └─────────────────┘ └─────────────────┘ └─────────────────┘
                    │               │               │
                    └───────────────┼───────────────┘
                                    │
                                    ▼
                    ┌─────────────────────────────────┐
                    │         INFRASTRUCTURE          │
                    │  ┌─────────┐ ┌─────────┐       │
                    │  │POSTGRES │ │  REDIS  │       │
                    │  │ (5432)  │ │ (6379)  │       │
                    │  └─────────┘ └─────────┘       │
                    │  ┌─────────┐ ┌─────────┐       │
                    │  │  KAFKA  │ │ ZOOKEEPER│       │
                    │  │ (9092)  │ │ (2181)  │       │
                    │  └─────────┘ └─────────┘       │
                    └─────────────────────────────────┘
```

## 🔧 Service Details

### 1. API Gateway Service (Port 8080)

**Purpose**: Single entry point for all client requests

**Responsibilities**:
- **Authentication & Authorization**: JWT token validation via User Service
- **Rate Limiting**: Redis-based rate limiting per client/IP
- **Request Routing**: Route requests to appropriate microservices
- **Load Balancing**: Distribute load across service instances
- **Circuit Breaker**: Prevent cascade failures
- **Request/Response Logging**: Centralized logging
- **CORS Handling**: Cross-origin resource sharing
- **API Documentation**: Swagger/OpenAPI integration

**Key Components**:
```java
- AuthenticationFilter: JWT validation
- RateLimitFilter: Redis-based rate limiting
- RouteConfigService: Dynamic route configuration
- RedisService: Caching and rate limiting
- JwtService: Token validation delegation
```

**Communication Patterns**:
- **Synchronous**: Validates JWT tokens with User Service
- **Asynchronous**: Logs requests/events to Kafka
- **Caching**: Redis for rate limiting and route configs

---

### 2. User Service (Port 8081)

**Purpose**: User management and authentication

**Responsibilities**:
- **User Registration**: Create new user accounts
- **User Authentication**: Login/logout functionality
- **JWT Token Management**: Generate and validate tokens
- **User Profile Management**: CRUD operations on user profiles
- **Role Management**: User roles and permissions
- **Password Management**: Secure password handling

**Key Components**:
```java
- UserController: REST endpoints for user operations
- UserService: Business logic for user management
- JwtService: JWT token generation and validation
- UserRepository: Data access layer
- PasswordEncoder: Secure password encoding
```

**Database Schema**:
```sql
users:
- id (PK)
- username (unique)
- email (unique)
- password (encrypted)
- first_name
- last_name
- phone_number
- role (USER, ADMIN)
- created_at
- updated_at
```

**Communication Patterns**:
- **Synchronous**: REST API calls from other services
- **Asynchronous**: Publishes user events to Kafka
- **Caching**: Redis for user session data

---

### 3. Event Service (Port 8082)

**Purpose**: Event management and administration

**Responsibilities**:
- **Event CRUD**: Create, read, update, delete events
- **Event Search**: Search and filter events
- **Event Categories**: Manage event types and categories
- **Event Status Management**: Active, inactive, cancelled events
- **Event Capacity Management**: Track available seats
- **Event Analytics**: Basic event statistics

**Key Components**:
```java
- EventController: REST endpoints for event operations
- EventService: Business logic for event management
- EventRepository: Data access layer
- EventSearchService: Search and filtering logic
```

**Database Schema**:
```sql
events:
- id (PK)
- title
- description
- location
- date
- capacity
- available_seats
- base_price
- event_type (CONCERT, SPORTS, THEATER, etc.)
- status (ACTIVE, INACTIVE, CANCELLED)
- created_at
- updated_at
```

**Communication Patterns**:
- **Synchronous**: REST API calls from other services
- **Asynchronous**: Publishes event updates to Kafka
- **Caching**: Redis for event listings and search results

---

### 4. Ticket Service (Port 8083)

**Purpose**: Ticket inventory and availability management

**Responsibilities**:
- **Ticket Inventory**: Manage ticket availability
- **Seat Management**: Handle seat assignments and conflicts
- **Ticket Locking**: Temporary ticket holds during purchase
- **Ticket Status Management**: Available, locked, sold, cancelled
- **Expired Lock Cleanup**: Automatic cleanup of expired locks
- **Ticket Analytics**: Availability statistics

**Key Components**:
```java
- TicketController: REST endpoints for ticket operations
- TicketService: Business logic for ticket management
- TicketRepository: Data access layer
- ScheduledTasks: Cleanup expired locks
- RedisService: Distributed locking
```

**Database Schema**:
```sql
tickets:
- id (PK)
- event_id (FK)
- seat_number
- price
- ticket_type (VIP, STANDARD, etc.)
- status (AVAILABLE, LOCKED, SOLD, CANCELLED)
- locked_until
- locked_by (user_id)
- created_at
- updated_at
```

**Communication Patterns**:
- **Synchronous**: REST API calls from Order Service
- **Asynchronous**: Publishes ticket status changes to Kafka
- **Distributed Locking**: Redis for ticket locking
- **Scheduled Tasks**: Cleanup expired locks

---

### 5. Order Service (Port 8084)

**Purpose**: Order processing and orchestration

**Responsibilities**:
- **Order Creation**: Create new orders
- **Order Orchestration**: Coordinate between services
- **Order Status Management**: Track order lifecycle
- **Payment Integration**: Coordinate with Payment Service
- **Ticket Reservation**: Lock tickets during order process
- **Order Cancellation**: Handle order cancellations and refunds
- **Order History**: User order history and tracking

**Key Components**:
```java
- OrderController: REST endpoints for order operations
- OrderService: Business logic for order management
- OrderEventService: Async event publishing
- UserServiceClient: Feign client for User Service
- TicketServiceClient: Feign client for Ticket Service
- PaymentServiceClient: Feign client for Payment Service
```

**Database Schema**:
```sql
orders:
- id (PK)
- user_id (FK)
- ticket_id (FK)
- event_id (FK)
- status (PENDING, CONFIRMED, CANCELLED, FAILED)
- total_amount
- quantity
- payment_id
- order_date
- created_at
- updated_at
```

**Communication Patterns**:
- **Synchronous**: REST calls to User, Ticket, and Payment services
- **Asynchronous**: Publishes order events to Kafka
- **Circuit Breaker**: Resilience4j for external service calls
- **Retry Logic**: Automatic retry for failed operations

---

### 6. Payment Service (Port 8085)

**Purpose**: Payment processing and management

**Responsibilities**:
- **Payment Processing**: Handle various payment methods
- **Payment Gateway Integration**: Multiple payment gateways
- **Payment Status Tracking**: Track payment lifecycle
- **Refund Processing**: Handle payment refunds
- **Payment Analytics**: Payment statistics and reporting
- **Fraud Detection**: Basic fraud prevention

**Key Components**:
```java
- PaymentController: REST endpoints for payment operations
- PaymentService: Business logic for payment processing
- PaymentGatewayService: Integration with payment gateways
- PaymentRepository: Data access layer
- FraudDetectionService: Basic fraud prevention
```

**Database Schema**:
```sql
payments:
- id (PK)
- order_id (FK)
- user_id (FK)
- amount
- currency
- payment_method (CREDIT_CARD, DEBIT_CARD, etc.)
- status (PENDING, COMPLETED, FAILED, REFUNDED)
- gateway_transaction_id
- gateway_response
- payment_date
- created_at
- updated_at
```

**Communication Patterns**:
- **Synchronous**: REST API calls from Order Service
- **Asynchronous**: Publishes payment events to Kafka
- **External APIs**: Payment gateway integrations
- **Circuit Breaker**: Handle payment gateway failures

---

### 7. Notification Service (Port 8086)

**Purpose**: Notification delivery and management

**Responsibilities**:
- **Email Notifications**: Send email notifications
- **SMS Notifications**: Send SMS notifications
- **Push Notifications**: Mobile push notifications
- **Notification Templates**: Manage notification templates
- **Notification History**: Track sent notifications
- **Retry Logic**: Handle failed notifications
- **Notification Preferences**: User notification settings

**Key Components**:
```java
- NotificationController: REST endpoints for notification operations
- NotificationService: Business logic for notification management
- EmailService: Email delivery service
- SmsService: SMS delivery service
- KafkaListener: Consume notification events
- NotificationRepository: Data access layer
```

**Database Schema**:
```sql
notifications:
- id (PK)
- user_id (FK)
- order_id (FK)
- type (EMAIL, SMS, PUSH)
- subject
- content
- status (PENDING, SENT, FAILED)
- retry_count
- sent_at
- created_at
- updated_at
```

**Communication Patterns**:
- **Asynchronous**: Consumes events from Kafka
- **External APIs**: Email/SMS service integrations
- **Retry Logic**: Handle delivery failures
- **Batch Processing**: Process notifications in batches

---

## 🔄 Service Communication Patterns

### 1. Synchronous Communication (REST APIs)

**Pattern**: Request-Response
**Use Cases**: Real-time operations requiring immediate response

**Services Using REST**:
- **API Gateway ↔ User Service**: JWT validation
- **Order Service ↔ User Service**: User validation
- **Order Service ↔ Ticket Service**: Ticket operations
- **Order Service ↔ Payment Service**: Payment processing

### 2. Asynchronous Communication (Kafka)

**Pattern**: Event-Driven Architecture
**Use Cases**: Non-blocking operations, notifications, analytics

**Event Topics**:
- `order-created`: New order created
- `order-confirmed`: Order payment confirmed
- `order-cancelled`: Order cancelled
- `payment-processed`: Payment completed
- `ticket-locked`: Tickets locked
- `ticket-sold`: Tickets sold
- `user-registered`: New user registered

### 3. Caching Communication (Redis)

**Pattern**: Cache-Aside
**Use Cases**: Frequently accessed data, session management

**Cached Data**:
- **User Sessions**: JWT tokens, user info
- **Event Data**: Event listings, search results
- **Ticket Availability**: Available ticket counts
- **Rate Limiting**: Request counts per client
- **Route Configuration**: API Gateway routes

### 4. Database Communication

**Pattern**: Repository Pattern
**Use Cases**: Data persistence, transactions

**Database Patterns**:
- **Shared Database**: All services use same PostgreSQL instance
- **Repository Pattern**: Abstract data access
- **Transaction Management**: ACID compliance
- **Connection Pooling**: HikariCP for performance

---

## 🔐 Security Architecture

### 1. Authentication & Authorization

**Security Components**:
- **JWT Tokens**: Stateless authentication
- **Role-Based Access Control**: User roles and permissions
- **API Gateway Security**: Centralized security enforcement
- **Service-to-Service Security**: Internal network security

### 2. Rate Limiting

**Rate Limiting Strategy**:
- **Per Client/IP**: Individual client limits
- **Per Endpoint**: Different limits for different APIs
- **Sliding Window**: Time-based rate limiting
- **Redis Storage**: Distributed rate limiting

---

## 📈 Scalability Patterns

### 1. Horizontal Scaling

**Scaling Strategies**:
- **Service Replication**: Multiple instances per service
- **Load Balancing**: Distribute load across instances
- **Auto-scaling**: Scale based on metrics
- **Database Scaling**: Read replicas, sharding

### 2. Caching Strategy

**Caching Layers**:
- **Application Cache**: In-memory caching
- **Distributed Cache**: Redis for shared data
- **Database Cache**: Query result caching
- **CDN Cache**: Static content caching

### 3. Circuit Breaker Pattern

**Circuit Breaker Implementation**:
- **Resilience4j**: Circuit breaker library
- **Failure Threshold**: Configurable failure limits
- **Timeout**: Automatic recovery attempts
- **Fallback**: Graceful degradation

---

## 🔍 Monitoring & Observability

### 1. Health Checks

**Health Check Endpoints**:
- `/actuator/health`: Basic health status
- `/actuator/health/db`: Database connectivity
- `/actuator/health/redis`: Redis connectivity
- `/actuator/health/kafka`: Kafka connectivity

### 2. Metrics Collection

**Metrics Types**:
- **Application Metrics**: Request rates, response times
- **Business Metrics**: Orders, payments, tickets
- **Infrastructure Metrics**: CPU, memory, disk
- **Custom Metrics**: Business-specific KPIs

### 3. Distributed Tracing

**Tracing Implementation**:
- **Sleuth**: Distributed tracing
- **Zipkin**: Trace visualization
- **Trace IDs**: Request correlation
- **Span Tracking**: Operation timing

---

## 🚀 Deployment Architecture

### 1. Container Orchestration

**Deployment Components**:
- **Docker Containers**: Service packaging
- **Kubernetes**: Container orchestration
- **Service Mesh**: Istio for advanced networking
- **CI/CD**: Automated deployment pipelines

### 2. Environment Strategy

**Environment Types**:
- **Development**: Local Docker Compose
- **Staging**: Production-like environment
- **Production**: High-availability setup

---

## 📋 Service Dependencies

### 1. Infrastructure Dependencies

| Service              | Database | Redis | Kafka | External APIs |
|----------------------|----------|-------|-------|---------------|
| API Gateway          | ✅        | ✅     | ✅     | ✅             |
| User Service         | ✅        | ✅     | ✅     | ❌             |
| Event Service        | ✅        | ✅     | ✅     | ❌             |
| Ticket Service       | ✅        | ✅     | ✅     | ❌             |
| Order Service        | ✅        | ✅     | ✅     | ✅             |
| Payment Service      | ✅        | ✅     | ✅     | ✅             |
| Notification Service | ✅        | ✅     | ✅     | ✅             |

### 2. Service Dependencies

| Service              | Depends On                                    | Communication Type           |
|----------------------|-----------------------------------------------|------------------------------|
| API Gateway          | User Service                                  | Synchronous (JWT validation) |
| Order Service        | User Service, Ticket Service, Payment Service | Synchronous (REST)           |
| Notification Service | All Services                                  | Asynchronous (Kafka)         |
| Payment Service      | Order Service                                 | Synchronous (REST)           |

---

## 🎯 Key Design Principles

### 1. Single Responsibility Principle
Each service has a single, well-defined responsibility and business domain.

### 2. Loose Coupling
Services communicate through well-defined APIs and events, not direct dependencies.

### 3. High Cohesion
Related functionality is grouped together within each service.

### 4. Fault Tolerance
Services are designed to handle failures gracefully with circuit breakers and fallbacks.

### 5. Scalability
Services can be scaled independently based on demand.

### 6. Observability
Comprehensive monitoring, logging, and tracing for all services.

### 7. Security
Security is implemented in multiple layers with proper authentication and authorization.

---

This architecture provides a robust, scalable, and maintainable foundation for the Ticket Management System, capable of handling high loads while maintaining system reliability and performance. 