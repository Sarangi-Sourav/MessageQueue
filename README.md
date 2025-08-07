# Kafka Spring Integration - E-commerce Order Processing System using KIRO 

A comprehensive Real-Time E-commerce Order Processing System built with Spring Boot and Apache Kafka, demonstrating advanced event-driven architecture patterns including SAGA choreography, event sourcing, and microservices communication.

## Project Structure

This is a multi-module Maven project with the following structure:

```
kafka-spring-integration/
├── shared/                    # Shared components and event models
├── order-service/            # Order management service (Port: 8081)
├── inventory-service/        # Inventory management service (Port: 8082)
├── payment-service/          # Payment processing service (Port: 8083)
├── notification-service/     # Customer notification service (Port: 8084)
├── analytics-service/        # Real-time analytics service (Port: 8085)
└── pom.xml                   # Parent POM configuration
```

## Shared Module

The `shared` module contains:

- **Event Models**: Base event classes and specific event types (OrderCreated, InventoryReserved, PaymentProcessed, etc.)
- **Domain Models**: Common data models like OrderItem
- **Enums**: Status enums for orders, payments, and notifications
- **Kafka Configuration**: Base Kafka producer/consumer configuration
- **Utilities**: Correlation ID generation and common utilities

## Key Features

- **Event-Driven Architecture**: All services communicate through Kafka events
- **SAGA Pattern**: Distributed transaction management with compensation flows
- **Multi-Module Maven**: Clean separation of concerns with shared components
- **Spring Boot Integration**: Each service is a standalone Spring Boot application
- **Observability Ready**: Configured for metrics, health checks, and monitoring
- **Resilience Patterns**: Circuit breakers and retry mechanisms
- **Containerization Ready**: Prepared for Docker deployment

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.4**
- **Apache Kafka** for event streaming
- **Spring Data JPA** for data persistence
- **H2 Database** for development/testing
- **Resilience4j** for circuit breakers and retries
- **Micrometer/Prometheus** for metrics
- **TestContainers** for integration testing

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Apache Kafka (for runtime)

### Build the Project

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Service Ports

- Order Service: 8081
- Inventory Service: 8082
- Payment Service: 8083
- Notification Service: 8084
- Analytics Service: 8085

## Event Flow

1. **Order Creation**: Customer places order via REST API
2. **Inventory Check**: Inventory service reserves stock
3. **Payment Processing**: Payment service processes payment
4. **Notifications**: Customer receives confirmations
5. **Analytics**: Real-time metrics are updated

## SAGA Compensation Flows

- **Inventory Insufficient**: Order → Inventory Check → Order Cancelled → Notification
- **Payment Failed**: Order → Inventory Reserved → Payment Failed → Inventory Released → Order Cancelled → Notification

## Next Steps

This project structure is ready for implementing the complete e-commerce order processing system. Each service can be developed independently while sharing common event models and utilities through the shared module.
