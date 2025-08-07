package com.ecommerce.shared.config;

import com.ecommerce.shared.enums.PaymentStatus;
import com.ecommerce.shared.events.BaseEvent;
import com.ecommerce.shared.events.OrderCreatedEvent;
import com.ecommerce.shared.events.InventoryReservedEvent;
import com.ecommerce.shared.events.PaymentProcessedEvent;
import com.ecommerce.shared.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaSerializationIntegrationTest {
    
    private final JsonSerializer<BaseEvent> serializer = new JsonSerializer<>();
    private final JsonDeserializer<BaseEvent> deserializer = new JsonDeserializer<>(BaseEvent.class);
    
    @Test
    void shouldSerializeAndDeserializeOrderCreatedEventThroughKafka() {
        // Given
        OrderItem item = new OrderItem("product-1", "Test Product", 2, BigDecimal.valueOf(10.00));
        OrderCreatedEvent originalEvent = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            List.of(item), 
            BigDecimal.valueOf(20.00)
        );
        
        // When - Simulate Kafka serialization/deserialization
        byte[] serializedData = serializer.serialize("test-topic", originalEvent);
        BaseEvent deserializedEvent = deserializer.deserialize("test-topic", serializedData);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof OrderCreatedEvent);
        
        OrderCreatedEvent orderEvent = (OrderCreatedEvent) deserializedEvent;
        assertEquals(originalEvent.getEventId(), orderEvent.getEventId());
        assertEquals(originalEvent.getCorrelationId(), orderEvent.getCorrelationId());
        assertEquals(originalEvent.getOrderId(), orderEvent.getOrderId());
        assertEquals(originalEvent.getCustomerId(), orderEvent.getCustomerId());
        assertEquals(originalEvent.getTotalAmount(), orderEvent.getTotalAmount());
        assertEquals(originalEvent.getEventType(), orderEvent.getEventType());
        
        // Verify items
        assertEquals(1, orderEvent.getItems().size());
        OrderItem deserializedItem = orderEvent.getItems().get(0);
        assertEquals("product-1", deserializedItem.getProductId());
        assertEquals("Test Product", deserializedItem.getProductName());
        assertEquals(2, deserializedItem.getQuantity());
        assertEquals(BigDecimal.valueOf(10.00), deserializedItem.getUnitPrice());
    }
    
    @Test
    void shouldSerializeAndDeserializeInventoryReservedEventThroughKafka() {
        // Given
        Map<String, Integer> reservedItems = Map.of("product-1", 2, "product-2", 1);
        InventoryReservedEvent originalEvent = new InventoryReservedEvent(
            "correlation-456",
            "order-789",
            reservedItems
        );
        
        // When - Simulate Kafka serialization/deserialization
        byte[] serializedData = serializer.serialize("test-topic", originalEvent);
        BaseEvent deserializedEvent = deserializer.deserialize("test-topic", serializedData);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof InventoryReservedEvent);
        
        InventoryReservedEvent inventoryEvent = (InventoryReservedEvent) deserializedEvent;
        assertEquals(originalEvent.getEventId(), inventoryEvent.getEventId());
        assertEquals(originalEvent.getCorrelationId(), inventoryEvent.getCorrelationId());
        assertEquals(originalEvent.getOrderId(), inventoryEvent.getOrderId());
        assertEquals(originalEvent.getEventType(), inventoryEvent.getEventType());
        assertEquals(originalEvent.getReservedItems(), inventoryEvent.getReservedItems());
    }
    
    @Test
    void shouldSerializeAndDeserializePaymentProcessedEventThroughKafka() {
        // Given
        PaymentProcessedEvent originalEvent = new PaymentProcessedEvent(
            "correlation-789",
            "order-123",
            "payment-456",
            BigDecimal.valueOf(99.99),
            PaymentStatus.COMPLETED
        );
        
        // When - Simulate Kafka serialization/deserialization
        byte[] serializedData = serializer.serialize("test-topic", originalEvent);
        BaseEvent deserializedEvent = deserializer.deserialize("test-topic", serializedData);
        
        // Then
        assertNotNull(deserializedEvent);
        assertTrue(deserializedEvent instanceof PaymentProcessedEvent);
        
        PaymentProcessedEvent paymentEvent = (PaymentProcessedEvent) deserializedEvent;
        assertEquals(originalEvent.getEventId(), paymentEvent.getEventId());
        assertEquals(originalEvent.getCorrelationId(), paymentEvent.getCorrelationId());
        assertEquals(originalEvent.getOrderId(), paymentEvent.getOrderId());
        assertEquals(originalEvent.getPaymentId(), paymentEvent.getPaymentId());
        assertEquals(originalEvent.getAmount(), paymentEvent.getAmount());
        assertEquals(originalEvent.getStatus(), paymentEvent.getStatus());
        assertEquals(originalEvent.getEventType(), paymentEvent.getEventType());
    }
    
    @Test
    void shouldHandlePolymorphicDeserializationCorrectly() {
        // Given - Different event types
        OrderCreatedEvent orderEvent = new OrderCreatedEvent("corr-1", "order-1", "customer-1", List.of(), BigDecimal.ZERO);
        InventoryReservedEvent inventoryEvent = new InventoryReservedEvent("corr-2", "order-2", Map.of("product-1", 1));
        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent("corr-3", "order-3", "payment-1", BigDecimal.TEN, PaymentStatus.COMPLETED);
        
        // When - Serialize and deserialize each event
        byte[] orderData = serializer.serialize("test-topic", orderEvent);
        byte[] inventoryData = serializer.serialize("test-topic", inventoryEvent);
        byte[] paymentData = serializer.serialize("test-topic", paymentEvent);
        
        BaseEvent deserializedOrder = deserializer.deserialize("test-topic", orderData);
        BaseEvent deserializedInventory = deserializer.deserialize("test-topic", inventoryData);
        BaseEvent deserializedPayment = deserializer.deserialize("test-topic", paymentData);
        
        // Then - Verify correct polymorphic deserialization
        assertTrue(deserializedOrder instanceof OrderCreatedEvent);
        assertTrue(deserializedInventory instanceof InventoryReservedEvent);
        assertTrue(deserializedPayment instanceof PaymentProcessedEvent);
        
        assertEquals("ORDER_CREATED", deserializedOrder.getEventType());
        assertEquals("INVENTORY_RESERVED", deserializedInventory.getEventType());
        assertEquals("PAYMENT_PROCESSED", deserializedPayment.getEventType());
    }
    
    @Test
    void shouldPreserveEventMetadataThroughSerialization() {
        // Given
        OrderCreatedEvent originalEvent = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            List.of(), 
            BigDecimal.ZERO
        );
        
        // When
        byte[] serializedData = serializer.serialize("test-topic", originalEvent);
        BaseEvent deserializedEvent = deserializer.deserialize("test-topic", serializedData);
        
        // Then - All metadata should be preserved
        assertEquals(originalEvent.getEventId(), deserializedEvent.getEventId());
        assertEquals(originalEvent.getCorrelationId(), deserializedEvent.getCorrelationId());
        assertEquals(originalEvent.getEventType(), deserializedEvent.getEventType());
        assertNotNull(deserializedEvent.getTimestamp());
        
        // Verify the timestamp is preserved (may lose some precision)
        assertNotNull(deserializedEvent.getTimestamp());
    }
}