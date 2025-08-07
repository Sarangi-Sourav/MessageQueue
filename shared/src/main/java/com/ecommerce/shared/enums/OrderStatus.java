package com.ecommerce.shared.enums;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    INVENTORY_INSUFFICIENT,
    PAYMENT_PROCESSED,
    PAYMENT_FAILED,
    CANCELLED,
    COMPLETED
}