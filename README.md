# PrimeStream - Plateforme de streaming avec Spring Boot

PrimeStream est une application web complete inspiree d'Amazon Prime Video et de Netflix. Elle propose une experience de streaming moderne avec films, series, abonnements, boutique, panier, paiement simule, factures, emails, assistant intelligent et personnalisation de l'interface selon les preferences de l'utilisateur.

Le projet est developpe avec Java, Spring Boot, Spring Security, Thymeleaf, MySQL/WAMP et JavaScript. Il peut fonctionner en local, sur un reseau local entre plusieurs machines, puis etre prepare pour un hebergement reel.

## Objectif du projet

L'objectif de PrimeStream est de simuler une vraie plateforme de streaming professionnelle.

L'application permet a un utilisateur de :

- creer un compte et se connecter ;
- regarder des films et series ;
- choisir un abonnement ;
- simuler un paiement ;
- utiliser une boutique integree ;
- ajouter des produits au panier ;
- generer une facture ;
- recevoir des emails ;
- discuter avec l'assistant SUCCHERO ;
- changer le theme de l'application ;
- filtrer les contenus selon ses preferences ;
- acceder a l'application depuis un autre appareil du meme reseau.

Ce projet met en pratique les notions importantes de Spring Boot : MVC, JPA, securite, base de donnees, templates Thymeleaf, API REST, taches planifiees, emails et architecture web complete.

## Technologies utilisees

### Backend

- Java
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- JWT pour les API REST

### Frontend

- Thymeleaf
- HTML5
- CSS3
- JavaScript
- Interface responsive
- Lecteur video HTML5

### Base de donnees

- MySQL
- WAMP
- phpMyAdmin

### Outils de test et demonstration

- Postman pour tester les API REST
- Navigateur web
- phpMyAdmin pour consulter la base
- Maven pour compiler et lancer le projet

## Fonctionnalites principales

### 1. Authentification et securite

PrimeStream integre Spring Security pour proteger l'application.

Fonctionnalites :

- inscription utilisateur ;
- connexion par formulaire ;
- deconnexion ;
- gestion des sessions ;
- mots de passe encodes ;
- protection CSRF sur les formulaires Thymeleaf ;
- protection des pages sensibles ;
- separation des roles utilisateur, vendeur et administrateur ;
- API REST protegee par JWT.

Comptes de demonstration :

```text
Utilisateur : user / User@123
Admin       : admin / Admin@123
Vendeur     : seller / Seller@123
```

### 2. Catalogue de streaming

L'application propose un catalogue de films et series.

Fonctionnalites :

- page d'accueil PrimeStream ;
- affichage des contenus populaires ;
- affichage des nouveautes ;
- catalogue de films et series ;
- recherche de contenu ;
- details d'un contenu ;
- lecture video ;
- telechargement de certains fichiers video ;
- affichage des acteurs ;
- recommandations ;
- historique de visionnage ;
- liste "A regarder plus tard".

Les videos de demonstration sont chargees depuis :

```text
src/main/resources/static/videos
```

### 3. Lecteur video

PrimeStream utilise un lecteur HTML5 pour lire les videos disponibles dans le projet.

Fonctionnalites :

- lecture de videos locales ;
- bouton de telechargement ;
- affichage des informations du film ;
- progression de lecture ;
- acces aux details du contenu ;
- integration avec la liste de visionnage.

### 4. Themes personnalises

L'utilisateur peut changer le theme de l'application depuis un menu deroulant.

Exemples de themes :

- Classic ;
- Romance ;
- Action ;
- Famille ;
- Science-fiction ;
- Afrique.

Quand un theme est choisi :

- les couleurs de l'interface changent ;
- les films affiches sont filtres selon le theme ;
- l'experience devient plus personnalisee.

Exemple : si l'utilisateur choisit le theme Romance, l'application affiche principalement les films de type romance et adapte les couleurs de l'interface.

### 5. Assistant intelligent SUCCHERO

SUCCHERO est l'assistant integre de PrimeStream.

Il est disponible sous forme de petit bouton flottant. Quand l'utilisateur clique dessus, une fenetre de chat s'ouvre.

SUCCHERO peut aider l'utilisateur a :

- comprendre le fonctionnement de PrimeStream ;
- trouver un film ou une serie ;
- comprendre les abonnements ;
- obtenir de l'aide sur la boutique ;
- comprendre le paiement ;
- utiliser la liste "A regarder plus tard" ;
- recevoir des recommandations ;
- comprendre les notifications ;
- poser des questions sur l'application.

SUCCHERO est limite au domaine de PrimeStream. Si l'utilisateur pose une question qui ne concerne pas l'application, il recentre la conversation sur PrimeStream.

Les conversations peuvent etre conservees en base de donnees.

### 6. Notifications intelligentes

L'application possede un systeme de notifications.

Exemples de notifications :

- un film recherche est maintenant disponible ;
- une nouvelle saison est ajoutee ;
- un abonnement arrive bientot a expiration ;
- un film populaire est disponible ;
- une recommandation est proposee.

Les notifications apparaissent sous forme de petits blocs modernes avec des actions possibles, comme :

- regarder maintenant ;
- ajouter a regarder plus tard ;
- consulter les details.

### 7. Abonnements

PrimeStream propose plusieurs plans d'abonnement.

Plans disponibles :

```text
Gratuit  : 0 EUR
Standard : 9,99 EUR/mois
Premium  : 14,99 EUR/mois
```

Chaque abonnement peut donner acces a des avantages differents :

- qualite video ;
- nombre d'ecrans ;
- publicites ou non ;
- contenus exclusifs ;
- acces premium.

### 8. Paiement simule

La page d'abonnement permet de simuler un paiement par carte bancaire.

Champs demandes :

- plan choisi ;
- nom sur la carte ;
- numero de carte ;
- date d'expiration ;
- CVV ;
- code postal.

Carte de test acceptee :

```text
Numero     : 4242 4242 4242 4242
Expiration : 12/30
CVV        : 123
Code postal: 00000
```

Carte de test refusee :

```text
4000 0000 0000 0002
```

Quand le paiement est accepte, l'application genere une transaction simulee et active l'abonnement choisi.

### 9. Boutique integree

PrimeStream contient une boutique permettant d'acheter des produits.

Fonctionnalites :

- liste des produits ;
- detail d'un produit ;
- images de produits ;
- ajout au panier ;
- mise a jour du compteur de panier ;
- checkout ;
- commande ;
- facture.

La boutique permet de simuler une experience proche d'un site e-commerce integre a une plateforme de streaming.

### 10. Panier

Le panier permet de :

- ajouter un produit ;
- modifier les quantites ;
- consulter le total ;
- passer a la commande ;
- finaliser l'achat.

Le compteur du panier se met a jour automatiquement apres ajout d'un produit.

### 11. Facturation

Apres une commande, une facture est generee automatiquement.

La facture contient :

- numero de facture ;
- date ;
- client ;
- adresse ;
- produits commandes ;
- quantite ;
- prix unitaire ;
- total ;
- moyen de paiement ;
- reference de transaction.

La facture peut etre imprimee ou enregistree en PDF depuis le navigateur.

### 12. Emails

PrimeStream integre un service d'envoi d'emails.

Types d'emails prevus :

- confirmation d'inscription ;
- rappel d'abonnement ;
- nouvelle saison disponible ;
- nouveau film disponible ;
- recommandations personnalisees ;
- annonces importantes.

Les emails sont mis en page avec un style moderne inspire de plateformes comme Netflix.

Par defaut, si les emails reels ne sont pas actives, ils sont journalises dans :

```text
logs/keyce.log
```

Pour envoyer de vrais emails, il faut configurer SMTP.

### 13. Taches planifiees

L'application peut executer des taches automatiques.

Exemples :

- verifier les abonnements qui expirent ;
- envoyer des rappels par email ;
- annoncer une nouvelle saison ;
- informer les utilisateurs d'un nouveau film ;
- verifier les demandes de disponibilite faites via SUCCHERO.

### 14. Administration

Un espace administrateur permet de gerer le contenu.

Fonctionnalites :

- gestion des contenus streaming ;
- ajout de films ou series ;
- gestion des images ;
- gestion des videos ;
- envoi d'emails de demonstration ;
- consultation de certaines donnees ;
- administration des contenus disponibles.

Route principale :

```text
/admin/stream
```

### 15. API REST et Postman

PrimeStream expose une API REST pour Postman.

La specification OpenAPI est disponible quand l'application tourne :

```text
http://localhost:8091/openapi.yaml
```

Etapes dans Postman :

1. Importer l'URL `http://localhost:8091/openapi.yaml`.
2. Appeler la route de connexion REST.
3. Recuperer le token JWT.
4. Utiliser le token en Bearer Token.
5. Tester les routes protegees.

Exemple de connexion REST :

```json
{
  "username": "user",
  "password": "User@123"
}
```

Les pages Thymeleaf utilisent la securite par session classique. Les routes REST utilisent JWT.

## Base de donnees

Le projet utilise MySQL avec WAMP.

Configuration par defaut :

```text
Base       : prime_stream
Host       : 127.0.0.1
Port       : 3306
Utilisateur: root
Mot de passe: vide
```

URL JDBC :

```text
jdbc:mysql://127.0.0.1:3306/prime_stream
```

Avant de lancer l'application, il faut demarrer WAMP et verifier que MySQL est actif.

La base peut etre consultee dans phpMyAdmin :

```text
http://localhost/phpmyadmin
```

## Lancement du projet

### 1. Demarrer WAMP

Ouvrir WAMP et verifier que MySQL est actif.

### 2. Ouvrir le terminal dans le projet

```powershell
cd "C:\Users\auror\OneDrive\Desktop\B2\SEMESTRE 2 (V)\JAVA et Spring Boot\petit projet\petit projet"
```

### 3. Compiler le projet

```powershell
mvn -DskipTests clean package
```

### 4. Lancer le serveur

```powershell
java -jar target\amazon-prime-2-0-1.0-SNAPSHOT.jar
```

### 5. Ouvrir l'application

```text
http://localhost:8091
```

## Lancement avec Maven

Pendant le developpement, il est aussi possible de lancer :

```powershell
mvn spring-boot:run
```

## Acces depuis un autre ordinateur

L'application peut etre utilisee en reseau local.

Le PC qui lance Spring Boot devient le serveur. Les autres appareils connectes au meme Wi-Fi peuvent acceder a l'application avec l'adresse IP du serveur.

Etapes :

1. Lancer WAMP sur le PC serveur.
2. Lancer PrimeStream sur le PC serveur.
3. Trouver l'adresse IP du serveur :

```powershell
ipconfig
```

4. Chercher l'adresse IPv4, par exemple :

```text
192.168.1.25
```

5. Depuis un autre PC ou telephone du meme reseau, ouvrir :

```text
http://192.168.1.25:8091
```

Si l'application ne s'ouvre pas depuis l'autre appareil, il faut autoriser le port `8091` dans le pare-feu Windows.

## Hebergement reel

Pour heberger PrimeStream sur un vrai serveur :

1. Construire le projet :

```powershell
mvn -DskipTests clean package
```

2. Copier le fichier `.jar` sur le serveur.
3. Configurer une base MySQL distante.
4. Configurer les variables d'environnement.
5. Lancer l'application avec Java.
6. Placer l'application derriere un proxy HTTPS comme Nginx ou Apache.
7. Associer un nom de domaine.

Exemple de variables :

```powershell
set DB_URL=jdbc:mysql://HOST:3306/prime_stream?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
set DB_USERNAME=prime_user
set DB_PASSWORD=mot_de_passe_fort
set APP_PUBLIC_BASE_URL=https://ton-domaine.com
set APP_MAIL_ENABLED=true
set APP_MAIL_FROM=ton-adresse@gmail.com
set SMTP_HOST=smtp.gmail.com
set SMTP_PORT=587
set SMTP_USERNAME=ton-adresse@gmail.com
set SMTP_PASSWORD=mot-de-passe-application
set SMTP_AUTH=true
set SMTP_STARTTLS=true
java -jar target\amazon-prime-2-0-1.0-SNAPSHOT.jar
```

## Configuration email

Pour envoyer des emails reels avec Gmail, il faut utiliser un mot de passe d'application Gmail.

Exemple :

```powershell
set APP_MAIL_ENABLED=true
set APP_MAIL_FROM=ton-adresse@gmail.com
set SMTP_HOST=smtp.gmail.com
set SMTP_PORT=587
set SMTP_USERNAME=ton-adresse@gmail.com
set SMTP_PASSWORD=mot-de-passe-application
set SMTP_AUTH=true
set SMTP_STARTTLS=true
mvn spring-boot:run
```

Sans configuration SMTP valide, les emails ne peuvent pas partir reellement.

## Routes principales

```text
/                         Accueil
/login                    Connexion
/register                 Inscription
/browse                   Catalogue streaming
/watch/{id}               Lecture video
/profile                  Profil utilisateur
/subscription             Abonnements et paiement
/products                 Boutique
/products/{id}            Detail produit
/cart                     Panier
/checkout                 Validation commande
/orders/{id}/invoice      Facture
/admin/stream             Administration streaming
/openapi.yaml             Specification API REST
/api/auth/login           Connexion API REST
/api/rest/**              API REST protegee par JWT
```

## Structure du projet

```text
src/main/java/com/example/authapp/model       Entites JPA
src/main/java/com/example/authapp/repo        Repositories Spring Data
src/main/java/com/example/authapp/service     Services metier
src/main/java/com/example/authapp/web         Controleurs MVC et REST
src/main/resources/templates                  Templates Thymeleaf
src/main/resources/static/css                 Feuilles de style
src/main/resources/static/js                  Scripts JavaScript
src/main/resources/static/videos              Videos locales
src/main/resources/static/images              Images statiques
uploads                                       Fichiers ajoutes par admin
logs                                          Journaux de l'application
```

## Demonstration rapide

1. Lancer WAMP.
2. Lancer l'application.
3. Aller sur `http://localhost:8091`.
4. Se connecter avec `user / User@123`.
5. Tester le catalogue.
6. Changer le theme.
7. Ouvrir SUCCHERO.
8. Aller sur `/subscription`.
9. Choisir Premium.
10. Utiliser la carte `4242 4242 4242 4242`.
11. Aller dans la boutique.
12. Ajouter un produit au panier.
13. Finaliser la commande.
14. Ouvrir la facture.
15. Tester l'acces depuis un autre appareil du meme reseau.

## Points forts du projet

- Application complete et realiste.
- Interface web responsive.
- Securite avec Spring Security.
- Base de donnees MySQL.
- Assistant intelligent integre.
- Paiement simule.
- Facturation.
- Boutique e-commerce.
- Emails HTML.
- Taches planifiees.
- API REST testable avec Postman.
- Utilisation possible en reseau local.
- Preparation possible pour un hebergement reel.

## Conclusion

PrimeStream est une application web complete qui rassemble les fonctionnalites essentielles d'une plateforme de streaming moderne. Le projet combine streaming, boutique, abonnement, securite, base de donnees, API REST, emails, assistant intelligent et personnalisation utilisateur.

Il peut etre utilise pour une demonstration academique, un projet de fin de semestre ou comme base pour une application plus avancee.
