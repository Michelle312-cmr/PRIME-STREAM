package com.example.authapp.web;

import com.example.authapp.dto.PaymentRequest;
import com.example.authapp.model.AppUser;
import com.example.authapp.service.PaymentService;
import com.example.authapp.service.PrimeStreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PrimeStreamService primeStreamService;
    private final PaymentService paymentService;

    public PaymentController(PrimeStreamService primeStreamService, PaymentService paymentService) {
        this.primeStreamService = primeStreamService;
        this.paymentService = paymentService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload, Authentication authentication) {
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentification requise"));
        }
        String plan = payload.getOrDefault("plan", "Gratuit");
        String[] expiry = payload.getOrDefault("exp", "").split("/");
        String month = expiry.length > 0 ? expiry[0].trim() : "";
        String year = expiry.length > 1 ? expiry[1].trim() : "";
        var amount = "Premium".equalsIgnoreCase(plan)
                ? new java.math.BigDecimal("14.99")
                : new java.math.BigDecimal("9.99");

        var result = paymentService.charge(new PaymentRequest(
                "Carte bancaire sandbox",
                payload.getOrDefault("name", ""),
                payload.getOrDefault("cardNumber", ""),
                month,
                year,
                payload.getOrDefault("cvv", ""),
                null,
                null
        ), amount);
        if (!result.approved()) {
            return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", result.message()));
        }

        AppUser user = primeStreamService.currentUser(authentication.getName());
        try {
            primeStreamService.subscribe(user, plan);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Impossible d'activer l'abonnement"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "transactionId", result.transactionId(),
                "message", result.message(),
                "redirect", "/profile?subscribed=true"
        ));
    }
}
