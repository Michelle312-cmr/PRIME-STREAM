package com.example.authapp.web;

import com.example.authapp.model.AppUser;
import com.example.authapp.repo.OrderRepository;
import com.example.authapp.repo.ProductRepository;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.service.AuditService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashSet;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(UserRepository userRepository, AuditService auditService,
                           ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/admin/home")
    public String adminHome(Authentication authentication, Model model) {
        var users = userRepository.findAll().stream()
                .sorted(Comparator.comparing(AppUser::getUsername))
                .toList();

        model.addAttribute("title", "Panneau Admin");
        model.addAttribute("who", authentication.getName());
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("admins", users.stream().filter(user -> user.getRoles().contains(AppUser.Role.ADMIN)).count());
        model.addAttribute("products", productRepository.count());
        model.addAttribute("orders", orderRepository.count());
        return "admin/home";
    }

    @PostMapping("/admin/users/{id}/toggle-seller")
    public String toggleSeller(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur introuvable.");
            return "redirect:/admin/home";
        }
        var user = userOpt.get();
        var roles = new HashSet<>(user.getRoles());
        if (roles.contains(AppUser.Role.SELLER)) {
            roles.remove(AppUser.Role.SELLER);
        } else {
            roles.add(AppUser.Role.SELLER);
        }
        user.setRoles(roles);
        userRepository.save(user);
        auditService.log("ADMIN_TOGGLE_SELLER", authentication.getName(), "target=" + user.getUsername() + ", roles=" + roles);
        redirectAttributes.addFlashAttribute("success", "Role vendeur mis a jour pour " + user.getUsername() + ".");
        return "redirect:/admin/home";
    }

    @PostMapping("/admin/users/{id}/toggle-admin")
    public String toggleAdmin(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur introuvable.");
            return "redirect:/admin/home";
        }

        var user = userOpt.get();
        if (user.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas retirer votre propre role admin.");
            return "redirect:/admin/home";
        }

        var roles = new HashSet<>(user.getRoles());
        if (roles.contains(AppUser.Role.ADMIN)) {
            roles.remove(AppUser.Role.ADMIN);
            roles.add(AppUser.Role.USER);
        } else {
            roles.add(AppUser.Role.ADMIN);
        }

        user.setRoles(roles);
        userRepository.save(user);
        auditService.log("ADMIN_TOGGLE_ROLE", authentication.getName(), "target=" + user.getUsername() + ", roles=" + roles);
        redirectAttributes.addFlashAttribute("success", "Role mis a jour pour " + user.getUsername() + ".");
        return "redirect:/admin/home";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur introuvable.");
            return "redirect:/admin/home";
        }

        var user = userOpt.get();
        if (user.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas supprimer votre propre compte.");
            return "redirect:/admin/home";
        }

        userRepository.delete(user);
        auditService.log("ADMIN_DELETE_USER", authentication.getName(), "target=" + user.getUsername());
        redirectAttributes.addFlashAttribute("success", "Compte supprime: " + user.getUsername() + ".");
        return "redirect:/admin/home";
    }
}
