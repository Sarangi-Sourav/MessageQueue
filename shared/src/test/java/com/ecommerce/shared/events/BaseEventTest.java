package com.ecommerce.shared.events;

import com.ecommerce.shared.enums.PaymentStatus;
import com.ecommerce.shared.model.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BaseEventTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void shouldCreateBaseEventWithCorrectMetadata() {
        // Given & When
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            List.of(), 
            BigDecimal.ZERO
        );
        
        // Then
        assertNotNull(event.getEventId());
        assertEquals("correlation-123", event.getCorrelationId());
        assertEquals("ORDER_CREATED", event.getEventType());
        assertNotNull(event.getTimestamp());
        assertTrue(event.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(event.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
    
    @Test
    void shouldSerializeAndDeserializeOrderCreatedEvent() throws Exception {
        // Given
        OrderItem item = new OrderItem("product-1", "Test Product", 2, BigDecimal.valueOf(10.00));
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            List.of(item), 
            BigDecimal.valueOf(20.00)
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        BaseEvent deserializedEvent = objectMapper.readValue(json, BaseEvent.class);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof OrderCreatedEvent);
        OrderCreatedEvent orderEvent = (OrderCreatedEvent) deserializedEvent;
        assertEquals("order-456", orderEvent.getOrderId());
        assertEquals("customer-789", orderEvent.getCustomerId());
        assertEquals("correlation-123", orderEvent.getCorrelationId());
        assertEquals("ORDER_CREATED", orderEvent.getEventType());
        assertEquals(1, orderEvent.getItems().size());
        assertEquals(BigDecimal.valueOf(20.00), orderEvent.getTotalAmount());
        
        // Verify item details
        OrderItem deserializedItem = orderEvent.getItems().get(0);
        assertEquals("product-1", deserializedItem.getProductId());
        assertEquals("Test Product", deserializedItem.getProductName());
        assertEquals(2, deserializedItem.getQuantity());
        assertEquals(BigDecimal.valueOf(10.00), deserializedItem.getUnitPrice());
    }
    
    @Test
    void shouldSerializeAndDeserializeInventoryReservedEvent() throws Exception {
        // Given
        Map<String, Integer> reservedItems = new HashMap<>();
        reservedItems.put("product-1", 2);
        reservedItems.put("product-2", 1);
        
        InventoryReservedEvent event = new InventoryReservedEvent(
            "correlation-456",
            "order-789",
            reservedItems
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        BaseEvent deserializedEvent = objectMapper.readValue(json, BaseEvent.class);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof InventoryReservedEvent);
        InventoryReservedEvent inventoryEvent = (InventoryReservedEvent) deserializedEvent;
        assertEquals("order-789", inventoryEvent.getOrderId());
        assertEquals("correlation-456", inventoryEvent.getCorrelationId());
        assertEquals("INVENTORY_RESERVED", inventoryEvent.getEventType());
        assertEquals(2, inventoryEvent.getReservedItems().size());
        assertEquals(Integer.valueOf(2), inventoryEvent.getReservedItems().get("product-1"));
        assertEquals(Integer.valueOf(1), inventoryEvent.getReservedItems().get("product-2"));
    }
    
    @Test
    void shouldSerializeAndDeserializePaymentProcessedEvent() throws Exception {
        // Given
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            "correlation-789",
            "order-123",
            "payment-456",
            BigDecimal.valueOf(99.99),
            PaymentStatus.COMPLETED
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        BaseEvent deserializedEvent = objectMapper.readValue(json, BaseEvent.class);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof PaymentProcessedEvent);
        PaymentProcessedEvent paymentEvent = (PaymentProcessedEvent) deserializedEvent;
        assertEquals("order-123", paymentEvent.getOrderId());
        assertEquals("payment-456", paymentEvent.getPaymentId());
        assertEquals("correlation-789", paymentEvent.getCorrelationId());
        assertEquals("PAYMENT_PROCESSED", paymentEvent.getEventType());
        assertEquals(BigDecimal.valueOf(99.99), paymentEvent.getAmount());
        assertEquals(PaymentStatus.COMPLETED, paymentEvent.getStatus());
    }
    
    @Test
    void shouldHandleJsonSerializationWithNullValues() throws Exception {
        // Given
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            null, // null customerId
            null, // null items
            null  // null totalAmount
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        BaseEvent deserializedEvent = objectMapper.readValue(json, BaseEvent.class);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof OrderCreatedEvent);
        OrderCreatedEvent orderEvent = (OrderCreatedEvent) deserializedEvent;
        assertEquals("order-456", orderEvent.getOrderId());
        assertNull(orderEvent.getCustomerId());
        assertNull(orderEvent.getItems());
        assertNull(orderEvent.getTotalAmount());
    }
    
    @Test
    void shouldValidateEventTypePolymorphism() throws Exception {
        // Given - Create different event types
        OrderCreatedEvent orderEvent = new OrderCreatedEvent("corr-1", "order-1", "customer-1", List.of(), BigDecimal.ZERO);
        InventoryReservedEvent inventoryEvent = new InventoryReservedEvent("corr-2", "order-2", Map.of("product-1", 1));
        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent("corr-3", "order-3", "payment-1", BigDecimal.TEN, PaymentStatus.COMPLETED);
        
        // When - Serialize as BaseEvent and deserialize
        String orderJson = objectMapper.writeValueAsString((BaseEvent) orderEvent);
        String inventoryJson = objectMapper.writeValueAsString((BaseEvent) inventoryEvent);
        String paymentJson = objectMapper.writeValueAsString((BaseEvent) paymentEvent);
        
        BaseEvent deserializedOrder = objectMapper.readValue(orderJson, BaseEvent.class);
        BaseEvent deserializedInventory = objectMapper.readValue(inventoryJson, BaseEvent.class);
        BaseEvent deserializedPayment = objectMapper.readValue(paymentJson, BaseEvent.class);
        
        // Then - Verify correct polymorphic deserialization
        assertTrue(deserializedOrder instanceof OrderCreatedEvent);
        assertTrue(deserializedInventory instanceof InventoryReservedEvent);
        assertTrue(deserializedPayment instanceof PaymentProcessedEvent);
        
        assertEquals("ORDER_CREATED", deserializedOrder.getEventType());
        assertEquals("INVENTORY_RESERVED", deserializedInventory.getEventType());
        assertEquals("PAYMENT_PROCESSED", deserializedPayment.getEventType());
    }
    
    @Test
    void shouldPreserveTimestampFormatDuringSerialization() throws Exception {
        // Given
        OrderCreatedEvent event = new OrderCreatedEvent("corr-1", "order-1", "customer-1", List.of(), BigDecimal.ZERO);
        
        // When
        String json = objectMapper.writeValueAsString(event);
        BaseEvent deserializedEvent = objectMapper.readValue(json, BaseEvent.class);
        
        // Then - Timestamps should be equal when serialized/deserialized (precision may be lost)
        assertNotNull(deserializedEvent.getTimestamp());
        
        // Verify JSON contains properly formatted timestamp
        assertTrue(json.contains("\"timestamp\":\""));
        assertTrue(json.matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\".*"));
    }
}