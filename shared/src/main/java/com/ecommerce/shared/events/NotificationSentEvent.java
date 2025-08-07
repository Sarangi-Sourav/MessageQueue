package com.ecommerce.shared.events;

import com.ecommerce.shared.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationSentEvent extends BaseEvent {
    
    private String orderId;
    private String customerId;
    private NotificationType notificationType;
    private String message;
    private boolean successful;
    
    public NotificationSentEvent(String correlationId, String orderId, String customerId,
                               NotificationType notificationType, String message, boolean successful) {
        super(correlationId, "NOTIFICATION_SENT");
        this.orderId = orderId;
        this.customerId = customerId;
        this.notificationType = notificationType;
        this.message = message;
        this.successful = successful;
    }
}