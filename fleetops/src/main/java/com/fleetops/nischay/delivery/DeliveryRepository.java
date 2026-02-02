package com.fleetops.nischay.delivery;

import com.fleetops.nischay.analytics.DeliveryStatsDto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("""
        SELECT new com.fleetops.nischay.analytics.DeliveryStatsDto(
            COUNT(d),
            SUM(CASE WHEN d.status = 'DELIVERED' THEN 1 ELSE 0 END),
            SUM(CASE WHEN d.status = 'FAILED' THEN 1 ELSE 0 END)
        )
        FROM Delivery d
    """)
    DeliveryStatsDto fetchDeliveryStats();
}
