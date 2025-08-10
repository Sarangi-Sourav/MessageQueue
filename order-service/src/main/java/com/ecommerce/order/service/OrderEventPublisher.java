package com.ecommerce.order.service;

import com.ecommerce.shared.config.KafkaTopics;
import com.ecommerce.shared.events.BaseEvent;
import com.ecommerce.shared.events.OrderCancelledEvent;
import com.ecommerce.shared.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    
    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;
    
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreated event for order: {}", event.getOrderId());
        
        CompletableFuture<SendResult<String, BaseEvent>> future = kafkaTemplate.send(
            KafkaTopics.ORDER_CREATED, 
            event.getOrderId(), 
            event
        );
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Successfully published OrderCreated event for order: {} with offset: {}", 
                    event.getOrderId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish OrderCreated event for order: {}", 
                    event.getOrderId(), exception);
            }
        });
    }
    
    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("Publishing OrderCancelled event for order: {}", event.getOrderId());
        
        CompletableFuture<SendResult<String, BaseEvent>> future = kafkaTemplate.send(
            KafkaTopics.ORDER_CANCELLED, 
            event.getOrderId(), 
            event
        );
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Successfully published OrderCancelled event for order: {} with offset: {}", 
                    event.getOrderId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish OrderCancelled event for order: {}", 
                    event.getOrderId(), exception);
            }
        });
    }
}