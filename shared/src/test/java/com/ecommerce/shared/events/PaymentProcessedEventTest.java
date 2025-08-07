package com.ecommerce.shared.events;

import com.ecommerce.shared.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessedEventTest {
    
    @Test
    void shouldCreatePaymentProcessedEventWithValidData() {
        // Given
        String correlationId = "correlation-789";
        String orderId = "order-123";
        String paymentId = "payment-456";
        BigDecimal amount = BigDecimal.valueOf(99.99);
        PaymentStatus status = PaymentStatus.COMPLETED;
        
        // When
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            correlationId, orderId, paymentId, amount, status
        );
        
        // Then
        assertEquals(correlationId, event.getCorrelationId());
        assertEquals(orderId, event.getOrderId());
        assertEquals(paymentId, event.getPaymentId());
        assertEquals(amount, event.getAmount());
        assertEquals(status, event.getStatus());
        assertEquals("PAYMENT_PROCESSED", event.getEventType());
        assertNotNull(event.getEventId());
        assertNotNull(event.getTimestamp());
    }
    
    @Test
    void shouldCreatePaymentProcessedEventWithDifferentStatuses() {
        // Test COMPLETED status
        PaymentProcessedEvent completedEvent = new PaymentProcessedEvent(
            "correlation-1", "order-1", "payment-1", BigDecimal.TEN, PaymentStatus.COMPLETED
        );
        assertEquals(PaymentStatus.COMPLETED, completedEvent.getStatus());
        
        // Test PENDING status
        PaymentProcessedEvent pendingEvent = new PaymentProcessedEvent(
            "correlation-2", "order-2", "payment-2", BigDecimal.ONE, PaymentStatus.PENDING
        );
        assertEquals(PaymentStatus.PENDING, pendingEvent.getStatus());
        
        // Test FAILED status
        PaymentProcessedEvent failedEvent = new PaymentProcessedEvent(
            "correlation-3", "order-3", "payment-3", BigDecimal.ZERO, PaymentStatus.FAILED
        );
        assertEquals(PaymentStatus.FAILED, failedEvent.getStatus());
    }
    
    @Test
    void shouldCreatePaymentProcessedEventWithZeroAmount() {
        // When
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            "correlation-789", 
            "order-123", 
            "payment-456", 
            BigDecimal.ZERO, 
            PaymentStatus.COMPLETED
        );
        
        // Then
        assertEquals(BigDecimal.ZERO, event.getAmount());
    }
    
    @Test
    void shouldCreatePaymentProcessedEventWithLargeAmount() {
        // Given
        BigDecimal largeAmount = new BigDecimal("999999.99");
        
        // When
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            "correlation-789", 
            "order-123", 
            "payment-456", 
            largeAmount, 
            PaymentStatus.COMPLETED
        );
        
        // Then
        assertEquals(largeAmount, event.getAmount());
    }
    
    @Test
    void shouldHandleNullValues() {
        // When
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            "correlation-789", 
            null, 
            null, 
            null, 
            null
        );
        
        // Then
        assertEquals("correlation-789", event.getCorrelationId());
        assertNull(event.getOrderId());
        assertNull(event.getPaymentId());
        assertNull(event.getAmount());
        assertNull(event.getStatus());
    }
    
    @Test
    void shouldPreserveAmountPrecision() {
        // Given
        BigDecimal preciseAmount = new BigDecimal("123.456789");
        
        // When
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            "correlation-789", 
            "order-123", 
            "payment-456", 
            preciseAmount, 
            PaymentStatus.COMPLETED
        );
        
        // Then
        assertEquals(preciseAmount, event.getAmount());
        assertEquals(6, event.getAmount().scale());
    }
}