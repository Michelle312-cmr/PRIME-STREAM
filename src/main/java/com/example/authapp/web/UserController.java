package com.example.authapp.web;

import com.example.authapp.model.WishlistItem;
import com.example.authapp.repo.OrderRepository;
import com.example.authapp.repo.ProductRepository;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.repo.WishlistRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public UserController(UserRepository userRepository, OrderRepository orderRepository,
                          WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/user/home")
    public String userHome(Authentication authentication, Model model) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("title", "Dashboard client");
        model.addAttribute("who", user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("orders", orderRepository.findByCustomerOrderByCreatedAtDesc(user));
        model.addAttribute("wishlist", wishlistRepository.findByUserOrderByPositionAscCreatedAtDesc(user));
        return "user/home";
    }

    @PostMapping("/user/profile")
    public String profile(@RequestParam("fullName") String fullName,
                          @RequestParam("phone") String phone,
                          @RequestParam("avatarUrl") String avatarUrl,
                          Authentication authentication, RedirectAttributes redirectAttributes) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Profil mis a jour.");
        return "redirect:/user/home";
    }

    @PostMapping("/prime/subscribe")
    public String subscribe(@RequestParam("plan") String plan, Authentication authentication, RedirectAttributes redirectAttributes) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        user.setPrimeMember(true);
        user.setPrimePlan(plan);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Bienvenue dans Prime " + plan + " : livraison gratuite activee.");
        return "redirect:/user/home";
    }

    @PostMapping("/prime/cancel")
    public String cancel(Authentication authentication, RedirectAttributes redirectAttributes) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        user.setPrimeMember(false);
        user.setPrimePlan(null);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Abonnement Prime annule.");
        return "redirect:/user/home";
    }

    @PostMapping("/wishlist/{productId}")
    public String wishlist(@PathVariable Long productId, Authentication authentication, RedirectAttributes redirectAttributes) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        var product = productRepository.findById(productId).orElseThrow();
        wishlistRepository.findByUserAndProduct(user, product).ifPresentOrElse(wishlistRepository::delete, () -> {
            WishlistItem item = new WishlistItem();
            item.setUser(user);
            item.setProduct(product);
            item.setPosition((int) wishlistRepository.countByUser(user) + 1);
            wishlistRepository.save(item);
        });
        redirectAttributes.addFlashAttribute("success", "Wishlist mise a jour.");
        return "redirect:/products/" + productId;
    }
}

