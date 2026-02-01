package com.fleetops.nischay.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Async
    public void notifyDeliveryAssigned(Long deliveryId) {
        // simulate async work (email, SMS, webhook)
        System.out.println("Notifying assignment for delivery: " + deliveryId);
    }
}
