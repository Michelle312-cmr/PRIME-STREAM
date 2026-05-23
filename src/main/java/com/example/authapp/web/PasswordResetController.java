package com.example.authapp.web;

import com.example.authapp.model.PasswordResetToken;
import com.example.authapp.repo.PasswordResetTokenRepository;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.service.AuditService;
import com.example.authapp.service.MailService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AuditService auditService;

    public PasswordResetController(UserRepository userRepository,
                                   PasswordResetTokenRepository tokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   MailService mailService,
                                   AuditService auditService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.auditService = auditService;
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "auth/forgot";
    }

    @PostMapping("/forgot")
    @Transactional
    public String doForgot(@RequestParam("email") String email, Model model) {
        var userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Aucun compte ne correspond a cette adresse email.");
            auditService.log("PASSWORD_RESET_REQUEST_FAILED", email, "email_not_found");
            return "auth/forgot";
        }

        var user = userOpt.get();
        String tokenValue = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(30 * 60);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setExpiryDate(expiry);
        token.setUser(user);
        tokenRepository.save(token);

        String resetLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/reset/")
                .path(tokenValue)
                .toUriString();

        mailService.sendPasswordReset(user.getEmail(), user.getUsername(), resetLink);
        auditService.log("PASSWORD_RESET_REQUEST", user.getUsername(), "email=" + user.getEmail());

        model.addAttribute("message", "Un lien de reinitialisation a ete envoye a votre email.");
        model.addAttribute("demoLink", resetLink);
        return "auth/forgot";
    }

    @GetMapping("/reset/{token}")
    public String resetPage(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset";
    }

    @PostMapping("/reset/{token}")
    @Transactional
    public String doReset(@PathVariable String token,
                          @RequestParam("newPassword") String newPassword,
                          Model model) {

        if (newPassword == null || newPassword.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Le mot de passe doit contenir au moins 8 caracteres.");
            return "auth/reset";
        }

        Optional<PasswordResetToken> tokOpt = tokenRepository.findByToken(token);
        if (tokOpt.isEmpty()) {
            model.addAttribute("error", "Jeton invalide.");
            return "auth/reset";
        }

        PasswordResetToken tok = tokOpt.get();
        if (tok.getExpiryDate() == null || tok.getExpiryDate().isBefore(Instant.now())) {
            model.addAttribute("error", "Jeton expire.");
            return "auth/reset";
        }

        var user = tok.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(tok);
        auditService.log("PASSWORD_RESET_SUCCESS", user.getUsername(), "token_used");

        model.addAttribute("success", "Mot de passe modifie. Vous pouvez vous connecter.");
        return "auth/reset";
    }
}
