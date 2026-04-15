package com.fleetops.nischay.fleet;

import com.fleetops.nischay.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle", indexes = {
        @Index(name = "idx_vehicle_number", columnList = "vehicleNumber", unique = true),
        @Index(name = "idx_vehicle_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String vehicleNumber;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;
}