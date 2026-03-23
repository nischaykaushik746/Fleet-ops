package com.fleetops.nischay.trip;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.fleet.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String destination;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @ManyToOne
    private Driver driver;

    @ManyToOne
    private Vehicle vehicle;
}