# TODO - Auth Admin/User (Spring Boot + Thymeleaf)

## Étape 1 — Préparation
- [ ] Créer une base de projet Spring Boot (Thymeleaf + Spring Security)
- [ ] Vérifier le port (≠ 8080)

## Étape 2 — Données & rôles
- [ ] Créer les entités User (username/email, password hash, rôle)
- [ ] Ajouter rôles: ADMIN et USER
- [ ] Mettre en place le stockage (H2 en dev)

## Étape 3 — Sécurité
- [ ] Configurer Spring Security (CSRF activé)
- [ ] Mettre en place Login form Thymeleaf avec messages d’erreur
- [ ] Mettre en place Register (optionnel selon besoin)
- [ ] Protéger endpoints admin
- [ ] Rediriger: admin -> admin home, user -> user home

## Étape 4 — Password oublié
- [ ] Implémenter un flux “mot de passe oublié” (token + page reset)
- [ ] Ajouter pages Thymeleaf correspondantes
- [ ] Expliquer comment tester (email simulé si nécessaire)

## Étape 5 — Logging
- [ ] Logger les actions (login ok/ko, register, reset password, admin actions)
- [ ] Écrire dans un fichier log (ex: logs/app.log)

## Étape 6 — Vue / UI
- [ ] Créer pages Thymeleaf: login, register, forgot, reset, landing, admin, user
- [ ] Ajouter un design “joli” (CSS)

## Étape 7 — Tests & run
- [ ] Lancer l’application
- [ ] Tester scénarios: mauvais mot de passe, rôles, accès admin, CSRF
- [ ] Vérifier que le port n’est pas 8080

