package com.example.authapp.web;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.model.StreamingProfile;
import com.example.authapp.service.MailService;
import com.example.authapp.service.PrimeStreamService;
import com.example.authapp.service.SuccheroService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class PrimeStreamController {

    private final PrimeStreamService primeStreamService;
    private final SuccheroService succheroService;
    private final MailService mailService;

    public PrimeStreamController(PrimeStreamService primeStreamService, SuccheroService succheroService, MailService mailService) {
        this.primeStreamService = primeStreamService;
        this.succheroService = succheroService;
        this.mailService = mailService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        addHomeModel(model, principal);
        return "stream/home";
    }

    @GetMapping("/browse")
    public String browse(@RequestParam(name = "q", defaultValue = "") String q,
                         @RequestParam(name = "genre", defaultValue = "") String genre,
                         @RequestParam(name = "type", defaultValue = "") String type,
                         @RequestParam(name = "year", defaultValue = "0") int year,
                         Model model,
                         Principal principal) {
        List<StreamingMedia> items = primeStreamService.search(q, genre, type, year);
        model.addAttribute("items", items);
        model.addAttribute("genres", primeStreamService.genres());
        model.addAttribute("q", q);
        model.addAttribute("genre", genre);
        model.addAttribute("type", type);
        model.addAttribute("year", year == 0 ? "" : year);
        model.addAttribute("noResultQuery", !q.isBlank() && items.isEmpty() ? q : "");
        addUserBits(model, principal);
        return "stream/browse";
    }

    @GetMapping("/watch/{id}")
    public String watch(@PathVariable("id") Long id, Model model, Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        StreamingProfile profile = primeStreamService.defaultProfile(user);
        StreamingMedia media = primeStreamService.media(id);
        model.addAttribute("media", media);
        model.addAttribute("progress", primeStreamService.progress(profile, media));
        model.addAttribute("recommendations", primeStreamService.recommendations(profile));
        model.addAttribute("reviews", primeStreamService.reviews(media));
        model.addAttribute("inWatchlist", primeStreamService.inWatchlist(user, media));
        model.addAttribute("actorLinks", actorLinks(media));
        addUserBits(model, principal);
        return "stream/watch";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        StreamingProfile profile = primeStreamService.defaultProfile(user);
        model.addAttribute("user", user);
        model.addAttribute("profiles", primeStreamService.profiles(user));
        model.addAttribute("history", primeStreamService.history(profile));
        model.addAttribute("watchlist", primeStreamService.watchlist(user));
        model.addAttribute("availabilityRequests", succheroService.recentRequests(user));
        model.addAttribute("succheroAlerts", succheroService.alerts(user));
        model.addAttribute("genres", primeStreamService.genres());
        addUserBits(model, principal);
        return "stream/profile";
    }

    @GetMapping("/subscription")
    public String subscription(Model model, Principal principal) {
        addUserBits(model, principal);
        return "stream/subscription";
    }

    @GetMapping("/support")
    public String support(Model model, Principal principal) {
        addUserBits(model, principal);
        return "stream/support";
    }

    @PostMapping("/subscription")
    public String subscribe(@RequestParam("plan") String plan, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            AppUser user = primeStreamService.currentUser(principal.getName());
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
                return "redirect:/subscription";
            }
            primeStreamService.subscribe(user, plan);
            redirectAttributes.addFlashAttribute("success", "Abonnement activé avec succès au plan: " + plan);
            return "redirect:/profile?subscribed=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'activation: " + e.getMessage());
            return "redirect:/subscription";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite lors de l'activation");
            return "redirect:/subscription";
        }
    }

    @PostMapping("/profile/preferences")
    public String updatePreferences(@RequestParam("moodMode") String moodMode,
                                    @RequestParam("preferredGenre") String preferredGenre,
                                    @RequestParam("preferredLanguage") String preferredLanguage,
                                    @RequestParam("preferredQuality") String preferredQuality,
                                    @RequestParam("maturityLevel") String maturityLevel,
                                    Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        primeStreamService.updatePreferences(
                primeStreamService.defaultProfile(user),
                moodMode,
                preferredGenre,
                preferredLanguage,
                preferredQuality,
                maturityLevel
        );
        return "redirect:/profile?preferences=saved";
    }

    @PostMapping("/succhero/notify")
    public String requestAvailability(@RequestParam("title") String title,
                                      @RequestParam(name = "genre", defaultValue = "") String genre,
                                      Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        boolean created = succheroService.requestAvailability(user, title, genre);
        return "redirect:/browse?q=" + encode(title) + (created ? "&notify=created" : "&notify=exists");
    }

    @PostMapping("/api/succhero/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> succheroChat(@RequestBody Map<String, String> payload, Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        return ResponseEntity.ok(Map.of("reply", succheroService.chat(user, payload.get("message"))));
    }

    @PostMapping("/api/stream/progress")
    @ResponseBody
    public ResponseEntity<Map<String, String>> progress(@RequestBody ProgressPayload payload, Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        primeStreamService.saveProgress(primeStreamService.defaultProfile(user), payload.mediaId(), payload.seconds(), payload.percent());
        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    @PostMapping("/api/stream/preferences")
    @ResponseBody
    public ResponseEntity<Map<String, String>> preferences(@RequestBody PreferencePayload payload, Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        StreamingProfile profile = primeStreamService.defaultProfile(user);
        primeStreamService.updatePreferences(
                profile,
                payload.moodMode(),
                payload.preferredGenre(),
                payload.preferredLanguage(),
                payload.preferredQuality(),
                payload.maturityLevel()
        );
        String genre = primeStreamService.moodGenre(profile);
        return ResponseEntity.ok(Map.of(
                "moodMode", profile.getMoodMode(),
                "genre", genre,
                "browseUrl", genre.isBlank() ? "/browse" : "/browse?genre=" + encode(genre)
        ));
    }

    @PostMapping("/api/stream/watchlist/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> watchlist(@PathVariable("id") Long id, Principal principal) {
        AppUser user = primeStreamService.currentUser(principal.getName());
        boolean added = primeStreamService.toggleWatchlist(user, id);
        return ResponseEntity.ok(Map.of("added", added));
    }

    @PostMapping("/review/{id}")
    public String review(@PathVariable("id") Long id,
                         @RequestParam("score") int score,
                         @RequestParam("comment") String comment,
                         Principal principal) {
        primeStreamService.review(primeStreamService.currentUser(principal.getName()), id, score, comment);
        return "redirect:/watch/" + id + "?reviewed=true";
    }

    @GetMapping("/admin/stream")
    public String admin(Model model, Principal principal) {
        model.addAttribute("items", primeStreamService.search("", "", "", 0));
        model.addAttribute("mediaForm", new MediaForm());
        model.addAttribute("featured", primeStreamService.featured());
        if (principal != null) {
            model.addAttribute("demoMailTo", primeStreamService.currentUser(principal.getName()).getEmail());
        }
        return "stream/admin-dashboard";
    }

    @PostMapping("/admin/stream")
    public String createMedia(@Valid @ModelAttribute("mediaForm") MediaForm form,
                              BindingResult bindingResult,
                              @RequestParam(name = "posterFile", required = false) MultipartFile posterFile,
                              @RequestParam(name = "bannerFile", required = false) MultipartFile bannerFile,
                              @RequestParam(name = "videoFile", required = false) MultipartFile videoFile,
                              Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("items", primeStreamService.search("", "", "", 0));
            model.addAttribute("featured", primeStreamService.featured());
            return "stream/admin-dashboard";
        }
        StreamingMedia media = new StreamingMedia();
        media.setTitle(form.title);
        media.setType(form.type);
        media.setDescription(form.description);
        media.setReleaseYear(form.releaseYear);
        media.setDurationMinutes(form.durationMinutes);
        media.setAgeRating(form.ageRating);
        media.setDirector(form.director);
        media.setRating(form.rating);
        media.setViews(form.views);
        media.setPremiumOnly(form.premiumOnly);
        media.setFeatured(form.featured);
        media.setTopTen(form.topTen);
        media.setPosterUrl(form.posterUrl);
        media.setBannerUrl(form.bannerUrl);
        media.setVideoUrl(form.videoUrl);
        String uploadedPoster = storeUpload(posterFile, "images");
        String uploadedBanner = storeUpload(bannerFile, "images");
        String uploadedVideo = storeUpload(videoFile, "videos");
        if (uploadedPoster != null) {
            media.setPosterUrl(uploadedPoster);
        }
        if (uploadedBanner != null) {
            media.setBannerUrl(uploadedBanner);
        }
        if (uploadedVideo != null) {
            media.setVideoUrl(uploadedVideo);
        }
        if (media.getPosterUrl() == null || media.getPosterUrl().isBlank()) {
            media.setPosterUrl("https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=800&q=80");
        }
        if (media.getBannerUrl() == null || media.getBannerUrl().isBlank()) {
            media.setBannerUrl("https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1800&q=80");
        }
        if (media.getVideoUrl() == null || media.getVideoUrl().isBlank()) {
            media.setVideoUrl("/videos/movie-demo.mp4");
        }
        media.setSubtitleUrl(form.subtitleUrl);
        media.setGenres(split(form.genres));
        media.setLanguages(split(form.languages));
        media.setCastMembers(split(form.castMembers));
        primeStreamService.saveMedia(media);
        succheroService.checkAvailabilityRequests();
        return "redirect:/admin/stream?created=true";
    }

    @PostMapping("/admin/stream/{id}/delete")
    public String deleteMedia(@PathVariable("id") Long id) {
        primeStreamService.deleteMedia(id);
        return "redirect:/admin/stream?deleted=true";
    }

    @PostMapping("/admin/stream/demo-mail")
    public String sendDemoMail(@RequestParam("email") String email, Principal principal) {
        AppUser admin = primeStreamService.currentUser(principal.getName());
        StreamingMedia media = primeStreamService.featured().stream()
                .findFirst()
                .orElseGet(() -> primeStreamService.search("", "", "", 0).stream()
                        .findFirst()
                        .orElseThrow());
        String watchLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/watch/{id}")
                .buildAndExpand(media.getId())
                .toUriString();
        mailService.sendCatalogAnnouncement(
                email,
                admin.getUsername(),
                media.getTitle(),
                "Un titre en vogue vient d'etre selectionne pour votre demonstration PrimeStream.",
                watchLink,
                media.getPosterUrl(),
                media.getDescription()
        );
        return "redirect:/admin/stream?demoMail=sent";
    }

    private void addHomeModel(Model model, Principal principal) {
        model.addAttribute("hero", primeStreamService.featured().stream().findFirst().orElse(null));
        model.addAttribute("featured", primeStreamService.featured());
        model.addAttribute("topTen", primeStreamService.topTen());
        model.addAttribute("newest", primeStreamService.newest());
        if (principal != null) {
            AppUser user = primeStreamService.currentUser(principal.getName());
            StreamingProfile profile = primeStreamService.defaultProfile(user);
            var alerts = succheroService.alerts(user);
            model.addAttribute("moodSelection", primeStreamService.moodSelection(profile));
            model.addAttribute("succheroAlerts", alerts);
            model.addAttribute("succheroMessages", succheroService.conversation(user));
            model.addAttribute("succheroGreeting", succheroService.greeting(user, alerts));
        }
        addUserBits(model, principal);
    }

    private void addUserBits(Model model, Principal principal) {
        if (principal != null) {
            AppUser user = primeStreamService.currentUser(principal.getName());
            model.addAttribute("currentUser", user);
            StreamingProfile profile = primeStreamService.defaultProfile(user);
            var alerts = succheroService.alerts(user);
            model.addAttribute("currentProfile", profile);
            model.addAttribute("succheroAlerts", alerts);
            model.addAttribute("succheroMessages", succheroService.conversation(user));
            model.addAttribute("succheroGreeting", succheroService.greeting(user, alerts));
        }
    }

    private Set<String> split(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Set.of(value.split("\\s*,\\s*"));
    }

    private Map<String, String> actorLinks(StreamingMedia media) {
        return media.getCastMembers().stream()
                .collect(Collectors.toMap(
                        actor -> actor,
                        actor -> "https://www.tiktok.com/search?q=" + encode(actor),
                        (left, right) -> left
                ));
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String storeUpload(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot).toLowerCase();
        }
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = stamp + "-" + UUID.randomUUID() + extension;
        Path dir = Path.of("uploads", folder).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Path target = dir.resolve(filename).normalize();
        if (!target.startsWith(dir)) {
            throw new IOException("Chemin upload invalide");
        }
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + folder + "/" + filename;
    }

    public record ProgressPayload(Long mediaId, int seconds, int percent) {
    }

    public record PreferencePayload(String moodMode, String preferredGenre, String preferredLanguage,
                                    String preferredQuality, String maturityLevel) {
    }

    public static class MediaForm {
        @NotBlank
        public String title;
        @NotBlank
        public String type = "Film";
        @Size(max = 1400)
        public String description;
        public int releaseYear = 2026;
        public int durationMinutes = 105;
        public String ageRating = "13+";
        public String director = "PrimeStream Studio";
        public double rating = 4.6;
        public int views = 1000;
        public boolean premiumOnly;
        public boolean featured;
        public boolean topTen;
        public String posterUrl;
        public String bannerUrl;
        public String videoUrl;
        public String subtitleUrl;
        public String genres = "Action, Drame";
        public String languages = "Francais, Anglais";
        public String castMembers = "Amina Cole, Noah Vance";

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        public String getAgeRating() {
            return ageRating;
        }

        public void setAgeRating(String ageRating) {
            this.ageRating = ageRating;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public boolean isPremiumOnly() {
            return premiumOnly;
        }

        public void setPremiumOnly(boolean premiumOnly) {
            this.premiumOnly = premiumOnly;
        }

        public boolean isFeatured() {
            return featured;
        }

        public void setFeatured(boolean featured) {
            this.featured = featured;
        }

        public boolean isTopTen() {
            return topTen;
        }

        public void setTopTen(boolean topTen) {
            this.topTen = topTen;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public void setPosterUrl(String posterUrl) {
            this.posterUrl = posterUrl;
        }

        public String getBannerUrl() {
            return bannerUrl;
        }

        public void setBannerUrl(String bannerUrl) {
            this.bannerUrl = bannerUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getSubtitleUrl() {
            return subtitleUrl;
        }

        public void setSubtitleUrl(String subtitleUrl) {
            this.subtitleUrl = subtitleUrl;
        }

        public String getGenres() {
            return genres;
        }

        public void setGenres(String genres) {
            this.genres = genres;
        }

        public String getLanguages() {
            return languages;
        }

        public void setLanguages(String languages) {
            this.languages = languages;
        }

        public String getCastMembers() {
            return castMembers;
        }

        public void setCastMembers(String castMembers) {
            this.castMembers = castMembers;
        }
    }
}
