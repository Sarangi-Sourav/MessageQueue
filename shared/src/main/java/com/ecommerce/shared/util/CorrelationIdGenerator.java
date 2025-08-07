package com.ecommerce.shared.util;

import java.util.UUID;

public final class CorrelationIdGenerator {
    
    private CorrelationIdGenerator() {
        // Utility class
    }
    
    public static String generate() {
        return UUID.randomUUID().toString();
    }
    
    public static String generateFromOrderId(String orderId) {
        return "order-" + orderId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}