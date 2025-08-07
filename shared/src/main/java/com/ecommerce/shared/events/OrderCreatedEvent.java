package com.ecommerce.shared.events;

import com.ecommerce.shared.model.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends BaseEvent {
    
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    
    public OrderCreatedEvent(String correlationId, String orderId, String customerId, 
                           List<OrderItem> items, BigDecimal totalAmount) {
        super(correlationId, "ORDER_CREATED");
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
    }
}