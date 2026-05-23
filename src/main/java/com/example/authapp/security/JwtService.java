package com.example.authapp.security;

import com.example.authapp.model.AppUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes:120}") long expirationMinutes) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(AppUser user) {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getUsername());
        payload.put("email", user.getEmail());
        payload.put("roles", user.getRoles().stream().map(Enum::name).toList());
        payload.put("iat", now);
        payload.put("exp", now + expirationMinutes * 60);
        String unsigned = encodeJson(header) + "." + encodeJson(payload);
        return unsigned + "." + sign(unsigned);
    }

    public long expirationMinutes() {
        return expirationMinutes;
    }

    public String username(String token) {
        return claims(token).get("sub").toString();
    }

    public boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            String unsigned = parts[0] + "." + parts[1];
            if (!MessageDigestSafe.equals(sign(unsigned), parts[2])) {
                return false;
            }
            Object exp = claims(token).get("exp");
            long expiresAt = exp instanceof Number number ? number.longValue() : Long.parseLong(exp.toString());
            return expiresAt > Instant.now().getEpochSecond();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> roles(String token) {
        Object roles = claims(token).get("roles");
        return roles instanceof List<?> list ? list.stream().map(Object::toString).toList() : List.of();
    }

    private Map<String, Object> claims(String token) {
        try {
            String[] parts = token.split("\\.");
            byte[] bytes = Base64.getUrlDecoder().decode(parts[1]);
            return mapper.readValue(bytes, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JWT", ex);
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(value));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot encode JWT", ex);
        }
    }

    private String sign(String unsigned) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(unsigned.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign JWT", ex);
        }
    }

    private static class MessageDigestSafe {
        static boolean equals(String left, String right) {
            byte[] a = left.getBytes(StandardCharsets.UTF_8);
            byte[] b = right.getBytes(StandardCharsets.UTF_8);
            if (a.length != b.length) return false;
            int result = 0;
            for (int i = 0; i < a.length; i++) {
                result |= a[i] ^ b[i];
            }
            return result == 0;
        }
    }
}
