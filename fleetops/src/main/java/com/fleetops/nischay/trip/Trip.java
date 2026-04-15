package com.fleetops.nischay.trip;

import com.fleetops.nischay.common.BaseEntity;
import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.fleet.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "trip", indexes = {
        @Index(name = "idx_trip_status", columnList = "status"),
        @Index(name = "idx_trip_driver", columnList = "driver_id"),
        @Index(name = "idx_trip_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_trip_start_time", columnList = "startTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String source;

    @Column(nullable = false, length = 255)
    private String destination;

    @Column(nullable = false)
    private Instant startTime;

    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TripStatus status = TripStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private String cancellationReason;
}