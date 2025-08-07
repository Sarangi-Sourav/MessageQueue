package com.ecommerce.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCancelledEvent extends BaseEvent {
    
    private String orderId;
    private String reason;
    
    public OrderCancelledEvent(String correlationId, String orderId, String reason) {
        super(correlationId, "ORDER_CANCELLED");
        this.orderId = orderId;
        this.reason = reason;
    }
}