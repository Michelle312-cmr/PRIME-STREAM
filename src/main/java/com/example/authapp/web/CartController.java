package com.example.authapp.web;

import com.example.authapp.model.AppUser;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Authentication authentication, Model model) {
        model.addAttribute("cart", cartService.summary(session, currentUser(authentication)));
        return "cart/cart";
    }

    @PostMapping("/api/cart/add/{id}")
    @ResponseBody
    public Object add(@PathVariable Long id, @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                      HttpSession session, Authentication authentication) {
        cartService.add(session, id, quantity);
        return cartService.summary(session, currentUser(authentication));
    }

    @PostMapping("/api/cart/update/{id}")
    @ResponseBody
    public Object update(@PathVariable Long id, @RequestParam("quantity") int quantity,
                         HttpSession session, Authentication authentication) {
        cartService.update(session, id, quantity);
        return cartService.summary(session, currentUser(authentication));
    }

    @PostMapping("/api/cart/coupon")
    @ResponseBody
    public Object coupon(@RequestParam("coupon") String coupon, HttpSession session, Authentication authentication) {
        cartService.applyCoupon(session, coupon);
        return cartService.summary(session, currentUser(authentication));
    }

    private AppUser currentUser(Authentication authentication) {
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
