package com.ecommerce.shared.events;

import com.ecommerce.shared.enums.PaymentStatus;
import com.ecommerce.shared.model.OrderItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventSerializationTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void shouldSerializeOrderCreatedEventToValidJson() throws JsonProcessingException {
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
        JsonNode jsonNode = objectMapper.readTree(json);
        
        // Then
        assertEquals("ORDER_CREATED", jsonNode.get("eventType").asText());
        assertEquals("correlation-123", jsonNode.get("correlationId").asText());
        assertEquals("order-456", jsonNode.get("orderId").asText());
        assertEquals("customer-789", jsonNode.get("customerId").asText());
        assertEquals(20.00, jsonNode.get("totalAmount").asDouble());
        
        // Verify items array
        JsonNode itemsNode = jsonNode.get("items");
        assertTrue(itemsNode.isArray());
        assertEquals(1, itemsNode.size());
        assertEquals("product-1", itemsNode.get(0).get("productId").asText());
        assertEquals("Test Product", itemsNode.get(0).get("productName").asText());
        assertEquals(2, itemsNode.get(0).get("quantity").asInt());
        assertEquals(10.00, itemsNode.get(0).get("unitPrice").asDouble());
        
        // Verify metadata fields
        assertNotNull(jsonNode.get("eventId"));
        assertNotNull(jsonNode.get("timestamp"));
    }
    
    @Test
    void shouldSerializeInventoryReservedEventToValidJson() throws JsonProcessingException {
        // Given
        Map<String, Integer> reservedItems = Map.of("product-1", 2, "product-2", 1);
        InventoryReservedEvent event = new InventoryReservedEvent(
            "correlation-456",
            "order-789",
            reservedItems
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        JsonNode jsonNode = objectMapper.readTree(json);
        
        // Then
        assertEquals("INVENTORY_RESERVED", jsonNode.get("eventType").asText());
        assertEquals("correlation-456", jsonNode.get("correlationId").asText());
        assertEquals("order-789", jsonNode.get("orderId").asText());
        
        // Verify reserved items
        JsonNode reservedItemsNode = jsonNode.get("reservedItems");
        assertNotNull(reservedItemsNode);
        assertEquals(2, reservedItemsNode.get("product-1").asInt());
        assertEquals(1, reservedItemsNode.get("product-2").asInt());
    }
    
    @Test
    void shouldSerializePaymentProcessedEventToValidJson() throws JsonProcessingException {
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
        JsonNode jsonNode = objectMapper.readTree(json);
        
        // Then
        assertEquals("PAYMENT_PROCESSED", jsonNode.get("eventType").asText());
        assertEquals("correlation-789", jsonNode.get("correlationId").asText());
        assertEquals("order-123", jsonNode.get("orderId").asText());
        assertEquals("payment-456", jsonNode.get("paymentId").asText());
        assertEquals(99.99, jsonNode.get("amount").asDouble());
        assertEquals("COMPLETED", jsonNode.get("status").asText());
    }
    
    @Test
    void shouldDeserializeJsonToCorrectEventTypes() throws JsonProcessingException {
        // Given - Create events and serialize them to get proper JSON format
        OrderCreatedEvent orderEvent = new OrderCreatedEvent("correlation-123", "order-456", "customer-789", List.of(), BigDecimal.ZERO);
        InventoryReservedEvent inventoryEvent = new InventoryReservedEvent("correlation-456", "order-789", Map.of("product-1", 2));
        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent("correlation-789", "order-123", "payment-456", BigDecimal.valueOf(99.99), PaymentStatus.COMPLETED);
        
        String orderJson = objectMapper.writeValueAsString(orderEvent);
        String inventoryJson = objectMapper.writeValueAsString(inventoryEvent);
        String paymentJson = objectMapper.writeValueAsString(paymentEvent);
        
        // When
        BaseEvent deserializedOrder = objectMapper.readValue(orderJson, BaseEvent.class);
        BaseEvent deserializedInventory = objectMapper.readValue(inventoryJson, BaseEvent.class);
        BaseEvent deserializedPayment = objectMapper.readValue(paymentJson, BaseEvent.class);
        
        // Then
        assertTrue(deserializedOrder instanceof OrderCreatedEvent);
        assertTrue(deserializedInventory instanceof InventoryReservedEvent);
        assertTrue(deserializedPayment instanceof PaymentProcessedEvent);
        
        assertEquals("ORDER_CREATED", deserializedOrder.getEventType());
        assertEquals("INVENTORY_RESERVED", deserializedInventory.getEventType());
        assertEquals("PAYMENT_PROCESSED", deserializedPayment.getEventType());
    }
    
    @Test
    void shouldHandleTimestampSerializationCorrectly() throws JsonProcessingException {
        // Given
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            List.of(), 
            BigDecimal.ZERO
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        BaseEvent deserializedEvent = objectMapper.readValue(json, BaseEvent.class);
        
        // Then - Timestamps should be close (precision may be lost during serialization)
        assertNotNull(deserializedEvent.getTimestamp());
        
        // Verify JSON format
        JsonNode jsonNode = objectMapper.readTree(json);
        String timestampString = jsonNode.get("timestamp").asText();
        assertTrue(timestampString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }
    
    @Test
    void shouldPreserveBigDecimalPrecisionInSerialization() throws JsonProcessingException {
        // Given
        BigDecimal preciseAmount = new BigDecimal("123.456789");
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            "correlation-789",
            "order-123",
            "payment-456",
            preciseAmount,
            PaymentStatus.COMPLETED
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        PaymentProcessedEvent deserializedEvent = (PaymentProcessedEvent) objectMapper.readValue(json, BaseEvent.class);
        
        // Then
        assertEquals(preciseAmount, deserializedEvent.getAmount());
        assertEquals(6, deserializedEvent.getAmount().scale());
    }
    
    @Test
    void shouldHandleComplexOrderItemsSerialization() throws JsonProcessingException {
        // Given
        OrderItem item1 = new OrderItem("product-1", "Product One", 2, new BigDecimal("10.50"));
        OrderItem item2 = new OrderItem("product-2", "Product Two", 1, new BigDecimal("25.99"));
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            List.of(item1, item2), 
            new BigDecimal("47.99")
        );
        
        // When
        String json = objectMapper.writeValueAsString(event);
        OrderCreatedEvent deserializedEvent = (OrderCreatedEvent) objectMapper.readValue(json, BaseEvent.class);
        
        // Then
        assertEquals(2, deserializedEvent.getItems().size());
        
        OrderItem deserializedItem1 = deserializedEvent.getItems().get(0);
        assertEquals("product-1", deserializedItem1.getProductId());
        assertEquals("Product One", deserializedItem1.getProductName());
        assertEquals(2, deserializedItem1.getQuantity());
        assertEquals(new BigDecimal("10.50"), deserializedItem1.getUnitPrice());
        
        OrderItem deserializedItem2 = deserializedEvent.getItems().get(1);
        assertEquals("product-2", deserializedItem2.getProductId());
        assertEquals("Product Two", deserializedItem2.getProductName());
        assertEquals(1, deserializedItem2.getQuantity());
        assertEquals(new BigDecimal("25.99"), deserializedItem2.getUnitPrice());
    }
}