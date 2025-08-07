package com.ecommerce.shared.events;

import com.ecommerce.shared.enums.PaymentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentProcessedEvent extends BaseEvent {
    
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus status;
    
    public PaymentProcessedEvent(String correlationId, String orderId, String paymentId, 
                               BigDecimal amount, PaymentStatus status) {
        super(correlationId, "PAYMENT_PROCESSED");
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
    }
}