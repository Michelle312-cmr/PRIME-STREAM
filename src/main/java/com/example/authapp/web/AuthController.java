package com.example.authapp.web;

import com.example.authapp.model.AppUser;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.service.AuditService;
import com.example.authapp.service.MailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final MailService mailService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService,
                          MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.mailService = mailService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid RegisterForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        if (userRepository.findByUsername(form.username).isPresent()) {
            model.addAttribute("error", "Ce username existe déjà.");
            return "auth/register";
        }
        if (userRepository.findByEmail(form.email).isPresent()) {
            model.addAttribute("error", "Cette adresse email est déjà utilisée.");
            return "auth/register";
        }

        AppUser user = new AppUser();
        user.setUsername(form.username);
        user.setEmail(form.email);
        user.setFullName(form.fullName);
        user.setPasswordHash(passwordEncoder.encode(form.password));
        user.setRoles(Set.of(AppUser.Role.USER));
        userRepository.save(user);
        auditService.log("REGISTER", form.username, "email=" + form.email);
        mailService.sendWelcomeConfirmation(
                form.email,
                form.username,
                "http://localhost:8091/login?confirmed=true&user=" + form.username
        );

        return "redirect:/login?registered=true&mail=true";
    }

    public static class RegisterForm {
        @NotBlank
        public String username;

        @Email
        @NotBlank
        public String email;

        @NotBlank
        public String fullName;

        @NotBlank
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caracteres.")
        public String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

