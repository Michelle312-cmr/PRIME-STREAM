package com.example.authapp.service;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.MediaReview;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.model.StreamingProfile;
import com.example.authapp.model.StreamingWatchlistItem;
import com.example.authapp.model.WatchHistory;
import com.example.authapp.repo.MediaReviewRepository;
import com.example.authapp.repo.StreamingMediaRepository;
import com.example.authapp.repo.StreamingProfileRepository;
import com.example.authapp.repo.StreamingWatchlistRepository;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.repo.WatchHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PrimeStreamService {

    private final StreamingMediaRepository mediaRepository;
    private final StreamingProfileRepository profileRepository;
    private final WatchHistoryRepository historyRepository;
    private final StreamingWatchlistRepository watchlistRepository;
    private final MediaReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public PrimeStreamService(StreamingMediaRepository mediaRepository,
                              StreamingProfileRepository profileRepository,
                              WatchHistoryRepository historyRepository,
                              StreamingWatchlistRepository watchlistRepository,
                              MediaReviewRepository reviewRepository,
                              UserRepository userRepository) {
        this.mediaRepository = mediaRepository;
        this.profileRepository = profileRepository;
        this.historyRepository = historyRepository;
        this.watchlistRepository = watchlistRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public AppUser currentUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Transactional
    public StreamingProfile defaultProfile(AppUser user) {
        return profileRepository.findFirstByUserOrderByIdAsc(user)
                .orElseGet(() -> {
                    StreamingProfile profile = new StreamingProfile();
                    profile.setUser(user);
                    profile.setName(user.getFullName() == null || user.getFullName().isBlank() ? user.getUsername() : user.getFullName());
                    profile.setAvatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=400&q=80");
                    return profileRepository.save(profile);
                });
    }

    public List<StreamingProfile> profiles(AppUser user) {
        return profileRepository.findByUserOrderByIdAsc(user);
    }

    public List<StreamingMedia> featured() {
        return mediaRepository.findTop8ByFeaturedTrueOrderByViewsDesc();
    }

    public List<StreamingMedia> topTen() {
        return mediaRepository.findTop10ByTopTenTrueOrderByViewsDesc();
    }

    public List<StreamingMedia> newest() {
        return mediaRepository.findTop8ByOrderByCreatedAtDesc();
    }

    public List<StreamingMedia> praised() {
        return mediaRepository.findTop8ByOrderByRatingDesc();
    }

    public List<StreamingMedia> search(String q, String genre, String type, int year) {
        return mediaRepository.search(clean(q), clean(genre), clean(type), year);
    }

    public List<StreamingMedia> moodSelection(StreamingProfile profile) {
        String genre = moodGenre(profile);
        if (genre.isBlank()) {
            return praised();
        }
        List<StreamingMedia> items = mediaRepository.findByGenresContainingIgnoreCase(genre);
        return items.isEmpty() ? praised() : items.stream().limit(8).toList();
    }

    @Transactional
    public void updatePreferences(StreamingProfile profile, String moodMode, String preferredGenre,
                                  String language, String quality, String maturityLevel) {
        String mood = clean(moodMode).toLowerCase(Locale.ROOT);
        profile.setMoodMode(switch (mood) {
            case "romance", "action", "family", "science", "afrique" -> mood;
            default -> "classic";
        });
        profile.setPreferredGenre(clean(preferredGenre).isBlank() ? moodGenre(profile) : clean(preferredGenre));
        profile.setPreferredLanguage(clean(language).isBlank() ? "Francais" : clean(language));
        profile.setPreferredQuality(clean(quality).isBlank() ? "Auto" : clean(quality));
        profile.setMaturityLevel(clean(maturityLevel).isBlank() ? "18+" : clean(maturityLevel));
        profileRepository.save(profile);
    }

    public String moodGenre(StreamingProfile profile) {
        if (profile == null || profile.getMoodMode() == null) {
            return "";
        }
        return moodGenre(profile.getMoodMode(), profile.getPreferredGenre());
    }

    public String moodGenre(String moodMode, String preferredGenre) {
        return switch (clean(moodMode).toLowerCase(Locale.ROOT)) {
            case "romance" -> "Romance";
            case "action" -> "Action";
            case "family" -> "Famille";
            case "science" -> "Science-fiction";
            case "afrique" -> "Afrique";
            default -> clean(preferredGenre);
        };
    }

    public List<String> genres() {
        return mediaRepository.findAllGenres();
    }

    public StreamingMedia media(Long id) {
        return mediaRepository.findById(id).orElseThrow();
    }

    public List<StreamingMedia> recommendations(StreamingProfile profile) {
        List<WatchHistory> history = historyRepository.findTop12ByProfileOrderByLastWatchedAtDesc(profile);
        if (history.isEmpty()) {
            return mediaRepository.mostWatched(PageRequest.of(0, 8));
        }
        Set<String> genres = new LinkedHashSet<>();
        history.forEach(item -> genres.addAll(item.getMedia().getGenres()));
        if (genres.isEmpty()) {
            return mediaRepository.mostWatched(PageRequest.of(0, 8));
        }
        return mediaRepository.findByGenresContainingIgnoreCase(genres.iterator().next()).stream()
                .filter(media -> history.stream().noneMatch(item -> item.getMedia().getId().equals(media.getId())))
                .limit(8)
                .toList();
    }

    public List<WatchHistory> history(StreamingProfile profile) {
        return historyRepository.findTop12ByProfileOrderByLastWatchedAtDesc(profile);
    }

    public List<StreamingWatchlistItem> watchlist(AppUser user) {
        return watchlistRepository.findByUserOrderByAddedAtDesc(user);
    }

    public boolean inWatchlist(AppUser user, StreamingMedia media) {
        return watchlistRepository.existsByUserAndMedia(user, media);
    }

    @Transactional
    public boolean toggleWatchlist(AppUser user, Long mediaId) {
        StreamingMedia media = media(mediaId);
        return watchlistRepository.findByUserAndMedia(user, media)
                .map(item -> {
                    watchlistRepository.delete(item);
                    return false;
                })
                .orElseGet(() -> {
                    StreamingWatchlistItem item = new StreamingWatchlistItem();
                    item.setUser(user);
                    item.setMedia(media);
                    watchlistRepository.save(item);
                    return true;
                });
    }

    @Transactional
    public void saveProgress(StreamingProfile profile, Long mediaId, int seconds, int percent) {
        StreamingMedia media = media(mediaId);
        WatchHistory history = historyRepository.findByProfileAndMedia(profile, media).orElseGet(() -> {
            WatchHistory created = new WatchHistory();
            created.setProfile(profile);
            created.setMedia(media);
            return created;
        });
        history.setProgressSeconds(Math.max(0, seconds));
        history.setProgressPercent(Math.max(0, Math.min(100, percent)));
        history.setLastWatchedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    public WatchHistory progress(StreamingProfile profile, StreamingMedia media) {
        return historyRepository.findByProfileAndMedia(profile, media).orElse(null);
    }

    @Transactional
    public void subscribe(AppUser user, String plan) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        
        String normalized = plan == null ? "Gratuit" : plan.trim().toLowerCase(Locale.ROOT);
        String selectedPlan = switch (normalized) {
            case "premium" -> "Premium";
            case "standard" -> "Standard";
            default -> "Gratuit";
        };
        
        user.setPrimePlan(selectedPlan);
        user.setPrimeMember(!"Gratuit".equals(selectedPlan));
        
        if (user.isPrimeMember()) {
            // Ajouter 30 jours pour tout plan payant
            user.setPrimeSubscriptionEndsAt(LocalDateTime.now().plusDays(30));
        } else {
            // Pas d'abonnement pour le plan gratuit
            user.setPrimeSubscriptionEndsAt(null);
        }
        
        // Réinitialiser le rappel d'abonnement
        user.setLastSubscriptionReminderAt(null);
        
        AppUser saved = userRepository.save(user);
        if (saved.getId() == null) {
            throw new RuntimeException("Échec de la sauvegarde de l'abonnement");
        }
    }

    @Transactional
    public StreamingMedia saveMedia(StreamingMedia media) {
        media.setCreatedAt(media.getCreatedAt() == null ? LocalDateTime.now() : media.getCreatedAt());
        return mediaRepository.save(media);
    }

    @Transactional
    public void deleteMedia(Long id) {
        mediaRepository.deleteById(id);
    }

    public List<MediaReview> reviews(StreamingMedia media) {
        return reviewRepository.findTop5ByMediaOrderByCreatedAtDesc(media);
    }

    @Transactional
    public void review(AppUser user, Long mediaId, int score, String comment) {
        MediaReview review = new MediaReview();
        review.setUser(user);
        review.setMedia(media(mediaId));
        review.setScore(Math.max(1, Math.min(5, score)));
        review.setComment(comment == null ? "" : comment.trim());
        reviewRepository.save(review);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
