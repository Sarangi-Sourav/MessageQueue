package com.ecommerce.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentFailedEvent extends BaseEvent {
    
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    private String failureReason;
    
    public PaymentFailedEvent(String correlationId, String orderId, String paymentId, 
                            BigDecimal amount, String failureReason) {
        super(correlationId, "PAYMENT_FAILED");
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.failureReason = failureReason;
    }
}