package com.fleetops.nischay.delivery;

import com.fleetops.nischay.notification.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private final NotificationService notificationService;

    public DeliveryService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void assignDelivery(Long deliveryId) {
        // business logic later
        notificationService.notifyDeliveryAssigned(deliveryId);
    }
}
