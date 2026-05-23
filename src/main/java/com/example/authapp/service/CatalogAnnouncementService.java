package com.example.authapp.service;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.repo.StreamingMediaRepository;
import com.example.authapp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CatalogAnnouncementService {

    private final UserRepository userRepository;
    private final StreamingMediaRepository mediaRepository;
    private final MailService mailService;
    private final String publicBaseUrl;

    public CatalogAnnouncementService(UserRepository userRepository,
                                      StreamingMediaRepository mediaRepository,
                                      MailService mailService,
                                      @Value("${app.public-base-url:http://localhost:8091}") String publicBaseUrl) {
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
        this.mailService = mailService;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Scheduled(cron = "0 30 10 * * MON")
    @Transactional
    public void announceWeeklyTrendingTitle() {
        StreamingMedia media = mediaRepository.mostWatched(PageRequest.of(0, 1)).stream().findFirst().orElse(null);
        if (media == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        userRepository.findAll().stream()
                .filter(AppUser::isPrimeMember)
                .filter(user -> user.getLastCatalogAnnouncementAt() == null
                        || user.getLastCatalogAnnouncementAt().isBefore(now.minusDays(6)))
                .forEach(user -> {
                    mailService.sendCatalogAnnouncement(
                            user.getEmail(),
                            user.getUsername(),
                            media.getTitle(),
                            "Un film en vogue vient d'etre mis en avant pour vous.",
                            publicBaseUrl.replaceAll("/+$", "") + "/watch/" + media.getId(),
                            media.getPosterUrl(),
                            media.getDescription()
                    );
                    user.setLastCatalogAnnouncementAt(now);
                    userRepository.save(user);
                });
    }
}
