package com.example.authapp.dto;

public record PaymentResult(
        boolean approved,
        String transactionId,
        String last4,
        String message
) {
}
