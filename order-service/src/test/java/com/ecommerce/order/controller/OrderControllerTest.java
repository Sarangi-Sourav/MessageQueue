package com.ecommerce.order.controller;

import com.ecommerce.order.config.TestKafkaConfig;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.OrderStatusResponse;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.shared.enums.OrderStatus;
import com.ecommerce.shared.model.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(TestKafkaConfig.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private CreateOrderRequest createOrderRequest;
    private OrderResponse orderResponse;
    private OrderStatusResponse orderStatusResponse;
    
    @BeforeEach
    void setUp() {
        OrderItem item = new OrderItem("prod-1", "Product 1", 2, new BigDecimal("10.00"));
        
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId("customer-123");
        createOrderRequest.setItems(Arrays.asList(item));
        
        orderResponse = new OrderResponse();
        orderResponse.setOrderId("order-123");
        orderResponse.setCustomerId("customer-123");
        orderResponse.setItems(Arrays.asList(item));
        orderResponse.setStatus(OrderStatus.CREATED);
        orderResponse.setTotalAmount(new BigDecimal("20.00"));
        orderResponse.setCreatedAt(LocalDateTime.now());
        orderResponse.setUpdatedAt(LocalDateTime.now());
        
        orderStatusResponse = new OrderStatusResponse();
        orderStatusResponse.setOrderId("order-123");
        orderStatusResponse.setStatus(OrderStatus.CREATED);
        orderStatusResponse.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void createOrder_ShouldReturnAcceptedWithOrderResponse() throws Exception {
        // Given
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);
        
        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.customerId").value("customer-123"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(20.00));
    }
    
    @Test
    void createOrder_ShouldReturnBadRequestForInvalidRequest() throws Exception {
        // Given - invalid request with empty customer ID
        createOrderRequest.setCustomerId("");
        
        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getOrder_ShouldReturnOrderWhenExists() throws Exception {
        // Given
        when(orderService.getOrder("order-123")).thenReturn(Optional.of(orderResponse));
        
        // When & Then
        mockMvc.perform(get("/orders/order-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.customerId").value("customer-123"));
    }
    
    @Test
    void getOrder_ShouldReturnNotFoundWhenNotExists() throws Exception {
        // Given
        when(orderService.getOrder("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/orders/non-existent"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getOrderStatus_ShouldReturnStatusWhenExists() throws Exception {
        // Given
        when(orderService.getOrderStatus("order-123")).thenReturn(Optional.of(orderStatusResponse));
        
        // When & Then
        mockMvc.perform(get("/orders/order-123/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}