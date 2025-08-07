package com.ecommerce.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryInsufficientEvent extends BaseEvent {
    
    private String orderId;
    private Map<String, Integer> unavailableItems; // productId -> requested quantity
    private String reason;
    
    public InventoryInsufficientEvent(String correlationId, String orderId, 
                                    Map<String, Integer> unavailableItems, String reason) {
        super(correlationId, "INVENTORY_INSUFFICIENT");
        this.orderId = orderId;
        this.unavailableItems = unavailableItems;
        this.reason = reason;
    }
}