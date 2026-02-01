package com.fleetops.nischay.delivery;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.hub.Hub;
import com.fleetops.nischay.user.User;
import jakarta.persistence.*;

@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

}
