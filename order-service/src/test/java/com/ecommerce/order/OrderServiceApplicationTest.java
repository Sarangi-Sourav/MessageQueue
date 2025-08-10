package com.ecommerce.order;

import com.ecommerce.order.config.TestKafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class OrderServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
    }
}