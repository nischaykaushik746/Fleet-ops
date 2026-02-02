package com.fleetops.nischay.delivery;

import com.fleetops.nischay.driver.*;
import com.fleetops.nischay.notification.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final NotificationService notificationService;

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            DriverRepository driverRepository,
            NotificationService notificationService) {

        this.deliveryRepository = deliveryRepository;
        this.driverRepository = driverRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void assignDelivery(Long deliveryId, Long driverId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver not available");
        }

        delivery.setDriver(driver);
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        driver.setStatus(DriverStatus.ON_TRIP);

        notificationService.notifyDeliveryAssigned(deliveryId);
    }
}
