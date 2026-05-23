package com.example.authapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger audit = LoggerFactory.getLogger("keyce-audit");

    public void log(String action, String actor, String details) {
        audit.info("ACTION={} actor={} details={}", action, actor, details);
    }
}
