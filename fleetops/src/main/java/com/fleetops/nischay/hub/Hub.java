package com.fleetops.nischay.hub;

import jakarta.persistence.*;

@Entity
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;

}