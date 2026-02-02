package com.fleetops.nischay.analytics;

import com.fleetops.nischay.delivery.DeliveryRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final DeliveryRepository deliveryRepository;

    public AnalyticsService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public DeliveryStatsDto getDeliveryStats() {
        return deliveryRepository.fetchDeliveryStats();
    }
}
