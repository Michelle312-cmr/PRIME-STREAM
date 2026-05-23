package com.example.authapp.service;

import com.example.authapp.dto.PaymentRequest;
import com.example.authapp.dto.PaymentResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class PaymentService {

    public PaymentResult charge(PaymentRequest request, BigDecimal amount) {
        String method = normalize(request.paymentMethod());
        if ("Carte bancaire sandbox".equals(method)) {
            return chargeCard(request, amount);
        }
        if ("PayPal sandbox".equals(method)) {
            return chargePaypal(request, amount);
        }
        if ("Mobile Money sandbox".equals(method)) {
            return chargeMobileMoney(request, amount);
        }
        if ("Paiement a la livraison".equals(method)) {
            return new PaymentResult(true, "COD-" + shortId(), "", "Paiement a la livraison confirme.");
        }
        return new PaymentResult(false, null, null, "Mode de paiement inconnu.");
    }

    private PaymentResult chargeCard(PaymentRequest request, BigDecimal amount) {
        String number = digits(request.cardNumber());
        if (isBlank(request.cardholderName())) {
            return failed("Le nom du titulaire est obligatoire.");
        }
        if (number.length() < 13 || number.length() > 19 || !passesLuhn(number)) {
            return failed("Numero de carte invalide.");
        }
        if (!validExpiry(request.expiryMonth(), request.expiryYear())) {
            return failed("Date d'expiration invalide ou depassee.");
        }
        if (digits(request.cvv()).length() < 3 || digits(request.cvv()).length() > 4) {
            return failed("CVV invalide.");
        }
        if (number.endsWith("0002")) {
            return failed("Paiement refuse par la banque sandbox.");
        }
        if (amount.compareTo(new BigDecimal("2000000")) > 0) {
            return failed("Montant superieur au plafond sandbox.");
        }
        return new PaymentResult(true, "CARD-" + shortId(), last4(number), "Paiement carte approuve.");
    }

    private PaymentResult chargePaypal(PaymentRequest request, BigDecimal amount) {
        String email = request.paypalEmail();
        if (isBlank(email) || !email.contains("@") || !email.contains(".")) {
            return failed("Email PayPal invalide.");
        }
        if (email.toLowerCase().contains("fail")) {
            return failed("Compte PayPal sandbox refuse.");
        }
        return new PaymentResult(true, "PAYPAL-" + shortId(), "", "Paiement PayPal approuve.");
    }

    private PaymentResult chargeMobileMoney(PaymentRequest request, BigDecimal amount) {
        String phone = digits(request.mobileMoneyPhone());
        if (phone.length() < 8 || phone.length() > 14) {
            return failed("Numero Mobile Money invalide.");
        }
        if (phone.endsWith("0000")) {
            return failed("Solde Mobile Money sandbox insuffisant.");
        }
        return new PaymentResult(true, "MOMO-" + shortId(), "", "Paiement Mobile Money approuve.");
    }

    private PaymentResult failed(String message) {
        return new PaymentResult(false, null, null, message);
    }

    private boolean validExpiry(String monthValue, String yearValue) {
        try {
            int month = Integer.parseInt(monthValue);
            int year = Integer.parseInt(yearValue);
            if (year < 100) {
                year += 2000;
            }
            YearMonth expiry = YearMonth.of(year, month);
            return !expiry.isBefore(YearMonth.now());
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private boolean passesLuhn(String number) {
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.digit(number.charAt(i), 10);
            if (digit < 0) {
                return false;
            }
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String digits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String last4(String value) {
        return value.substring(Math.max(0, value.length() - 4));
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
