package com.example.authapp.service;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.AvailabilityRequest;
import com.example.authapp.model.SuccheroMessage;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.repo.AvailabilityRequestRepository;
import com.example.authapp.repo.SuccheroMessageRepository;
import com.example.authapp.repo.StreamingMediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SuccheroService {

    private final AvailabilityRequestRepository availabilityRequestRepository;
    private final SuccheroMessageRepository succheroMessageRepository;
    private final StreamingMediaRepository mediaRepository;
    private final MailService mailService;
    private final String publicBaseUrl;

    public SuccheroService(AvailabilityRequestRepository availabilityRequestRepository,
                           SuccheroMessageRepository succheroMessageRepository,
                           StreamingMediaRepository mediaRepository,
                           MailService mailService,
                           @Value("${app.public-base-url:http://localhost:8091}") String publicBaseUrl) {
        this.availabilityRequestRepository = availabilityRequestRepository;
        this.succheroMessageRepository = succheroMessageRepository;
        this.mediaRepository = mediaRepository;
        this.mailService = mailService;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Transactional
    public boolean requestAvailability(AppUser user, String searchedTitle, String preferredGenre) {
        String title = clean(searchedTitle);
        if (title.isBlank()) {
            return false;
        }
        if (availabilityRequestRepository.existsByUserAndSearchedTitleIgnoreCaseAndNotifiedFalse(user, title)) {
            return false;
        }
        AvailabilityRequest request = new AvailabilityRequest();
        request.setUser(user);
        request.setSearchedTitle(title);
        request.setPreferredGenre(clean(preferredGenre));
        availabilityRequestRepository.save(request);
        return true;
    }

    public List<AvailabilityRequest> recentRequests(AppUser user) {
        return availabilityRequestRepository.findTop8ByUserOrderByCreatedAtDesc(user);
    }

    public List<AvailabilityRequest> alerts(AppUser user) {
        return availabilityRequestRepository.findTop5ByUserAndNotifiedTrueOrderByNotifiedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public String greeting(AppUser user, List<AvailabilityRequest> alerts) {
        String name = user.getFullName() == null || user.getFullName().isBlank()
                ? user.getUsername() : user.getFullName();
        if (!alerts.isEmpty()) {
            AvailabilityRequest alert = alerts.get(0);
            StreamingMedia media = alert.getMatchedMedia();
            String title = media != null ? media.getTitle() : "un contenu";
            return "Bonjour " + name + ", " + title
                    + " est maintenant disponible. SUCCHERO l'a garde au chaud pour vous.";
        }
        return "Bonjour " + name + ", je suis SUCCHERO. Dites-moi ce que vous voulez regarder et je vous aide a le trouver.";
    }

    public List<SuccheroMessage> conversation(AppUser user) {
        return succheroMessageRepository.findTop12ByUserOrderByCreatedAtDesc(user).stream()
                .sorted(Comparator.comparing(SuccheroMessage::getCreatedAt))
                .toList();
    }

    @Transactional
    public String chat(AppUser user, String message) {
        String cleanMessage = clean(message);
        if (cleanMessage.isBlank()) {
            return "Je vous ecoute. Posez-moi une question sur PrimeStream, un film, un abonnement, votre profil ou vos notifications.";
        }
        saveMessage(user, SuccheroMessage.Role.USER, cleanMessage);
        String reply = answer(user, cleanMessage);
        saveMessage(user, SuccheroMessage.Role.ASSISTANT, reply);
        return reply;
    }

    private String answer(AppUser user, String message) {
        String text = normalize(message);
        if (!isPrimeStreamTopic(text)) {
            return "Je reste concentre sur PrimeStream: films, series, recherche, watchlist, abonnement, profil, paiement, notifications, acteurs et support. Posez-moi une question dans ce cadre et je vous aide.";
        }
        if (containsAny(text, "bonjour", "salut", "hello", "bonsoir")) {
            return "Bonjour " + displayName(user) + ". Je peux vous aider a choisir un film, retrouver une page, gerer votre abonnement ou activer une notification de disponibilite.";
        }
        if (containsAny(text, "qui es tu", "tu es qui", "succhero", "assistant")) {
            return "Je suis SUCCHERO, l'assistant PrimeStream. Je peux discuter avec vous du catalogue, des themes, des acteurs, des telechargements, des abonnements, des notifications et de l'acces reseau.";
        }
        if (containsAny(text, "theme", "couleur", "ambiance", "mode")) {
            return "Utilisez le menu Changer de theme dans la barre du haut. Quand vous choisissez Romance, Action, Famille, Science-fiction ou Afrique, l'interface change et le catalogue s'ouvre sur les films du genre correspondant.";
        }
        if (containsAny(text, "romance", "amour", "romantique")) {
            return "Le mode Romance applique une ambiance plus douce et affiche les films de romance disponibles, comme Cafe Minuit si le titre est dans le catalogue.";
        }
        if (containsAny(text, "action", "combat", "thriller")) {
            return "Le mode Action affiche les contenus Action et Thriller, avec une ambiance visuelle plus intense.";
        }
        if (containsAny(text, "enfant", "famille", "parental", "age")) {
            return "Dans votre profil, reglez le niveau age et le mode Famille. Le prochain lot pourra ajouter un vrai PIN parental comme sur Prime Video.";
        }
        if (containsAny(text, "langue", "anglais", "espagnol", "francais", "traduction", "translate")) {
            return "La barre du haut permet de choisir FR, EN ou ES. Les libelles principaux de l'interface changent directement, et les langues disponibles d'un film restent affichees dans sa fiche.";
        }
        if (containsAny(text, "telecharger", "download", "hors ligne")) {
            return "Sur la page d'un film, utilisez le bouton Telecharger. Les fichiers locaux de demonstration sont servis depuis PrimeStream et peuvent etre recuperes par le navigateur.";
        }
        if (containsAny(text, "pas disponible", "indisponible", "notification", "prevenir", "arrive")) {
            return "Allez dans Films et series, recherchez le titre. Si aucun resultat ne sort, cliquez sur Activer la notification. Je le garde en base et je vous envoie un mail quand il est ajoute.";
        }
        if (containsAny(text, "suite", "episode", "nouvel episode", "nouvelle saison")) {
            return "Quand une suite ou un nouvel episode est ajoute au catalogue, PrimeStream peut envoyer un email aux abonnes. Le tableau admin permet d'ajouter le contenu, puis les taches planifiees s'occupent des annonces.";
        }
        if (containsAny(text, "abonnement", "payer", "paiement", "premium", "standard", "gratuit")) {
            return "La page Abonnement propose Gratuit, Standard et Premium. Les rappels sont envoyes par tache planifiee quand la date de fin approche. En demo, le paiement reste simule.";
        }
        if (containsAny(text, "ma liste", "watchlist", "plus tard", "favori")) {
            return "Depuis une fiche film, utilisez Ajouter a ma liste. Vous retrouverez tous les titres dans Profil > Ma liste.";
        }
        if (containsAny(text, "continuer", "historique", "reprendre", "progression")) {
            return "Quand vous regardez une video, PrimeStream sauvegarde automatiquement la progression. Vous retrouvez ensuite le film dans Profil > Continuer a regarder.";
        }
        if (containsAny(text, "acteur", "actrice", "instagram", "tiktok", "casting")) {
            return "Sur une fiche film, les noms des acteurs sont cliquables. J'utilise une recherche TikTok parce qu'elle marche meme quand on ne connait pas le compte officiel exact.";
        }
        if (containsAny(text, "serveur", "reseau", "autre pc", "lan", "wifi")) {
            return "Le serveur ecoute sur 0.0.0.0:8091. Depuis un autre PC du meme reseau, ouvrez http://ADRESSE-IP-DU-SERVEUR:8091 apres avoir autorise le port 8091 dans le pare-feu Windows.";
        }
        if (containsAny(text, "heberger", "deployer", "deployment", "production")) {
            return "Pour heberger PrimeStream, il faut construire le jar, utiliser une base MySQL distante, definir APP_PUBLIC_BASE_URL avec le domaine public, puis lancer l'application derriere HTTPS.";
        }
        if (containsAny(text, "recommande", "conseille", "regarder", "film", "serie", "catalogue")) {
            return recommendationAnswer(text);
        }
        if (containsAny(text, "aide", "support", "probleme", "bug")) {
            return "Pour un souci de lecture, compte, paiement ou catalogue, ouvrez Service client. Vous pouvez aussi me decrire le probleme ici s'il concerne PrimeStream.";
        }
        return "Je peux vous guider dans PrimeStream: recherche de films, watchlist, abonnement, profil, notifications de disponibilite, acteurs, lecture video et acces reseau.";
    }

    private String recommendationAnswer(String text) {
        String genre = genreFromText(text);
        List<StreamingMedia> matches = genre.isBlank()
                ? mediaRepository.mostWatched(org.springframework.data.domain.PageRequest.of(0, 3))
                : mediaRepository.findByGenresContainingIgnoreCase(genre).stream().limit(3).toList();
        if (matches.isEmpty()) {
            return "Je n'ai pas encore de titre parfait pour ce genre. Essayez une recherche dans Films et series, puis activez une notification si le film manque.";
        }
        List<String> titles = new ArrayList<>();
        for (StreamingMedia media : matches) {
            titles.add(media.getTitle() + " (" + media.getType() + ", " + media.getReleaseYear() + ")");
        }
        return "Je vous conseille " + String.join(", ", titles) + ". Vous pouvez les ouvrir depuis Films et series ou les ajouter a Ma liste.";
    }

    private String genreFromText(String text) {
        if (containsAny(text, "romance", "amour")) return "Romance";
        if (containsAny(text, "action", "thriller")) return "Action";
        if (containsAny(text, "science", "sci-fi", "fiction")) return "Science-fiction";
        if (containsAny(text, "famille", "enfant")) return "Famille";
        if (containsAny(text, "afrique", "africain", "douala", "lagos")) return "Afrique";
        if (containsAny(text, "documentaire", "nature", "sport")) return "Documentaire";
        return "";
    }

    private boolean isPrimeStreamTopic(String text) {
        return containsAny(text, "bonjour", "salut", "hello", "bonsoir")
                || containsAny(text, "film", "serie", "video", "prime", "primestream", "succhero", "catalogue")
                || containsAny(text, "watchlist", "liste", "plus tard", "favori", "recommande", "regarder", "telecharger", "download")
                || containsAny(text, "abonnement", "paiement", "premium", "standard", "gratuit", "payer")
                || containsAny(text, "profil", "theme", "couleur", "langue", "traduction", "romance", "action", "famille", "science", "afrique")
                || containsAny(text, "notification", "disponible", "indisponible", "prevenir", "arrive", "suite", "episode", "saison")
                || containsAny(text, "acteur", "actrice", "tiktok", "instagram", "casting")
                || containsAny(text, "serveur", "reseau", "autre pc", "pc", "ordinateur", "lan", "wifi", "heberger", "deployer")
                || containsAny(text, "support", "aide", "bug", "lecture", "historique", "progression", "parental");
    }

    private void saveMessage(AppUser user, SuccheroMessage.Role role, String content) {
        SuccheroMessage message = new SuccheroMessage();
        message.setUser(user);
        message.setRole(role);
        message.setContent(content.length() > 1400 ? content.substring(0, 1400) : content);
        succheroMessageRepository.save(message);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void checkAvailabilityRequests() {
        availabilityRequestRepository.findByNotifiedFalseOrderByCreatedAtAsc()
                .forEach(this::notifyIfAvailable);
    }

    @Transactional
    public void notifyIfAvailable(AvailabilityRequest request) {
        mediaRepository.findTop5ByTitleContainingIgnoreCaseOrderByViewsDesc(request.getSearchedTitle()).stream()
                .findFirst()
                .ifPresent(media -> {
                    request.setMatchedMedia(media);
                    request.setNotified(true);
                    request.setNotifiedAt(LocalDateTime.now());
                    availabilityRequestRepository.save(request);
                    mailService.sendAvailabilityNotice(
                            request.getUser().getEmail(),
                            request.getUser().getUsername(),
                            request.getSearchedTitle(),
                            media.getTitle(),
                            watchLink(media),
                            media.getPosterUrl(),
                            media.getDescription()
                    );
                });
    }

    private String watchLink(StreamingMedia media) {
        return publicBaseUrl.replaceAll("/+$", "") + "/watch/" + media.getId();
    }

    private String displayName(AppUser user) {
        return user.getFullName() == null || user.getFullName().isBlank() ? user.getUsername() : user.getFullName();
    }

    private boolean containsAny(String text, String... keywords) {
        Set<String> values = Set.of(keywords);
        return values.stream().anyMatch(text::contains);
    }

    private String normalize(String value) {
        return clean(value).toLowerCase(Locale.ROOT);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
