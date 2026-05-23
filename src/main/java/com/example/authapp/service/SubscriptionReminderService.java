package com.example.authapp.service;

import com.example.authapp.model.AppUser;
import com.example.authapp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SubscriptionReminderService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final String publicBaseUrl;

    public SubscriptionReminderService(UserRepository userRepository,
                                       MailService mailService,
                                       @Value("${app.public-base-url:http://localhost:8091}") String publicBaseUrl) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendUpcomingSubscriptionReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderLimit = now.plusDays(3);
        userRepository.findAll().stream()
                .filter(AppUser::isPrimeMember)
                .filter(user -> user.getPrimeSubscriptionEndsAt() != null)
                .filter(user -> !user.getPrimeSubscriptionEndsAt().isBefore(now))
                .filter(user -> !user.getPrimeSubscriptionEndsAt().isAfter(reminderLimit))
                .filter(user -> user.getLastSubscriptionReminderAt() == null
                        || user.getLastSubscriptionReminderAt().isBefore(now.minusHours(20)))
                .forEach(user -> {
                    mailService.sendSubscriptionReminder(
                            user.getEmail(),
                            user.getUsername(),
                            user.getPrimePlan(),
                            user.getPrimeSubscriptionEndsAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            publicBaseUrl.replaceAll("/+$", "") + "/subscription"
                    );
                    user.setLastSubscriptionReminderAt(now);
                    userRepository.save(user);
                });
    }
}
