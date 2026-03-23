package com.fleetops.nischay.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditService {

    public void logAction(String username, String action, String details) {

        log.info("AUDIT | USER={} | ACTION={} | DETAILS={}",
                username, action, details);
    }
}