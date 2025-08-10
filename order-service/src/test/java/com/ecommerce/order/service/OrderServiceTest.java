package com.ecommerce.order.service;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItemEntity;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.shared.enums.OrderStatus;
import com.ecommerce.shared.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderEventPublisher orderEventPublisher;
    
    @InjectMocks
    private OrderService orderService;
    
    private CreateOrderRequest createOrderRequest;
    private Order savedOrder;
    
    @BeforeEach
    void setUp() {
        OrderItem item1 = new OrderItem("prod-1", "Product 1", 2, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem("prod-2", "Product 2", 1, new BigDecimal("15.00"));
        
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId("customer-123");
        createOrderRequest.setItems(Arrays.asList(item1, item2));
        
        // Create OrderItemEntity list for the saved order
        OrderItemEntity entity1 = new OrderItemEntity("prod-1", "Product 1", 2, new BigDecimal("10.00"));
        OrderItemEntity entity2 = new OrderItemEntity("prod-2", "Product 2", 1, new BigDecimal("15.00"));
        
        savedOrder = new Order();
        savedOrder.setOrderId("order-123");
        savedOrder.setCustomerId("customer-123");
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(new BigDecimal("35.00"));
        savedOrder.setItems(Arrays.asList(entity1, entity2));
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        // When
        OrderResponse response = orderService.createOrder(createOrderRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("order-123", response.getOrderId());
        assertEquals("customer-123", response.getCustomerId());
        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertEquals(new BigDecimal("35.00"), response.getTotalAmount());
        assertEquals(2, response.getItems().size());
        
        verify(orderRepository).save(any(Order.class));
        verify(orderEventPublisher).publishOrderCreated(any());
    }
    
    @Test
    void getOrder_ShouldReturnOrderWhenExists() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(savedOrder));
        
        // When
        Optional<OrderResponse> response = orderService.getOrder("order-123");
        
        // Then
        assertTrue(response.isPresent());
        assertEquals("order-123", response.get().getOrderId());
        assertEquals("customer-123", response.get().getCustomerId());
        
        verify(orderRepository).findById("order-123");
    }
    
    @Test
    void getOrder_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(orderRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When
        Optional<OrderResponse> response = orderService.getOrder("non-existent");
        
        // Then
        assertFalse(response.isPresent());
        
        verify(orderRepository).findById("non-existent");
    }
    
    @Test
    void updateOrderStatus_ShouldUpdateStatusWhenOrderExists() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        // When
        orderService.updateOrderStatus("order-123", OrderStatus.PAYMENT_PROCESSED);
        
        // Then
        verify(orderRepository).findById("order-123");
        verify(orderRepository).save(any(Order.class));
    }
}