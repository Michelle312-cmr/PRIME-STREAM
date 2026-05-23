package com.example.authapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger("keyce-audit");
    private static final String BRAND = "PrimeStream";
    private static final String RED = "#e50914";

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.enabled:false}") boolean enabled,
                       @Value("${app.mail.from:no-reply@keyce.local}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    @Async
    public void sendPasswordReset(String to, String username, String resetLink) {
        String subject = "PrimeStream - reinitialisez votre mot de passe";
        String preheader = "Un lien de securite vous attend. Il expire dans 30 minutes.";
        String plain = """
                Bonjour %s,

                Cliquez sur ce lien pour definir un nouveau mot de passe:
                %s

                Ce lien expire dans 30 minutes.
                """.formatted(username, resetLink);

        String html = actionEmail(
                preheader,
                "Securite du compte",
                "Reinitialisez votre mot de passe",
                "Bonjour " + displayName(username) + ", utilisez ce lien securise pour choisir un nouveau mot de passe. Pour votre protection, il expire dans 30 minutes.",
                "Reinitialiser mon mot de passe",
                resetLink,
                "Si vous n'avez pas demande cette operation, ignorez simplement cet email."
        );
        send(to, subject, plain, html);
    }

    @Async
    public void sendWelcomeConfirmation(String to, String username, String confirmationLink) {
        String subject = "Bienvenue sur PrimeStream - confirmez votre compte";
        String preheader = "Votre compte est pret. Confirmez votre email pour commencer.";
        String plain = """
                Bonjour %s,

                Votre compte PrimeStream a bien ete cree.
                Cliquez sur ce lien pour confirmer votre email:
                %s

                Bon visionnage sur PrimeStream.
                """.formatted(username, confirmationLink);

        String html = actionEmail(
                preheader,
                "Bienvenue",
                "Votre ecran PrimeStream est pret",
                "Bonjour " + displayName(username) + ", confirmez votre email et retrouvez vos films, series, recommandations et notifications SUCCHERO.",
                "Confirmer mon compte",
                confirmationLink,
                "Vous pourrez ensuite personnaliser votre theme, creer votre liste et reprendre vos videos."
        );
        send(to, subject, plain, html);
    }

    @Async
    public void sendAvailabilityNotice(String to,
                                       String username,
                                       String requestedTitle,
                                       String availableTitle,
                                       String watchLink,
                                       String posterUrl,
                                       String description) {
        String subject = availableTitle + " est disponible sur PrimeStream";
        String plain = """
                Bonjour %s,

                Bonne nouvelle: "%s", lie a votre recherche "%s", est maintenant disponible sur PrimeStream.

                Regarder maintenant:
                %s

                Vous pouvez aussi l'ajouter a votre liste pour plus tard.
                """.formatted(username, availableTitle, requestedTitle, watchLink);

        String html = titleEmail(
                "SUCCHERO a trouve votre film",
                "Nouvelle disponibilite sur PrimeStream",
                availableTitle,
                "Votre recherche: " + requestedTitle,
                description,
                posterUrl,
                watchLink,
                "Regarder maintenant",
                "SUCCHERO vous previendra aussi lorsque d'autres titres recherches arrivent dans le catalogue."
        );
        send(to, subject, plain, html);
    }

    @Async
    public void sendSubscriptionReminder(String to, String username, String plan, String endsAt, String subscriptionLink) {
        String subject = "Votre abonnement PrimeStream expire bientot";
        String plain = """
                Bonjour %s,

                Votre abonnement %s arrive bientot a expiration le %s.

                Pour le renouveler ou changer de formule:
                %s

                SUCCHERO reste disponible dans l'application si vous avez besoin d'aide.
                """.formatted(username, plan, endsAt, subscriptionLink);

        String html = actionEmail(
                "Votre abonnement arrive bientot a expiration.",
                "Rappel abonnement",
                "Votre offre " + safe(plan) + " se termine bientot",
                "Bonjour " + displayName(username) + ", votre abonnement arrive a expiration le " + safe(endsAt) + ". Vous pouvez renouveler, changer d'offre ou verifier votre paiement en quelques secondes.",
                "Gerer mon abonnement",
                subscriptionLink,
                "Annulez ou changez d'offre a tout moment. Vos recommandations, votre liste et votre historique restent associes a votre compte."
        );
        send(to, subject, plain, html);
    }

    @Async
    public void sendCatalogAnnouncement(String to,
                                        String username,
                                        String title,
                                        String reason,
                                        String watchLink,
                                        String posterUrl,
                                        String description) {
        String subject = title + " est en vogue sur PrimeStream";
        String plain = """
                Bonjour %s,

                %s

                "%s" est disponible sur PrimeStream.

                Regarder maintenant:
                %s

                Vous pouvez aussi l'ajouter a votre liste pour plus tard.
                """.formatted(username, reason, title, watchLink);

        String html = titleEmail(
                "Derniers ajouts selectionnes pour vous",
                reason,
                title,
                "Selection PrimeStream",
                description,
                posterUrl,
                watchLink,
                "Voir le titre",
                "Plus vous regardez et enregistrez des titres, plus SUCCHERO affine vos recommandations."
        );
        send(to, subject, plain, html);
    }

    private void send(String to, String subject, String plainText, String html) {
        if (!enabled) {
            log.info("MAIL_DEMO to={} subject=\"{}\" htmlPreview={}", to, subject, stripForLog(html));
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, html);
            mailSender.send(message);
            log.info("MAIL_SENT to={} subject=\"{}\"", to, subject);
        } catch (MessagingException | MailException ex) {
            log.error("MAIL_FAILED to={} subject=\"{}\" reason={}", to, subject, ex.getMessage());
        }
    }

    private String actionEmail(String preheader,
                               String eyebrow,
                               String title,
                               String message,
                               String buttonLabel,
                               String buttonUrl,
                               String footnote) {
        return shell(preheader, """
                <tr>
                  <td style="padding:0 26px 26px 26px;">
                    <div style="background:#141414;border-radius:22px;overflow:hidden;border:1px solid #242424;">
                      <div style="padding:34px 28px 16px 28px;background:linear-gradient(145deg,#3a0005,#141414 58%,#050505);">
                        <div style="font-size:13px;line-height:18px;color:#bbbbbb;text-transform:uppercase;letter-spacing:2px;font-weight:700;">""" + safe(eyebrow) + """
                        </div>
                        <h1 style="margin:14px 0 12px 0;color:#ffffff;font-size:34px;line-height:40px;font-weight:800;">""" + safe(title) + """
                        </h1>
                        <p style="margin:0;color:#dddddd;font-size:18px;line-height:29px;">""" + safe(message) + """
                        </p>
                      </div>
                      """ + button(buttonLabel, buttonUrl) + """
                    </div>
                  </td>
                </tr>
                """ + benefitsBlock() + """
                <tr>
                  <td style="padding:0 26px 34px 26px;">
                    <p style="margin:0;color:#9b9b9b;font-size:14px;line-height:22px;">""" + safe(footnote) + """
                    </p>
                  </td>
                </tr>
                """);
    }

    private String titleEmail(String preheader,
                              String eyebrow,
                              String title,
                              String subtitle,
                              String description,
                              String posterUrl,
                              String watchLink,
                              String buttonLabel,
                              String footnote) {
        String image = imageBlock(posterUrl, title, watchLink);
        return shell(preheader, """
                <tr>
                  <td style="padding:0 26px 26px 26px;">
                    <div style="background:linear-gradient(150deg,#520012,#1a1014 46%,#050505);border-radius:22px;overflow:hidden;border:1px solid #282828;">
                      <div style="text-align:center;padding:30px 20px 18px 20px;">
                        <div style="font-size:13px;line-height:18px;color:#f0f0f0;text-transform:uppercase;letter-spacing:2px;font-weight:700;">""" + safe(eyebrow) + """
                        </div>
                        <h1 style="margin:16px auto 0 auto;color:#ffffff;font-size:31px;line-height:38px;font-weight:900;max-width:430px;">""" + safe(title) + """
                        </h1>
                        <p style="margin:8px auto 0 auto;color:#d7d7d7;font-size:16px;line-height:24px;max-width:430px;">""" + safe(subtitle) + """
                        </p>
                      </div>
                      """ + image + """
                      <div style="padding:22px 28px 8px 28px;">
                        <p style="margin:0;color:#dddddd;font-size:18px;line-height:30px;">""" + safe(shortDescription(description)) + """
                        </p>
                      </div>
                      """ + button(buttonLabel, watchLink) + """
                    </div>
                  </td>
                </tr>
                """ + recommendationsBlock(watchLink) + """
                <tr>
                  <td style="padding:0 26px 34px 26px;">
                    <p style="margin:0;color:#9b9b9b;font-size:14px;line-height:22px;">""" + safe(footnote) + """
                    </p>
                  </td>
                </tr>
                """);
    }

    private String shell(String preheader, String content) {
        return """
                <!doctype html>
                <html lang="fr">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>PrimeStream</title>
                </head>
                <body style="margin:0;padding:0;background:#e9eef4;font-family:Arial,Helvetica,sans-serif;">
                  <div style="display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;">""" + safe(preheader) + """
                  </div>
                  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="background:#e9eef4;margin:0;padding:0;">
                    <tr>
                      <td align="center" style="padding:24px 10px;">
                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="width:100%;max-width:560px;background:#000000;border-radius:0;overflow:hidden;">
                          <tr>
                            <td style="padding:26px 26px 18px 26px;text-align:center;">
                              <div style="font-size:42px;line-height:42px;color:""" + RED + """
                ;font-weight:900;letter-spacing:-3px;">P</div>
                              <div style="margin-top:8px;color:#ffffff;font-size:15px;line-height:20px;font-weight:700;letter-spacing:1px;">""" + BRAND + """
                              </div>
                            </td>
                          </tr>
                          """ + content + """
                          <tr>
                            <td style="padding:22px 26px 30px 26px;border-top:1px solid #1f1f1f;">
                              <p style="margin:0 0 8px 0;color:#777777;font-size:12px;line-height:18px;">Cet email vous est envoye parce que vous utilisez PrimeStream.</p>
                              <p style="margin:0;color:#777777;font-size:12px;line-height:18px;">SUCCHERO reste disponible dans l'application pour toute question liee au catalogue, a l'abonnement ou aux notifications.</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """;
    }

    private String imageBlock(String posterUrl, String title, String link) {
        String url = normalizeImageUrl(posterUrl, link);
        if (url.isBlank()) {
            return """
                    <div style="margin:14px 24px 0 24px;border-radius:18px;background:linear-gradient(135deg,#99151d,#222 48%,#000);height:230px;text-align:center;">
                      <div style="padding-top:78px;color:#ffffff;font-size:31px;line-height:38px;font-weight:900;">""" + safe(title) + """
                      </div>
                    </div>
                    """;
        }
        return """
                <a href=\"""" + safeAttribute(link) + """
                \" style="display:block;margin:14px 24px 0 24px;text-decoration:none;">
                  <img src=\"""" + safeAttribute(url) + """
                \" alt=\"""" + safeAttribute(title) + """
                \" width="512" style="display:block;width:100%;max-width:512px;border:0;border-radius:18px;object-fit:cover;background:#222222;">
                </a>
                """;
    }

    private String button(String label, String url) {
        return """
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0">
                  <tr>
                    <td align="center" style="padding:22px 28px 30px 28px;">
                      <a href=\"""" + safeAttribute(url) + """
                \" style="display:block;background:""" + RED + """
                ;border-radius:999px;color:#ffffff;font-size:18px;line-height:24px;font-weight:800;text-decoration:none;padding:17px 24px;text-align:center;">""" + safe(label) + """
                      </a>
                    </td>
                  </tr>
                </table>
                """;
    }

    private String recommendationsBlock(String watchLink) {
        return """
                <tr>
                  <td style="padding:0 26px 26px 26px;">
                    <div style="background:#171717;border-radius:20px;padding:24px 20px;border:1px solid #242424;">
                      <h2 style="margin:0 0 16px 0;color:#ffffff;font-size:26px;line-height:32px;">Derniers ajouts selectionnes pour vous</h2>
                      <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0">
                        <tr>
                          <td style="padding:0 6px 0 0;width:33.33%;">
                            <a href=\"""" + safeAttribute(watchLink) + """
                \" style="display:block;text-decoration:none;background:#2a2a2a;border-radius:12px;padding:18px 8px;color:#ffffff;text-align:center;font-weight:800;">Action</a>
                          </td>
                          <td style="padding:0 3px;width:33.33%;">
                            <a href=\"""" + safeAttribute(watchLink) + """
                \" style="display:block;text-decoration:none;background:#2a2a2a;border-radius:12px;padding:18px 8px;color:#ffffff;text-align:center;font-weight:800;">Series</a>
                          </td>
                          <td style="padding:0 0 0 6px;width:33.33%;">
                            <a href=\"""" + safeAttribute(watchLink) + """
                \" style="display:block;text-decoration:none;background:#2a2a2a;border-radius:12px;padding:18px 8px;color:#ffffff;text-align:center;font-weight:800;">Romance</a>
                          </td>
                        </tr>
                      </table>
                    </div>
                  </td>
                </tr>
                """;
    }

    private String benefitsBlock() {
        return """
                <tr>
                  <td style="padding:0 26px 26px 26px;">
                    <div style="background:#171717;border-radius:20px;padding:24px 22px;border:1px solid #242424;">
                      <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0">
                        <tr>
                          <td style="width:52px;color:#bdbdbd;font-size:28px;line-height:34px;">✓</td>
                          <td style="color:#ffffff;font-size:18px;line-height:27px;">Annulez ou changez d'offre a tout moment.</td>
                        </tr>
                        <tr>
                          <td style="width:52px;color:#bdbdbd;font-size:28px;line-height:34px;padding-top:16px;">◇</td>
                          <td style="color:#ffffff;font-size:18px;line-height:27px;padding-top:16px;">Obtenez des recommandations selon les titres regardes et enregistres.</td>
                        </tr>
                      </table>
                    </div>
                  </td>
                </tr>
                """;
    }

    private String normalizeImageUrl(String imageUrl, String actionLink) {
        String value = imageUrl == null ? "" : imageUrl.trim();
        if (value.isBlank()) {
            return "";
        }
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return value;
        }
        if (value.startsWith("/")) {
            return baseUrl(actionLink) + value;
        }
        return value;
    }

    private String baseUrl(String link) {
        if (link == null) {
            return "";
        }
        int scheme = link.indexOf("://");
        if (scheme < 0) {
            return "";
        }
        int slash = link.indexOf('/', scheme + 3);
        return slash > 0 ? link.substring(0, slash) : link;
    }

    private String shortDescription(String description) {
        String value = description == null || description.isBlank()
                ? "Disponible maintenant dans votre catalogue PrimeStream."
                : description.trim();
        return value.length() > 220 ? value.substring(0, 217) + "..." : value;
    }

    private String displayName(String username) {
        return username == null || username.isBlank() ? "cher utilisateur" : username;
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String safeAttribute(String value) {
        return safe(value).replace("'", "&#39;");
    }

    private String stripForLog(String html) {
        return html == null ? "" : html.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }
}
