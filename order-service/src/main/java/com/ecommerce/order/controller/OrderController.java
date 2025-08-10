package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.OrderStatusResponse;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Received order creation request for customer: {}", request.getCustomerId());
        
        try {
            OrderResponse response = orderService.createOrder(request);
            log.info("Order created successfully with ID: {}", response.getOrderId());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            log.error("Failed to create order for customer: {}", request.getCustomerId(), e);
            throw e;
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        log.info("Received request to get order: {}", orderId);
        
        return orderService.getOrder(orderId)
            .map(order -> {
                log.info("Order found: {}", orderId);
                return ResponseEntity.ok(order);
            })
            .orElseGet(() -> {
                log.warn("Order not found: {}", orderId);
                return ResponseEntity.notFound().build();
            });
    }
    
    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable String orderId) {
        log.info("Received request to get order status: {}", orderId);
        
        return orderService.getOrderStatus(orderId)
            .map(status -> {
                log.info("Order status found for: {}", orderId);
                return ResponseEntity.ok(status);
            })
            .orElseGet(() -> {
                log.warn("Order not found for status check: {}", orderId);
                return ResponseEntity.notFound().build();
            });
    }
    
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @RequestParam String customerId) {
        log.info("Received request to get orders for customer: {}", customerId);
        
        List<OrderResponse> orders = orderService.getOrdersByCustomer(customerId);
        log.info("Found {} orders for customer: {}", orders.size(), customerId);
        
        return ResponseEntity.ok(orders);
    }
}