package com.fleetops.nischay.user;

import jakarta.persistence.*;

@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
