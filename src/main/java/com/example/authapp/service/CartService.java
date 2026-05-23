package com.example.authapp.service;

import com.example.authapp.dto.CartLine;
import com.example.authapp.dto.CartSummary;
import com.example.authapp.model.AppUser;
import com.example.authapp.repo.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {
    private static final String CART = "cart";
    private static final String COUPON = "coupon";

    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<Long, Integer> cart(HttpSession session) {
        Object existing = session.getAttribute(CART);
        if (existing instanceof Map<?, ?> map) {
            Map<Long, Integer> typed = new HashMap<>();
            map.forEach((key, value) -> typed.put(Long.valueOf(key.toString()), Integer.valueOf(value.toString())));
            session.setAttribute(CART, typed);
            return typed;
        }
        Map<Long, Integer> cart = new HashMap<>();
        session.setAttribute(CART, cart);
        return cart;
    }

    public void add(HttpSession session, Long productId, int quantity) {
        var cart = cart(session);
        cart.merge(productId, Math.max(1, quantity), Integer::sum);
    }

    public void update(HttpSession session, Long productId, int quantity) {
        var cart = cart(session);
        if (quantity <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, quantity);
        }
    }

    public void applyCoupon(HttpSession session, String coupon) {
        session.setAttribute(COUPON, coupon == null ? "" : coupon.trim().toUpperCase());
    }

    public void clear(HttpSession session) {
        session.removeAttribute(CART);
        session.removeAttribute(COUPON);
    }

    public CartSummary summary(HttpSession session, AppUser user) {
        var lines = cart(session).entrySet().stream()
                .flatMap(entry -> productRepository.findById(entry.getKey()).stream()
                        .map(product -> {
                            if (product.getPrice() == null) {
                                product.setPrice(BigDecimal.ZERO);
                            }
                            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()))
                                    .setScale(2, RoundingMode.HALF_UP);
                            return new CartLine(product, entry.getValue(), lineTotal);
                        }))
                .toList();
        
        BigDecimal subtotal = lines.stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        String coupon = String.valueOf(session.getAttribute(COUPON) == null ? "" : session.getAttribute(COUPON));
        BigDecimal discount = "PRIME20".equals(coupon) ? 
                subtotal.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        BigDecimal taxBase = subtotal.subtract(discount);
        BigDecimal tax = taxBase.multiply(new BigDecimal("0.1925"))
                .setScale(2, RoundingMode.HALF_UP);
        
        boolean isPrimeMember = user != null && user.isPrimeMember();
        boolean freeShipping = isPrimeMember || subtotal.compareTo(new BigDecimal("100")) >= 0;
        BigDecimal shipping = freeShipping ? BigDecimal.ZERO : new BigDecimal("6.99");
        
        BigDecimal total = subtotal.subtract(discount).add(tax).add(shipping)
                .setScale(2, RoundingMode.HALF_UP);
        
        int itemCount = lines.stream().mapToInt(CartLine::quantity).sum();
        
        return new CartSummary(lines, subtotal, tax, shipping, discount, total, itemCount, coupon);
    }
}
