package com.example.authapp.bootstrap;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.Product;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.repo.ProductRepository;
import com.example.authapp.repo.StreamingMediaRepository;
import com.example.authapp.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final StreamingMediaRepository streamingMediaRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, ProductRepository productRepository,
                           StreamingMediaRepository streamingMediaRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
        this.streamingMediaRepository = streamingMediaRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setFullName("Admin Prime");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRoles(Set.of(AppUser.Role.ADMIN, AppUser.Role.SELLER));
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {
            AppUser user = new AppUser();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setFullName("Client Demo");
            user.setPrimeMember(true);
            user.setPrimePlan("mensuel");
            user.setPasswordHash(passwordEncoder.encode("User@123"));
            user.setRoles(Set.of(AppUser.Role.USER));
            userRepository.save(user);
        }

        if (!userRepository.existsByUsername("seller")) {
            AppUser seller = new AppUser();
            seller.setUsername("seller");
            seller.setEmail("seller@example.com");
            seller.setFullName("Vendeur Demo");
            seller.setPasswordHash(passwordEncoder.encode("Seller@123"));
            seller.setRoles(Set.of(AppUser.Role.SELLER, AppUser.Role.USER));
            userRepository.save(seller);
        }

        if (productRepository.count() == 0) {
            AppUser seller = userRepository.findByUsername("seller").orElseThrow();
            seed("Echo Studio Max", "High-Tech", "Enceinte connectee premium avec son spatial, controle vocal et hub domotique integre.",
                    "https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("179.99"), new BigDecimal("229.99"), 34, 4.8, 812, true, true, seller,
                    List.of("Audio 360", "Bluetooth 5.3", "Garantie 2 ans"));
            seed("Kindle Aura Pro", "Livres & ebooks", "Liseuse etanche avec ecran mat haute definition, autonomie longue duree et bibliotheque Prime Reading simulee.",
                    "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("129.90"), new BigDecimal("159.90"), 58, 4.7, 441, true, false, seller,
                    List.of("Ecran 7 pouces", "32 Go", "Etanche IPX8"));
            seed("PrimePad Air 11", "Informatique", "Tablette fine pour streaming, prise de notes, jeux cloud et productivite mobile.",
                    "https://images.unsplash.com/photo-1561154464-82e9adf32764?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("349.00"), new BigDecimal("429.00"), 19, 4.6, 1290, true, true, seller,
                    List.of("11 pouces", "128 Go", "Wi-Fi 6"));
            seed("Pack Maison Connectee", "Maison", "Kit compose de capteurs, ampoules intelligentes et mini camera pour piloter la maison depuis une seule app.",
                    "https://images.unsplash.com/photo-1558002038-1055907df827?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("89.50"), new BigDecimal("119.00"), 7, 4.5, 329, true, true, seller,
                    List.of("4 ampoules", "2 capteurs", "Installation rapide"));
            seed("Casque Nova ANC", "High-Tech", "Casque sans fil a reduction de bruit active avec mode transparence et charge rapide.",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("96.99"), new BigDecimal("139.99"), 43, 4.4, 974, true, false, seller,
                    List.of("ANC hybride", "40 h autonomie", "USB-C"));
            seed("Robot Cuisine Chefly", "Cuisine", "Robot multifonction avec balance integree, recettes guidees et nettoyage assiste.",
                    "https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("249.99"), new BigDecimal("299.99"), 15, 4.6, 233, true, false, seller,
                    List.of("1200 W", "12 programmes", "Bol 4.5 L"));
            seed("Sneakers Prime Run", "Mode", "Chaussures legeres pour marche quotidienne, avec amorti doux et maille respirante.",
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("59.99"), new BigDecimal("79.99"), 84, 4.3, 587, false, true, seller,
                    List.of("Semelle EVA", "Mesh respirant", "Tailles 39-45"));
            seed("Projecteur Cinema Mini", "Divertissement", "Projecteur compact Full HD avec haut-parleurs integres et mode streaming.",
                    "https://images.unsplash.com/photo-1601944179066-29786cb9d32a?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("219.00"), new BigDecimal("269.00"), 22, 4.5, 367, true, false, seller,
                    List.of("Full HD", "HDMI", "Image 120 pouces"));
            seed("Bureau Ergonomique Lift", "Bureau", "Bureau assis-debout motorise, memoires de hauteur et plateau resistant.",
                    "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80",
                    new BigDecimal("399.00"), new BigDecimal("499.00"), 11, 4.7, 188, true, false, seller,
                    List.of("Motorise", "2 memoires", "Plateau 140 cm"));
        }

        if (streamingMediaRepository.count() == 0) {
            stream("Nebula City", "Film", "Dans une megaville suspendue au-dessus des nuages, une pilote rebelle decouvre que la lumiere qui alimente la ville cache une dette humaine immense.",
                    2026, 118, "13+", "Leila Morgan", 4.8, 982400, true, true, true,
                    "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=1800&q=80",
                    "Action, Science-fiction, Prime Originals", "Francais, Anglais, Espagnol", "Maya Stone, Idris Hale, Nora Kim");
            stream("Riviera Noire", "Serie", "Une enquetrice revient sur la cote mediterraneenne pour resoudre une disparition liee a une dynastie hoteliere et a ses secrets.",
                    2025, 52, "16+", "Sami Diallo", 4.7, 843220, false, true, true,
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1800&q=80",
                    "Crime, Drame, Thriller", "Francais, Anglais", "Adele Moreau, Karim Benali, Victor Lane");
            stream("Orbit Kids", "Anime", "Trois enfants et un robot farceur transforment une station spatiale en terrain d'aventure familial.",
                    2024, 28, "7+", "Ana Ruiz", 4.5, 612300, false, false, true,
                    "https://images.unsplash.com/photo-1454789548928-9efd52dc4031?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=1800&q=80",
                    "Animation, Famille, Aventure", "Francais, Anglais", "Lina Fox, Tom Avery, Zed One");
            stream("The Last Summit", "Documentaire", "Des alpinistes camerounais, francais et chiliens tentent une traversee hivernale jamais filmee avec une precision aussi intime.",
                    2023, 94, "10+", "Chloe Martin", 4.9, 777040, false, true, false,
                    "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1800&q=80",
                    "Documentaire, Nature, Sport", "Francais, Anglais", "Moussa Talla, Elena Torres");
            stream("Lagos Pulse", "Serie", "Un collectif de DJs, stylistes et codeurs transforme la nuit de Lagos en empire culturel, entre amour et rivalites.",
                    2026, 45, "16+", "Nkem Okoye", 4.6, 692100, true, false, true,
                    "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?auto=format&fit=crop&w=1800&q=80",
                    "Musique, Drame, Afrique", "Francais, Anglais", "Tayo King, Sade Bloom, Mina West");
            stream("Code Rouge", "Film", "Une ingenieure en cybersecurite a quatre-vingt-dix minutes pour stopper une attaque qui manipule les hopitaux d'une capitale.",
                    2025, 103, "13+", "Marc Nguyen", 4.4, 553000, false, false, false,
                    "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1800&q=80",
                    "Thriller, Technologie, Action", "Francais, Anglais", "Eva Chen, Lucas Gray");
            stream("Cafe Minuit", "Film", "A Douala, un cafe ouvert toute la nuit devient le point de rencontre de destins amoureux, familiaux et musicaux.",
                    2024, 109, "13+", "Ariane Mbarga", 4.7, 489800, false, false, false,
                    "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1514933651103-005eec06c04b?auto=format&fit=crop&w=1800&q=80",
                    "Romance, Comedie, Afrique", "Francais", "Nadine Essome, Joel Talla");
            stream("Empire 2099", "Serie", "Des familles concurrentes controlent l'eau, la memoire et les routes orbitales dans une Afrique futuriste.",
                    2026, 50, "16+", "Iris Kamga", 4.8, 901200, true, true, true,
                    "https://images.unsplash.com/photo-1484950763426-56b5bf172dbb?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1800&q=80",
                    "Science-fiction, Drame, Politique", "Francais, Anglais", "Eli Kotto, Sarah Miles, Ken Zhou");
        }
        syncLocalDemoVideos();
        refreshStreamingArtwork();
    }

    private void seed(String name, String category, String description, String imageUrl, BigDecimal price,
                      BigDecimal oldPrice, int stock, double rating, int soldCount, boolean prime, boolean flash,
                      AppUser seller, List<String> specs) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setGallery(List.of(imageUrl, imageUrl + "&sat=-20", imageUrl + "&contrast=10"));
        product.setPrice(price);
        product.setOldPrice(oldPrice);
        product.setStock(stock);
        product.setRating(rating);
        product.setReviewCount(Math.max(12, soldCount / 5));
        product.setSoldCount(soldCount);
        product.setPrimeEligible(prime);
        product.setFlashDeal(flash);
        product.setBadge(flash ? "Offre flash" : prime ? "Prime" : "Tendance");
        product.setSeller(seller);
        product.setSpecs(specs);
        productRepository.save(product);
    }

    private void stream(String title, String type, String description, int year, int duration, String ageRating,
                        String director, double rating, int views, boolean premiumOnly, boolean featured,
                        boolean topTen, String poster, String banner, String genres, String languages, String cast) {
        StreamingMedia media = new StreamingMedia();
        media.setTitle(title);
        media.setType(type);
        media.setDescription(description);
        media.setReleaseYear(year);
        media.setDurationMinutes(duration);
        media.setAgeRating(ageRating);
        media.setDirector(director);
        media.setRating(rating);
        media.setViews(views);
        media.setPremiumOnly(premiumOnly);
        media.setFeatured(featured);
        media.setTopTen(topTen);
        media.setPosterUrl(poster);
        media.setBannerUrl(banner);
        media.setVideoUrl(videoFor(title));
        media.setSubtitleUrl("https://raw.githubusercontent.com/videojs/video.js/main/docs/examples/shared/example-captions.vtt");
        media.setGenres(Set.of(genres.split("\\s*,\\s*")));
        media.setLanguages(Set.of(languages.split("\\s*,\\s*")));
        media.setCastMembers(Set.of(cast.split("\\s*,\\s*")));
        streamingMediaRepository.save(media);
    }

    private String videoFor(String title) {
        return "/videos/movie-demo.mp4";
    }

    private void syncLocalDemoVideos() {
        upsertLocalStream("Look Back VF", "Film",
                "Deux jeunes artistes que tout oppose se rapprochent grace au dessin. Une demonstration locale ideale pour le mode Romance et Anime.",
                2026, 62, "13+", "PrimeStream Demo", 4.8, 720000, false, true, true,
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1519682337058-a94d519337bc?auto=format&fit=crop&w=1800&q=80",
                localVideo("Look_Back_VF.mp4"),
                "Anime, Drame, Romance", "Francais", "Yuki Kawai, Rin Takahashi");

        upsertLocalStream("Look Back VOSTFR", "Film",
                "Version originale sous-titree francais du drame artistique Look Back, disponible pour la demonstration multilingue.",
                2026, 62, "13+", "PrimeStream Demo", 4.7, 610000, false, false, false,
                "https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=1800&q=80",
                localVideo("Look Back VOSTFR.mp4"),
                "Anime, Drame", "Japonais, Francais", "Yuki Kawai, Rin Takahashi");

        upsertLocalStream("The Boys Saison 5 Episode 01", "Serie",
                "Premier episode local de demonstration pour tester les annonces de nouvelle saison, la lecture et le telechargement.",
                2026, 58, "18+", "PrimeStream Demo", 4.9, 980000, true, true, true,
                "https://images.unsplash.com/photo-1518709268805-4e9042af2176?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1535016120720-40c646be5580?auto=format&fit=crop&w=1800&q=80",
                localVideo("The Boys Saison 5 Ep 01 VF.mp4"),
                "Action, Thriller, Serie", "Francais", "Karl Urban, Jack Quaid, Erin Moriarty");

        upsertLocalStream("Ride Your Wave VF", "Film",
                "Une romance animee autour de l'ocean, ajoutee depuis le dossier local des videos pour la demonstration.",
                2025, 96, "10+", "PrimeStream Demo", 4.6, 430000, false, false, false,
                "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1800&q=80",
                localVideo("Ride Your Wave.VF @APE.avi"),
                "Romance, Anime, Famille", "Francais", "Hinako Mukaimizu, Minato Hinageshi");

        upsertLocalStream("PrimeStream Demo Action", "Film",
                "Court extrait local pour verifier rapidement la lecture video, la progression et le telechargement.",
                2026, 8, "13+", "PrimeStream Studio", 4.4, 188000, false, false, false,
                "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1800&q=80",
                localVideo("video_2026-05-15_01-32-45.mp4"),
                "Action, Prime Originals", "Francais", "Amina Cole, Noah Vance");
    }

    private void upsertLocalStream(String title, String type, String description, int year, int duration,
                                   String ageRating, String director, double rating, int views,
                                   boolean premiumOnly, boolean featured, boolean topTen, String poster,
                                   String banner, String videoUrl, String genres, String languages, String cast) {
        StreamingMedia media = streamingMediaRepository.findAll().stream()
                .filter(item -> item.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseGet(StreamingMedia::new);
        media.setTitle(title);
        media.setType(type);
        media.setDescription(description);
        media.setReleaseYear(year);
        media.setDurationMinutes(duration);
        media.setAgeRating(ageRating);
        media.setDirector(director);
        media.setRating(rating);
        media.setViews(views);
        media.setPremiumOnly(premiumOnly);
        media.setFeatured(featured);
        media.setTopTen(topTen);
        media.setPosterUrl(poster);
        media.setBannerUrl(banner);
        media.setVideoUrl(videoUrl);
        media.setSubtitleUrl("https://raw.githubusercontent.com/videojs/video.js/main/docs/examples/shared/example-captions.vtt");
        media.setGenres(Set.of(genres.split("\\s*,\\s*")));
        media.setLanguages(Set.of(languages.split("\\s*,\\s*")));
        media.setCastMembers(Set.of(cast.split("\\s*,\\s*")));
        streamingMediaRepository.save(media);
    }

    private String localVideo(String fileName) {
        return "/videos/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void refreshStreamingArtwork() {
        updateArtwork("Nebula City",
                "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Riviera Noire",
                "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Orbit Kids",
                "https://images.unsplash.com/photo-1614726365952-510103b1bbb4?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("The Last Summit",
                "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Lagos Pulse",
                "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Code Rouge",
                "https://images.unsplash.com/photo-1510511233900-1982d92bd835?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Cafe Minuit",
                "https://images.unsplash.com/photo-1516585427167-9f4af9627e6c?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Empire 2099",
                "https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1518709268805-4e9042af2176?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Look Back VF",
                "https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1519682337058-a94d519337bc?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Look Back VOSTFR",
                "https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("The Boys Saison 5 Episode 01",
                "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1518709268805-4e9042af2176?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("Ride Your Wave VF",
                "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1800&h=720&q=88");
        updateArtwork("PrimeStream Demo Action",
                "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&h=1350&q=88",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1800&h=720&q=88");
    }

    private void updateArtwork(String title, String posterUrl, String bannerUrl) {
        streamingMediaRepository.findAll().stream()
                .filter(media -> media.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .ifPresent(media -> {
                    media.setPosterUrl(posterUrl);
                    media.setBannerUrl(bannerUrl);
                    streamingMediaRepository.save(media);
                });
    }
}

