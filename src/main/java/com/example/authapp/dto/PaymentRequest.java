package com.example.authapp.dto;

public record PaymentRequest(
        String paymentMethod,
        String cardholderName,
        String cardNumber,
        String expiryMonth,
        String expiryYear,
        String cvv,
        String paypalEmail,
        String mobileMoneyPhone
) {
}
