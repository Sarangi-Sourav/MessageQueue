package com.ecommerce.shared.config;

public final class KafkaTopics {
    
    // Order related topics
    public static final String ORDER_CREATED = "order-created";
    public static final String ORDER_CANCELLED = "order-cancelled";
    
    // Inventory related topics
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_INSUFFICIENT = "inventory-insufficient";
    
    // Payment related topics
    public static final String PAYMENT_PROCESSED = "payment-processed";
    public static final String PAYMENT_FAILED = "payment-failed";
    
    // Notification related topics
    public static final String NOTIFICATION_SENT = "notification-sent";
    
    // Dead letter topics
    public static final String ORDER_DLT = "order-created-dlt";
    public static final String INVENTORY_DLT = "inventory-reserved-dlt";
    public static final String PAYMENT_DLT = "payment-processed-dlt";
    public static final String NOTIFICATION_DLT = "notification-sent-dlt";
    
    private KafkaTopics() {
        // Utility class
    }
}