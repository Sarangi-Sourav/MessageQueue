package com.ecommerce.shared.events;

import com.ecommerce.shared.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreatedEventTest {
    
    @Test
    void shouldCreateOrderCreatedEventWithValidData() {
        // Given
        String correlationId = "correlation-123";
        String orderId = "order-456";
        String customerId = "customer-789";
        OrderItem item = new OrderItem("product-1", "Test Product", 2, BigDecimal.valueOf(10.00));
        List<OrderItem> items = List.of(item);
        BigDecimal totalAmount = BigDecimal.valueOf(20.00);
        
        // When
        OrderCreatedEvent event = new OrderCreatedEvent(correlationId, orderId, customerId, items, totalAmount);
        
        // Then
        assertEquals(correlationId, event.getCorrelationId());
        assertEquals(orderId, event.getOrderId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(items, event.getItems());
        assertEquals(totalAmount, event.getTotalAmount());
        assertEquals("ORDER_CREATED", event.getEventType());
        assertNotNull(event.getEventId());
        assertNotNull(event.getTimestamp());
    }
    
    @Test
    void shouldCreateOrderCreatedEventWithEmptyItems() {
        // Given
        List<OrderItem> emptyItems = List.of();
        
        // When
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            emptyItems, 
            BigDecimal.ZERO
        );
        
        // Then
        assertTrue(event.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, event.getTotalAmount());
    }
    
    @Test
    void shouldCreateOrderCreatedEventWithMultipleItems() {
        // Given
        OrderItem item1 = new OrderItem("product-1", "Product 1", 2, BigDecimal.valueOf(10.00));
        OrderItem item2 = new OrderItem("product-2", "Product 2", 1, BigDecimal.valueOf(15.00));
        List<OrderItem> items = List.of(item1, item2);
        BigDecimal totalAmount = BigDecimal.valueOf(35.00);
        
        // When
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            "customer-789", 
            items, 
            totalAmount
        );
        
        // Then
        assertEquals(2, event.getItems().size());
        assertEquals(totalAmount, event.getTotalAmount());
        assertEquals("product-1", event.getItems().get(0).getProductId());
        assertEquals("product-2", event.getItems().get(1).getProductId());
    }
    
    @Test
    void shouldHandleNullValues() {
        // When
        OrderCreatedEvent event = new OrderCreatedEvent(
            "correlation-123", 
            "order-456", 
            null, 
            null, 
            null
        );
        
        // Then
        assertEquals("correlation-123", event.getCorrelationId());
        assertEquals("order-456", event.getOrderId());
        assertNull(event.getCustomerId());
        assertNull(event.getItems());
        assertNull(event.getTotalAmount());
    }
}