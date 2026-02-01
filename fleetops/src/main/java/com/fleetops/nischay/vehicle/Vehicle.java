package com.fleetops.nischay.vehicle;

import com.fleetops.nischay.driver.Driver;
import jakarta.persistence.*;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @OneToOne
    @JoinColumn(name = "driver_id")
    private Driver assignedDriver;

}
