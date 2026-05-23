(function () {
    const root = document.documentElement;
    const body = document.body;
    const moodGenres = {
        classic: "",
        romance: "Romance",
        action: "Action",
        family: "Famille",
        science: "Science-fiction",
        afrique: "Afrique"
    };

    const translations = {
        fr: {
            "nav.home": "Accueil",
            "nav.browse": "Films et series",
            "nav.profile": "Profil",
            "nav.subscription": "Abonnement",
            "nav.support": "Service client",
            "nav.login": "Connexion",
            "nav.logout": "Sortir",
            "nav.movies": "Films",
            "nav.series": "Series",
            "nav.docs": "Documentaires",
            "nav.myspace": "Mon espace",
            "nav.payment": "Abonnement et paiement",
            "nav.help": "Aide et service client",
            "mood.classic": "Theme",
            "mood.romance": "Romance",
            "mood.action": "Action",
            "mood.family": "Famille",
            "mood.science": "Science-fiction",
            "mood.africa": "Afrique",
            "common.close": "Fermer",
            "common.send": "Envoyer",
            "common.watch": "Voir",
            "common.watchLater": "A regarder plus tard",
            "common.play": "Lire",
            "common.explore": "Explorer",
            "common.seeAll": "Tout voir",
            "common.download": "Telecharger",
            "home.trending": "Tendances maintenant",
            "home.top10": "Top 10 dans votre pays",
            "home.new": "Nouveautes",
            "home.changeTheme": "Changer le theme",
            "browse.title": "Films, series et documentaires",
            "browse.subtitle": "Recherche intelligente, filtres et recommandations facon plateforme premium.",
            "browse.search": "Titre, acteur, realisateur...",
            "browse.allGenres": "Tous les genres",
            "browse.allFormats": "Tous les formats",
            "browse.year": "Annee",
            "browse.searchButton": "Rechercher",
            "browse.notifyText": "Activez une notification et SUCCHERO vous previendra par email quand le titre arrive dans PrimeStream.",
            "browse.notifyButton": "Activer la notification",
            "watch.genres": "Genres",
            "watch.languages": "Langues",
            "watch.cast": "Avec",
            "watch.director": "Realisation",
            "watch.review": "Votre avis",
            "watch.comment": "Votre commentaire",
            "watch.publish": "Publier",
            "watch.recentReviews": "Avis recents",
            "watch.next": "Regarder ensuite",
            "profile.manageSubscription": "Gerer l'abonnement",
            "profile.profiles": "Profils",
            "profile.recentlyWatched": "Vus recemment",
            "profile.myList": "Dans ma liste",
            "profile.accountInfo": "Informations du compte",
            "profile.username": "Nom utilisateur",
            "profile.phone": "Telephone",
            "profile.roles": "Roles",
            "profile.playbackPrefs": "Preferences de lecture",
            "profile.language": "Langue",
            "profile.quality": "Qualite",
            "profile.maturity": "Niveau age",
            "profile.mode": "Mode",
            "profile.favoriteGenre": "Genre favori",
            "profile.downloads": "Telechargements",
            "profile.notifications": "Notifications",
            "profile.realtime": "Temps reel",
            "profile.realtimeText": "Nouveaux episodes et rappels",
            "profile.support": "Support",
            "profile.contactSupport": "Contacter le service client",
            "profile.personalization": "Personnalisation",
            "profile.smartTheme": "Theme intelligent PrimeStream",
            "profile.smartThemeText": "Le mode choisi change l'ambiance visuelle et favorise les films du genre correspondant sur l'accueil.",
            "profile.save": "Enregistrer",
            "profile.succheroNotifications": "Notifications SUCCHERO",
            "profile.continueWatching": "Continuer a regarder",
            "profile.resume": "Reprendre",
            "profile.listTitle": "Ma liste",
            "subscription.title": "Choisissez votre experience PrimeStream",
            "subscription.subtitle": "Paiement en temps reel prepare cote interface. En demo, l'activation est immediate; avec des cles Stripe/PayPal, le backend peut etre branche ensuite.",
            "subscription.free": "Gratuit",
            "subscription.freeText": "480p, 1 ecran, publicites.",
            "subscription.activate": "Activer",
            "subscription.standardText": "Full HD, 2 ecrans, sans publicites.",
            "subscription.trial": "Essayer 7 jours",
            "subscription.premiumText": "4K HDR, 4 ecrans, contenus exclusifs.",
            "subscription.upgrade": "Passer Premium",
            "subscription.securePayment": "Paiement securise",
            "subscription.paymentText": "Carte bancaire, PayPal Sandbox ou Mobile Money. Les informations ci-dessous simulent une experience proche d'Amazon Prime Video.",
            "subscription.cardName": "Nom sur la carte",
            "subscription.zip": "Code postal",
            "subscription.verify": "Verifier le paiement",
            "support.kicker": "Service client",
            "support.title": "Aide PrimeStream",
            "support.subtitle": "Support streaming, abonnement, paiement, lecture video et compte utilisateur.",
            "support.playback": "Probleme de lecture",
            "support.playbackText": "Verifiez votre connexion, rechargez la page, puis essayez une video locale MP4.",
            "support.account": "Compte et connexion",
            "support.accountText": "Creation de compte, recuperation de mot de passe, email de confirmation et profils.",
            "support.subscription": "Abonnement",
            "support.subscriptionText": "Changement de plan, essai gratuit, paiement simule ou preparation Stripe/PayPal.",
            "support.contact": "Contacter le support",
            "support.email": "Votre email",
            "support.playbackOption": "Lecture video",
            "support.paymentOption": "Paiement",
            "support.accountOption": "Compte",
            "support.catalogOption": "Catalogue",
            "support.problem": "Expliquez le probleme",
            "support.send": "Envoyer la demande",
            "succhero.subtitle": "Assistant PrimeStream",
            "succhero.placeholder": "Message SUCCHERO",
            "toast.themeSaved": "Theme applique. Le catalogue affiche les films correspondants."
        },
        en: {
            "nav.home": "Home",
            "nav.browse": "Movies and series",
            "nav.profile": "Profile",
            "nav.subscription": "Subscription",
            "nav.support": "Help",
            "nav.login": "Sign in",
            "nav.logout": "Sign out",
            "nav.movies": "Movies",
            "nav.series": "Series",
            "nav.docs": "Documentaries",
            "nav.myspace": "My space",
            "nav.payment": "Subscription and payment",
            "nav.help": "Help center",
            "mood.classic": "Theme",
            "mood.romance": "Romance",
            "mood.action": "Action",
            "mood.family": "Family",
            "mood.science": "Science fiction",
            "mood.africa": "Africa",
            "common.close": "Close",
            "common.send": "Send",
            "common.watch": "Watch",
            "common.watchLater": "Watch later",
            "common.play": "Play",
            "common.explore": "Explore",
            "common.seeAll": "See all",
            "common.download": "Download",
            "home.trending": "Trending now",
            "home.top10": "Top 10 in your country",
            "home.new": "New releases",
            "home.changeTheme": "Change theme",
            "browse.title": "Movies, series and documentaries",
            "browse.subtitle": "Smart search, filters and premium platform recommendations.",
            "browse.search": "Title, actor, director...",
            "browse.allGenres": "All genres",
            "browse.allFormats": "All formats",
            "browse.year": "Year",
            "browse.searchButton": "Search",
            "browse.notifyText": "Turn on a notification and SUCCHERO will email you when the title arrives on PrimeStream.",
            "browse.notifyButton": "Turn on notification",
            "watch.genres": "Genres",
            "watch.languages": "Languages",
            "watch.cast": "Cast",
            "watch.director": "Director",
            "watch.review": "Your review",
            "watch.comment": "Your comment",
            "watch.publish": "Publish",
            "watch.recentReviews": "Recent reviews",
            "watch.next": "Watch next",
            "profile.manageSubscription": "Manage subscription",
            "profile.profiles": "Profiles",
            "profile.recentlyWatched": "Recently watched",
            "profile.myList": "In my list",
            "profile.accountInfo": "Account information",
            "profile.username": "Username",
            "profile.phone": "Phone",
            "profile.roles": "Roles",
            "profile.playbackPrefs": "Playback preferences",
            "profile.language": "Language",
            "profile.quality": "Quality",
            "profile.maturity": "Age rating",
            "profile.mode": "Mode",
            "profile.favoriteGenre": "Favorite genre",
            "profile.downloads": "Downloads",
            "profile.notifications": "Notifications",
            "profile.realtime": "Real time",
            "profile.realtimeText": "New episodes and reminders",
            "profile.support": "Support",
            "profile.contactSupport": "Contact customer service",
            "profile.personalization": "Personalization",
            "profile.smartTheme": "Smart PrimeStream theme",
            "profile.smartThemeText": "The selected mode changes the visual mood and promotes matching movies on the home page.",
            "profile.save": "Save",
            "profile.succheroNotifications": "SUCCHERO notifications",
            "profile.continueWatching": "Continue watching",
            "profile.resume": "Resume",
            "profile.listTitle": "My list",
            "subscription.title": "Choose your PrimeStream experience",
            "subscription.subtitle": "Real-time payment is prepared in the interface. In demo mode, activation is immediate; Stripe/PayPal keys can be connected later.",
            "subscription.free": "Free",
            "subscription.freeText": "480p, 1 screen, ads.",
            "subscription.activate": "Activate",
            "subscription.standardText": "Full HD, 2 screens, ad-free.",
            "subscription.trial": "Try 7 days",
            "subscription.premiumText": "4K HDR, 4 screens, exclusive content.",
            "subscription.upgrade": "Go Premium",
            "subscription.securePayment": "Secure payment",
            "subscription.paymentText": "Bank card, PayPal Sandbox or Mobile Money. The form below simulates an Amazon Prime Video-like experience.",
            "subscription.cardName": "Name on card",
            "subscription.zip": "Postal code",
            "subscription.verify": "Verify payment",
            "support.kicker": "Customer service",
            "support.title": "PrimeStream help",
            "support.subtitle": "Streaming, subscription, payment, video playback and account support.",
            "support.playback": "Playback issue",
            "support.playbackText": "Check your connection, reload the page, then try a local MP4 video.",
            "support.account": "Account and sign-in",
            "support.accountText": "Account creation, password recovery, confirmation emails and profiles.",
            "support.subscription": "Subscription",
            "support.subscriptionText": "Plan changes, free trial, simulated payment or Stripe/PayPal preparation.",
            "support.contact": "Contact support",
            "support.email": "Your email",
            "support.playbackOption": "Video playback",
            "support.paymentOption": "Payment",
            "support.accountOption": "Account",
            "support.catalogOption": "Catalogue",
            "support.problem": "Explain the issue",
            "support.send": "Send request",
            "succhero.subtitle": "PrimeStream assistant",
            "succhero.placeholder": "Message SUCCHERO",
            "toast.themeSaved": "Theme applied. The catalogue now shows matching movies."
        },
        es: {
            "nav.home": "Inicio",
            "nav.browse": "Peliculas y series",
            "nav.profile": "Perfil",
            "nav.subscription": "Suscripcion",
            "nav.support": "Ayuda",
            "nav.login": "Conectar",
            "nav.logout": "Salir",
            "nav.movies": "Peliculas",
            "nav.series": "Series",
            "nav.docs": "Documentales",
            "nav.myspace": "Mi espacio",
            "nav.payment": "Suscripcion y pago",
            "nav.help": "Centro de ayuda",
            "mood.classic": "Tema",
            "mood.romance": "Romance",
            "mood.action": "Accion",
            "mood.family": "Familia",
            "mood.science": "Ciencia ficcion",
            "mood.africa": "Africa",
            "common.close": "Cerrar",
            "common.send": "Enviar",
            "common.watch": "Ver",
            "common.watchLater": "Ver mas tarde",
            "common.play": "Reproducir",
            "common.explore": "Explorar",
            "common.seeAll": "Ver todo",
            "common.download": "Descargar",
            "home.trending": "Tendencias ahora",
            "home.top10": "Top 10 en tu pais",
            "home.new": "Novedades",
            "home.changeTheme": "Cambiar tema",
            "browse.title": "Peliculas, series y documentales",
            "browse.subtitle": "Busqueda inteligente, filtros y recomendaciones premium.",
            "browse.search": "Titulo, actor, director...",
            "browse.allGenres": "Todos los generos",
            "browse.allFormats": "Todos los formatos",
            "browse.year": "Ano",
            "browse.searchButton": "Buscar",
            "browse.notifyText": "Activa una notificacion y SUCCHERO te enviara un email cuando el titulo llegue a PrimeStream.",
            "browse.notifyButton": "Activar notificacion",
            "watch.genres": "Generos",
            "watch.languages": "Idiomas",
            "watch.cast": "Reparto",
            "watch.director": "Direccion",
            "watch.review": "Tu opinion",
            "watch.comment": "Tu comentario",
            "watch.publish": "Publicar",
            "watch.recentReviews": "Opiniones recientes",
            "watch.next": "Ver despues",
            "profile.manageSubscription": "Gestionar suscripcion",
            "profile.profiles": "Perfiles",
            "profile.recentlyWatched": "Visto recientemente",
            "profile.myList": "En mi lista",
            "profile.accountInfo": "Informacion de la cuenta",
            "profile.username": "Usuario",
            "profile.phone": "Telefono",
            "profile.roles": "Roles",
            "profile.playbackPrefs": "Preferencias de reproduccion",
            "profile.language": "Idioma",
            "profile.quality": "Calidad",
            "profile.maturity": "Nivel de edad",
            "profile.mode": "Modo",
            "profile.favoriteGenre": "Genero favorito",
            "profile.downloads": "Descargas",
            "profile.notifications": "Notificaciones",
            "profile.realtime": "Tiempo real",
            "profile.realtimeText": "Nuevos episodios y recordatorios",
            "profile.support": "Soporte",
            "profile.contactSupport": "Contactar servicio al cliente",
            "profile.personalization": "Personalizacion",
            "profile.smartTheme": "Tema inteligente PrimeStream",
            "profile.smartThemeText": "El modo elegido cambia el ambiente visual y destaca peliculas del genero correspondiente en el inicio.",
            "profile.save": "Guardar",
            "profile.succheroNotifications": "Notificaciones SUCCHERO",
            "profile.continueWatching": "Continuar viendo",
            "profile.resume": "Reanudar",
            "profile.listTitle": "Mi lista",
            "subscription.title": "Elige tu experiencia PrimeStream",
            "subscription.subtitle": "El pago en tiempo real esta preparado en la interfaz. En demo, la activacion es inmediata; las claves Stripe/PayPal pueden conectarse despues.",
            "subscription.free": "Gratis",
            "subscription.freeText": "480p, 1 pantalla, anuncios.",
            "subscription.activate": "Activar",
            "subscription.standardText": "Full HD, 2 pantallas, sin anuncios.",
            "subscription.trial": "Probar 7 dias",
            "subscription.premiumText": "4K HDR, 4 pantallas, contenidos exclusivos.",
            "subscription.upgrade": "Pasar a Premium",
            "subscription.securePayment": "Pago seguro",
            "subscription.paymentText": "Tarjeta bancaria, PayPal Sandbox o Mobile Money. Los datos siguientes simulan una experiencia cercana a Amazon Prime Video.",
            "subscription.cardName": "Nombre en la tarjeta",
            "subscription.zip": "Codigo postal",
            "subscription.verify": "Verificar pago",
            "support.kicker": "Servicio al cliente",
            "support.title": "Ayuda PrimeStream",
            "support.subtitle": "Soporte de streaming, suscripcion, pago, reproduccion de video y cuenta de usuario.",
            "support.playback": "Problema de reproduccion",
            "support.playbackText": "Comprueba tu conexion, recarga la pagina y prueba un video local MP4.",
            "support.account": "Cuenta e inicio de sesion",
            "support.accountText": "Creacion de cuenta, recuperacion de contrasena, email de confirmacion y perfiles.",
            "support.subscription": "Suscripcion",
            "support.subscriptionText": "Cambio de plan, prueba gratuita, pago simulado o preparacion Stripe/PayPal.",
            "support.contact": "Contactar soporte",
            "support.email": "Tu email",
            "support.playbackOption": "Reproduccion de video",
            "support.paymentOption": "Pago",
            "support.accountOption": "Cuenta",
            "support.catalogOption": "Catalogo",
            "support.problem": "Explica el problema",
            "support.send": "Enviar solicitud",
            "succhero.subtitle": "Asistente PrimeStream",
            "succhero.placeholder": "Mensaje SUCCHERO",
            "toast.themeSaved": "Tema aplicado. El catalogo muestra peliculas relacionadas."
        }
    };

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    const headers = { "Content-Type": "application/json" };
    if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

    const storedLanguage = localStorage.getItem("primestream-language") || "fr";
    const languageSelect = document.querySelector("[data-language-select]");
    if (languageSelect) {
        languageSelect.value = storedLanguage;
        languageSelect.addEventListener("change", () => {
            localStorage.setItem("primestream-language", languageSelect.value);
            applyTranslations(languageSelect.value);
        });
    }
    applyTranslations(storedLanguage);

    const moodSelect = document.querySelector("[data-mood-select]");
    if (moodSelect) {
        const currentMood = body?.dataset.mood || localStorage.getItem("primestream-mood") || "classic";
        moodSelect.value = currentMood;
        filterCardsByMood(currentMood);
        moodSelect.addEventListener("change", async () => {
            const moodMode = moodSelect.value;
            const genre = moodGenres[moodMode] || "";
            localStorage.setItem("primestream-mood", moodMode);
            if (body) body.dataset.mood = moodMode;
            filterCardsByMood(moodMode);
            const response = await fetch("/api/stream/preferences", {
                method: "POST",
                headers,
                body: JSON.stringify({
                    moodMode,
                    preferredGenre: genre,
                    preferredLanguage: languageName(localStorage.getItem("primestream-language") || "fr"),
                    preferredQuality: "Auto",
                    maturityLevel: "18+"
                })
            });
            if (response.ok) {
                const payload = await response.json();
                showMiniToast(t("toast.themeSaved"));
                setTimeout(() => {
                    window.location.href = payload.browseUrl || (genre ? `/browse?genre=${encodeURIComponent(genre)}` : "/browse");
                }, 450);
            }
        });        document.addEventListener("click", async (ev) => {
            const btn = ev.target.closest("[data-plan-btn], [data-pay-subscription]");
            if (!btn) return;
            ev.preventDefault();
            const planSelect = document.querySelector("[data-plan-select]");
            const selectedPlan = document.querySelector("[data-selected-plan]");
            const resultBox = document.querySelector("[data-payment-result]");
            const plan = btn.dataset.plan || selectedPlan?.value || planSelect?.value || "Standard";
            if (planSelect) planSelect.value = plan;
            if (selectedPlan) selectedPlan.value = plan;
            if (!plan || plan === "Gratuit") {
                const form = btn.closest("form");
                if (form) form.submit();
                return;
            }

            const name = document.querySelector("[data-card-name]")?.value || "";
            const card = document.querySelector("[data-card-number]")?.value || "";
            const exp = document.querySelector("[data-card-exp]")?.value || "";
            const cvv = document.querySelector("[data-card-cvv]")?.value || "";
            const postal = document.querySelector('input[placeholder="Code postal"]')?.value || "";

            try {
                btn.disabled = true;
                btn.dataset.orig = btn.textContent;
                btn.textContent = "Verification...";
                if (resultBox) resultBox.textContent = "Connexion a la banque sandbox...";
                const resp = await fetch("/api/payment/verify", {
                    method: "POST",
                    headers,
                    body: JSON.stringify({ plan, cardNumber: card, name, exp, cvv, postal })
                });
                const data = await resp.json();
                if (resp.ok && data.status === "ok") {
                    if (resultBox) resultBox.textContent = `Paiement accepte. Transaction ${data.transactionId}. Activation du plan ${plan}...`;
                    setTimeout(() => window.location.href = data.redirect || "/profile?subscribed=true", 650);
                } else {
                    if (resultBox) resultBox.textContent = data.message || data.error || "Paiement echoue";
                    alert(data.message || data.error || "Paiement echoue");
                    btn.disabled = false;
                    btn.textContent = btn.dataset.orig || "Payer";
                }
            } catch (e) {
                if (resultBox) resultBox.textContent = "Erreur de communication avec le serveur";
                alert("Erreur de communication avec le serveur");
                btn.disabled = false;
                btn.textContent = btn.dataset.orig || "Payer";
            }
        });
        document.querySelector("[data-plan-select]")?.addEventListener("change", (event) => {
            const selectedPlan = document.querySelector("[data-selected-plan]");
            if (selectedPlan) selectedPlan.value = event.target.value;
        });
    }

    document.querySelectorAll("[data-side-menu-toggle]").forEach((button) => {
        button.addEventListener("click", () => body.classList.toggle("side-open"));
    });

    document.querySelectorAll("[data-watchlist]").forEach((button) => {
        button.addEventListener("click", async () => {
            const id = button.dataset.watchlist;
            const response = await fetch(`/api/stream/watchlist/${id}`, { method: "POST", headers });
            if (!response.ok) return;
            const payload = await response.json();
            button.textContent = payload.added ? "Retirer de ma liste" : "Ajouter a ma liste";
        });
    });

    document.querySelectorAll("[data-succhero-widget]").forEach((widget) => {
        const thread = widget.querySelector("[data-succhero-thread]");
        thread?.scrollTo(0, thread.scrollHeight);
        widget.querySelectorAll("[data-succhero-toggle]").forEach((button) => {
            button.addEventListener("click", () => {
                widget.classList.toggle("is-open");
                widget.querySelector('input[name="message"]')?.focus();
                thread?.scrollTo(0, thread.scrollHeight);
            });
        });
    });

    document.querySelectorAll("[data-succhero-chat]").forEach((form) => {
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const input = form.querySelector('input[name="message"]');
            const widget = form.closest("[data-succhero-widget]");
            const thread = widget?.querySelector("[data-succhero-thread]") || form.querySelector("[data-succhero-thread]");
            const message = input?.value?.trim();
            if (!message) return;
            appendSuccheroMessage(thread, message, "from-user");
            input.value = "";
            const waiting = appendSuccheroMessage(thread, "SUCCHERO ecrit...", "from-assistant is-typing");
            try {
                const response = await fetch("/api/succhero/chat", {
                    method: "POST",
                    headers,
                    body: JSON.stringify({ message })
                });
                const payload = response.ok ? await response.json() : { reply: "Je n'arrive pas a joindre PrimeStream pour le moment." };
                waiting.remove();
                appendSuccheroMessage(thread, payload.reply, "from-assistant");
            } catch (error) {
                waiting.remove();
                appendSuccheroMessage(thread, "Connexion indisponible. Verifiez que le serveur PrimeStream tourne.", "from-assistant");
            }
        });
    });

    document.querySelectorAll("[data-succhero-toast]").forEach((toast) => {
        requestAnimationFrame(() => toast.classList.add("is-visible"));
        setTimeout(() => toast.classList.remove("is-visible"), 9000);
    });

    document.querySelectorAll(".prime-player").forEach((video) => {
        const mediaId = Number(video.dataset.mediaId);
        const start = Number(video.dataset.start || "0");
        let restored = false;
        let lastSave = 0;

        video.addEventListener("loadedmetadata", () => {
            if (!restored && start > 0 && start < video.duration - 5) {
                video.currentTime = start;
                restored = true;
            }
        });

        video.addEventListener("timeupdate", () => {
            const now = Date.now();
            if (now - lastSave < 7000 || !video.duration) return;
            lastSave = now;
            const seconds = Math.floor(video.currentTime);
            const percent = Math.min(100, Math.round((video.currentTime / video.duration) * 100));
            fetch("/api/stream/progress", {
                method: "POST",
                headers,
                body: JSON.stringify({ mediaId, seconds, percent })
            }).catch(() => {});
        });

        video.addEventListener("error", () => {
            const box = document.createElement("div");
            box.className = "video-error";
            box.textContent = "La video ne peut pas etre lue dans ce navigateur. Utilisez Telecharger ou choisissez une video MP4.";
            video.insertAdjacentElement("afterend", box);
        }, { once: true });
    });

    function appendSuccheroMessage(thread, text, className) {
        if (!thread) return document.createElement("p");
        const line = document.createElement("p");
        line.className = className;
        line.textContent = text;
        thread.appendChild(line);
        thread.scrollTop = thread.scrollHeight;
        return line;
    }

    function filterCardsByMood(moodMode) {
        const genre = moodGenres[moodMode] || "";
        document.querySelectorAll("[data-genres]").forEach((card) => {
            card.hidden = Boolean(genre) && !card.dataset.genres.toLowerCase().includes(genre.toLowerCase());
        });
    }

    function applyTranslations(language) {
        root.lang = language;
        const dictionary = translations[language] || translations.fr;
        document.querySelectorAll("[data-i18n]").forEach((element) => {
            const value = dictionary[element.dataset.i18n];
            if (value) element.textContent = value;
        });
        document.querySelectorAll("[data-i18n-placeholder]").forEach((element) => {
            const value = dictionary[element.dataset.i18nPlaceholder];
            if (value) element.setAttribute("placeholder", value);
        });
    }

    function t(key) {
        const language = localStorage.getItem("primestream-language") || "fr";
        return translations[language]?.[key] || translations.fr[key] || key;
    }

    function languageName(code) {
        if (code === "en") return "Anglais";
        if (code === "es") return "Espagnol";
        return "Francais";
    }

    function showMiniToast(text) {
        const toast = document.createElement("div");
        toast.className = "mini-toast";
        toast.textContent = text;
        document.body.appendChild(toast);
        requestAnimationFrame(() => toast.classList.add("is-visible"));
        setTimeout(() => {
            toast.classList.remove("is-visible");
            setTimeout(() => toast.remove(), 220);
        }, 2800);
    }
})();

