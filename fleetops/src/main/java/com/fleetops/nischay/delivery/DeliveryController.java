package com.fleetops.nischay.delivery;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public String createDelivery() {
        return "Delivery created";
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public String assignDelivery(@PathVariable Long id) {
        return "Delivery assigned";
    }
}
