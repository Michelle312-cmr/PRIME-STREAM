package com.example.authapp.web;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.AvailabilityRequest;
import com.example.authapp.model.SuccheroMessage;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.model.StreamingProfile;
import com.example.authapp.model.StreamingWatchlistItem;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.security.JwtService;
import com.example.authapp.service.PrimeStreamService;
import com.example.authapp.service.SuccheroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PrimeStreamService primeStreamService;
    private final SuccheroService succheroService;

    public RestApiController(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             JwtService jwtService,
                             PrimeStreamService primeStreamService,
                             SuccheroService succheroService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.primeStreamService = primeStreamService;
        this.succheroService = succheroService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
                .or(() -> userRepository.findByEmail(request.username()))
                .orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Identifiants invalides"));
        }
        return ResponseEntity.ok(new LoginResponse(
                jwtService.generate(user),
                "Bearer",
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).sorted().toList(),
                jwtService.expirationMinutes()
        ));
    }

    @GetMapping("/rest/me")
    public UserDto me(Authentication authentication) {
        return userDto(currentUser(authentication));
    }

    @GetMapping("/rest/media")
    public List<MediaDto> media(@RequestParam(name = "q", defaultValue = "") String q,
                                @RequestParam(name = "genre", defaultValue = "") String genre,
                                @RequestParam(name = "type", defaultValue = "") String type,
                                @RequestParam(name = "year", defaultValue = "0") int year) {
        return primeStreamService.search(q, genre, type, year).stream().map(this::mediaDto).toList();
    }

    @GetMapping("/rest/media/{id}")
    public MediaDto mediaDetails(@PathVariable("id") Long id) {
        return mediaDto(primeStreamService.media(id));
    }

    @GetMapping("/rest/watchlist")
    public List<WatchlistItemDto> watchlist(Authentication authentication) {
        AppUser user = currentUser(authentication);
        return primeStreamService.watchlist(user).stream().map(this::watchlistDto).toList();
    }

    @PostMapping("/rest/watchlist/{id}")
    public Map<String, Object> toggleWatchlist(@PathVariable("id") Long id, Authentication authentication) {
        boolean added = primeStreamService.toggleWatchlist(currentUser(authentication), id);
        return Map.of("mediaId", id, "added", added);
    }

    @PostMapping("/rest/progress")
    public Map<String, String> saveProgress(@RequestBody ProgressRequest request, Authentication authentication) {
        AppUser user = currentUser(authentication);
        primeStreamService.saveProgress(
                primeStreamService.defaultProfile(user),
                request.mediaId(),
                request.seconds(),
                request.percent()
        );
        return Map.of("status", "saved");
    }

    @PostMapping("/rest/profile/preferences")
    public UserDto updatePreferences(@RequestBody PreferenceRequest request, Authentication authentication) {
        AppUser user = currentUser(authentication);
        primeStreamService.updatePreferences(
                primeStreamService.defaultProfile(user),
                request.moodMode(),
                request.preferredGenre(),
                request.preferredLanguage(),
                request.preferredQuality(),
                request.maturityLevel()
        );
        return userDto(user);
    }

    @PostMapping("/rest/subscription")
    public UserDto subscribe(@RequestBody SubscriptionRequest request, Authentication authentication) {
        AppUser user = currentUser(authentication);
        primeStreamService.subscribe(user, request.plan());
        return userDto(user);
    }

    @PostMapping("/rest/succhero/chat")
    public Map<String, String> succheroChat(@RequestBody ChatRequest request, Authentication authentication) {
        return Map.of("reply", succheroService.chat(currentUser(authentication), request.message()));
    }

    @GetMapping("/rest/succhero/messages")
    public List<SuccheroMessageDto> succheroMessages(Authentication authentication) {
        return succheroService.conversation(currentUser(authentication)).stream()
                .map(this::messageDto)
                .toList();
    }

    @PostMapping("/rest/succhero/notify")
    public Map<String, Object> requestAvailability(@RequestBody AvailabilityRequestDto request,
                                                   Authentication authentication) {
        boolean created = succheroService.requestAvailability(
                currentUser(authentication),
                request.searchedTitle(),
                request.preferredGenre()
        );
        return Map.of("created", created);
    }

    @GetMapping("/rest/succhero/requests")
    public List<AvailabilityResponseDto> availabilityRequests(Authentication authentication) {
        return succheroService.recentRequests(currentUser(authentication)).stream()
                .map(this::availabilityDto)
                .toList();
    }

    private AppUser currentUser(Authentication authentication) {
        return primeStreamService.currentUser(authentication.getName());
    }

    private UserDto userDto(AppUser user) {
        StreamingProfile profile = primeStreamService.defaultProfile(user);
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream().map(Enum::name).sorted().toList(),
                user.isPrimeMember(),
                user.getPrimePlan(),
                user.getPrimeSubscriptionEndsAt(),
                profile.getMoodMode(),
                profile.getPreferredGenre()
        );
    }

    private MediaDto mediaDto(StreamingMedia media) {
        return new MediaDto(
                media.getId(),
                media.getTitle(),
                media.getType(),
                media.getDescription(),
                media.getReleaseYear(),
                media.getDurationMinutes(),
                media.getAgeRating(),
                media.getDirector(),
                media.getRating(),
                media.getViews(),
                media.isPremiumOnly(),
                media.isFeatured(),
                media.isTopTen(),
                media.getPosterUrl(),
                media.getBannerUrl(),
                media.getVideoUrl(),
                media.getGenres(),
                media.getLanguages(),
                media.getCastMembers()
        );
    }

    private WatchlistItemDto watchlistDto(StreamingWatchlistItem item) {
        return new WatchlistItemDto(item.getId(), item.getAddedAt(), mediaDto(item.getMedia()));
    }

    private SuccheroMessageDto messageDto(SuccheroMessage message) {
        return new SuccheroMessageDto(message.getId(), message.getRole().name(), message.getContent(), message.getCreatedAt());
    }

    private AvailabilityResponseDto availabilityDto(AvailabilityRequest request) {
        return new AvailabilityResponseDto(
                request.getId(),
                request.getSearchedTitle(),
                request.getPreferredGenre(),
                request.isNotified(),
                request.getCreatedAt(),
                request.getNotifiedAt(),
                request.getMatchedMedia() == null ? null : mediaDto(request.getMatchedMedia())
        );
    }

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String token, String tokenType, String username, List<String> roles, long expiresInMinutes) {
    }

    public record UserDto(Long id, String username, String email, String fullName, List<String> roles,
                          boolean primeMember, String primePlan, LocalDateTime primeSubscriptionEndsAt,
                          String moodMode, String preferredGenre) {
    }

    public record MediaDto(Long id, String title, String type, String description, int releaseYear,
                           int durationMinutes, String ageRating, String director, double rating, int views,
                           boolean premiumOnly, boolean featured, boolean topTen, String posterUrl,
                           String bannerUrl, String videoUrl, Set<String> genres, Set<String> languages,
                           Set<String> castMembers) {
    }

    public record WatchlistItemDto(Long id, LocalDateTime addedAt, MediaDto media) {
    }

    public record ProgressRequest(Long mediaId, int seconds, int percent) {
    }

    public record PreferenceRequest(String moodMode, String preferredGenre, String preferredLanguage,
                                    String preferredQuality, String maturityLevel) {
    }

    public record SubscriptionRequest(String plan) {
    }

    public record ChatRequest(String message) {
    }

    public record AvailabilityRequestDto(String searchedTitle, String preferredGenre) {
    }

    public record AvailabilityResponseDto(Long id, String searchedTitle, String preferredGenre, boolean notified,
                                          LocalDateTime createdAt, LocalDateTime notifiedAt, MediaDto matchedMedia) {
    }

    public record SuccheroMessageDto(Long id, String role, String content, LocalDateTime createdAt) {
    }
}
