package com.ecommerce.order.dto;

import com.ecommerce.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
    
    private String orderId;
    private OrderStatus status;
    private LocalDateTime updatedAt;
}