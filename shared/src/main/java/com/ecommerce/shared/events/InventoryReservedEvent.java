package com.ecommerce.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservedEvent extends BaseEvent {
    
    private String orderId;
    private Map<String, Integer> reservedItems; // productId -> quantity
    
    public InventoryReservedEvent(String correlationId, String orderId, Map<String, Integer> reservedItems) {
        super(correlationId, "INVENTORY_RESERVED");
        this.orderId = orderId;
        this.reservedItems = reservedItems;
    }
}