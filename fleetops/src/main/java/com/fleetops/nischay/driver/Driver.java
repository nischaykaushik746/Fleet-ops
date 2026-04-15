package com.fleetops.nischay.driver;

import com.fleetops.nischay.common.BaseEntity;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver", indexes = {
        @Index(name = "idx_driver_status", columnList = "status"),
        @Index(name = "idx_driver_user", columnList = "user_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 20)
    private String licenseNumber;

    @Column(length = 15)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DriverStatus status = DriverStatus.AVAILABLE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_vehicle_id")
    private Vehicle assignedVehicle;
}