package com.ecommerce.order.service;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.OrderStatusResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItemEntity;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.shared.enums.OrderStatus;
import com.ecommerce.shared.events.OrderCreatedEvent;
import com.ecommerce.shared.model.OrderItem;
import com.ecommerce.shared.util.CorrelationIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        // Generate order ID
        String orderId = UUID.randomUUID().toString();
        
        // Calculate total amount
        BigDecimal totalAmount = request.getItems().stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Convert OrderItem to OrderItemEntity
        List<OrderItemEntity> orderItems = request.getItems().stream()
            .map(item -> new OrderItemEntity(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
            ))
            .collect(Collectors.toList());
        
        // Create and save order
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(request.getCustomerId());
        order.setItems(orderItems);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", orderId);
        
        // Publish OrderCreated event
        String correlationId = CorrelationIdGenerator.generate();
        OrderCreatedEvent event = new OrderCreatedEvent(
            correlationId,
            orderId,
            request.getCustomerId(),
            request.getItems(),
            totalAmount
        );
        
        orderEventPublisher.publishOrderCreated(event);
        
        return mapToOrderResponse(savedOrder);
    }
    
    public Optional<OrderResponse> getOrder(String orderId) {
        log.info("Retrieving order with ID: {}", orderId);
        return orderRepository.findById(orderId)
            .map(this::mapToOrderResponse);
    }
    
    public Optional<OrderStatusResponse> getOrderStatus(String orderId) {
        log.info("Retrieving order status for ID: {}", orderId);
        return orderRepository.findById(orderId)
            .map(order -> new OrderStatusResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getUpdatedAt()
            ));
    }
    
    public List<OrderResponse> getOrdersByCustomer(String customerId) {
        log.info("Retrieving orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId).stream()
            .map(this::mapToOrderResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateOrderStatus(String orderId, OrderStatus status) {
        log.info("Updating order {} status to: {}", orderId, status);
        orderRepository.findById(orderId)
            .ifPresent(order -> {
                order.setStatus(status);
                orderRepository.save(order);
                log.info("Order {} status updated to: {}", orderId, status);
            });
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        // Convert OrderItemEntity back to OrderItem for response
        List<OrderItem> items = order.getItems().stream()
            .map(entity -> new OrderItem(
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getUnitPrice()
            ))
            .collect(Collectors.toList());
        
        return new OrderResponse(
            order.getOrderId(),
            order.getCustomerId(),
            items,
            order.getStatus(),
            order.getTotalAmount(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}