package com.ecommerce.shared.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "ORDER_CREATED"),
    @JsonSubTypes.Type(value = InventoryReservedEvent.class, name = "INVENTORY_RESERVED"),
    @JsonSubTypes.Type(value = InventoryInsufficientEvent.class, name = "INVENTORY_INSUFFICIENT"),
    @JsonSubTypes.Type(value = PaymentProcessedEvent.class, name = "PAYMENT_PROCESSED"),
    @JsonSubTypes.Type(value = PaymentFailedEvent.class, name = "PAYMENT_FAILED"),
    @JsonSubTypes.Type(value = OrderCancelledEvent.class, name = "ORDER_CANCELLED"),
    @JsonSubTypes.Type(value = NotificationSentEvent.class, name = "NOTIFICATION_SENT")
})
public abstract class BaseEvent {
    
    private String eventId;
    private String correlationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private String eventType;
    
    public BaseEvent(String correlationId, String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.correlationId = correlationId;
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
    }
}