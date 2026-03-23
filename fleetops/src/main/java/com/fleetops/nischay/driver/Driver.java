package com.fleetops.nischay.driver;

import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @OneToOne
    private Vehicle assignedVehicle;
}