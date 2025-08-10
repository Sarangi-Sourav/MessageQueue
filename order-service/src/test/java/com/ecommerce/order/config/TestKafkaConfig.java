package com.ecommerce.order.config;

import com.ecommerce.shared.events.BaseEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestKafkaConfig {
    
    @Bean
    @Primary
    public KafkaTemplate<String, BaseEvent> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}