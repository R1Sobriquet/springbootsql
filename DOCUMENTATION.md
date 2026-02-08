# Projet Spring Boot - Gestion de Cave à Vins

## Description du Projet

Application web de gestion d'une cave à vins développée avec **Spring Boot** et **MySQL**. Elle permet de gérer un catalogue de vins, des utilisateurs et des commandes via une API REST et une interface web moderne.

---

## Technologies Utilisées

| Technologie | Version | Description |
|-------------|---------|-------------|
| Java | 17 | Langage de programmation |
| Spring Boot | 3.2.0 | Framework backend |
| Spring Data JPA | 3.2.0 | ORM et accès aux données |
| MySQL | 8.x | Base de données relationnelle |
| Docker Compose | - | Conteneurisation de MySQL |
| H2 Database | - | Base de données pour les tests |
| HTML/CSS/JS | - | Interface utilisateur |

---

## Architecture du Projet

```
springbootsql/
├── src/
│   ├── main/
│   │   ├── java/com/example/accessingdatamysql/
│   │   │   ├── AccessingDataMysqlApplication.java  # Point d'entrée
│   │   │   │
│   │   │   ├── # Entités (Modèles)
│   │   │   ├── User.java                 # Utilisateur
│   │   │   ├── Produit.java              # Vin/Produit
│   │   │   ├── Commande.java             # Commande
│   │   │   ├── LigneCommande.java        # Ligne de commande
│   │   │   ├── StatutCommande.java       # Enum des statuts
│   │   │   │
│   │   │   ├── # Repositories (Accès données)
│   │   │   ├── UserRepository.java
│   │   │   ├── ProduitRepository.java
│   │   │   ├── CommandeRepository.java
│   │   │   ├── LigneCommandeRepository.java
│   │   │   │
│   │   │   ├── # Controllers (API REST)
│   │   │   ├── MainController.java       # /demo/*
│   │   │   ├── ProduitController.java    # /catalogue/*
│   │   │   ├── CommandeController.java   # /api/commandes/*
│   │   │   │
│   │   │   └── dto/                      # Data Transfer Objects
│   │   │       ├── CommandeRequest.java
│   │   │       ├── CommandeResponse.java
│   │   │       └── LigneCommandeRequest.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties    # Configuration
│   │       ├── compose.yaml              # Docker MySQL
│   │       └── static/                   # Interface web
│   │           ├── index.html
│   │           ├── css/style.css
│   │           └── js/app.js
│   │
│   └── test/
│       ├── java/com/example/accessingdatamysql/
│       │   ├── UserRepositoryTest.java
│       │   ├── ProduitRepositoryTest.java
│       │   └── CommandeRepositoryTest.java
│       └── resources/
│           └── application.properties    # Config tests (H2)
│
├── pom.xml                               # Dépendances Maven
└── compose.yaml                          # Docker Compose
```

---

## Modèle de Données

### Diagramme Entité-Relation

```
┌─────────────┐       ┌─────────────────┐       ┌─────────────┐
│    User     │       │    Commande     │       │   Produit   │
├─────────────┤       ├─────────────────┤       ├─────────────┤
│ id (PK)     │──────<│ id (PK)         │       │ id (PK)     │
│ name        │  1:N  │ user_id (FK)    │       │ nom (unique)│
│ email       │       │ dateCommande    │       │ annee       │
└─────────────┘       │ statut          │       │ region      │
                      └────────┬────────┘       │ prix        │
                               │                │ stock       │
                               │ 1:N            │ notes[]     │
                               ▼                └──────┬──────┘
                      ┌─────────────────┐              │
                      │  LigneCommande  │              │
                      ├─────────────────┤              │
                      │ id (PK)         │              │
                      │ commande_id (FK)│              │
                      │ produit_id (FK) │──────────────┘
                      │ quantite        │         N:1
                      │ prixUnitaire    │
                      └─────────────────┘
```

### Description des Entités

#### User (Utilisateur)
| Champ | Type | Description |
|-------|------|-------------|
| id | Integer | Identifiant unique (auto-généré) |
| name | String | Nom de l'utilisateur |
| email | String | Adresse email |

#### Produit (Vin)
| Champ | Type | Description |
|-------|------|-------------|
| id | Integer | Identifiant unique |
| nom | String | Nom du vin (unique) |
| annee | Integer | Année de production |
| region | String | Région d'origine |
| prix | Double | Prix unitaire |
| stock | Integer | Quantité en stock |
| notes | List<String> | Notes de dégustation |

#### Commande
| Champ | Type | Description |
|-------|------|-------------|
| id | Integer | Identifiant unique |
| user | User | Client (relation ManyToOne) |
| dateCommande | LocalDateTime | Date de création |
| statut | StatutCommande | État de la commande |
| lignes | List<LigneCommande> | Articles commandés |

#### LigneCommande
| Champ | Type | Description |
|-------|------|-------------|
| id | Integer | Identifiant unique |
| commande | Commande | Commande parente |
| produit | Produit | Vin commandé |
| quantite | Integer | Quantité |
| prixUnitaire | Double | Prix au moment de la commande |

#### StatutCommande (Enum)
| Valeur | Label | Couleur |
|--------|-------|---------|
| EN_ATTENTE | En attente | Orange (#FFA500) |
| VALIDEE | Validée | Bleu (#3B82F6) |
| EN_PREPARATION | En préparation | Violet (#8B5CF6) |
| EXPEDIEE | Expédiée | Cyan (#06B6D4) |
| LIVREE | Livrée | Vert (#10B981) |
| ANNULEE | Annulée | Rouge (#EF4444) |

---

## API REST

### Utilisateurs (/demo)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/demo/add` | Ajouter un utilisateur |
| GET | `/demo/all` | Lister tous les utilisateurs |

**Exemple - Ajouter un utilisateur:**
```bash
curl -X POST "http://localhost:8080/demo/add" -d "name=Jean" -d "email=jean@example.com"
```

### Produits (/catalogue)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/catalogue/produits` | Lister tous les produits |
| GET | `/catalogue/produits/{nom}` | Récupérer un produit par nom |
| POST | `/catalogue/produits` | Ajouter un produit |
| PUT | `/catalogue/produits/{nom}` | Modifier un produit |
| DELETE | `/catalogue/produits/{nom}` | Supprimer un produit |

**Exemple - Ajouter un vin:**
```json
POST /catalogue/produits
{
    "nom": "Côte du Rhône",
    "annee": 2019,
    "region": "Rhône",
    "prix": 10.0,
    "stock": 40,
    "notes": ["sec", "épicé"]
}
```

### Commandes (/api/commandes)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/commandes` | Lister toutes les commandes |
| GET | `/api/commandes/{id}` | Détails d'une commande |
| GET | `/api/commandes/user/{userId}` | Commandes d'un utilisateur |
| GET | `/api/commandes/statut/{statut}` | Filtrer par statut |
| POST | `/api/commandes` | Créer une commande |
| PUT | `/api/commandes/{id}/statut` | Modifier le statut |
| DELETE | `/api/commandes/{id}` | Supprimer une commande |
| GET | `/api/commandes/statuts` | Liste des statuts |

**Exemple - Créer une commande:**
```json
POST /api/commandes
{
    "userId": 1,
    "lignes": [
        {"nomProduit": "Côte du Rhône", "quantite": 2},
        {"nomProduit": "Bordeaux", "quantite": 1}
    ]
}
```

**Exemple - Réponse commande:**
```json
{
    "id": 1,
    "user": {
        "id": 1,
        "name": "Jean",
        "email": "jean@example.com"
    },
    "dateCommande": "2024-01-30T15:30:00",
    "dateFormatted": "30/01/2024 15:30",
    "statut": {
        "code": "EN_ATTENTE",
        "label": "En attente",
        "color": "#FFA500"
    },
    "lignes": [...],
    "total": 45.00,
    "nombreArticles": 3
}
```

---

## Interface Utilisateur

### Pages Disponibles

1. **Dashboard** - Vue d'ensemble avec statistiques
2. **Produits** - Catalogue de vins avec ajout/modification/suppression
3. **Utilisateurs** - Gestion des clients
4. **Commandes** - Suivi et gestion des commandes

### Fonctionnalités UI

- Design moderne avec gradients (thème vin bordeaux)
- Cartes produits avec informations détaillées
- Badges colorés pour les statuts de commande
- Modales pour les formulaires
- Notifications toast
- Filtrage des commandes par statut
- Responsive design

### Captures d'écran

L'interface est accessible à: `http://localhost:8080`

---

## Tests Unitaires

### Tests Implémentés

| Classe de Test | Nombre de Tests | Description |
|----------------|-----------------|-------------|
| UserRepositoryTest | 8 | CRUD utilisateurs |
| ProduitRepositoryTest | 8 | CRUD produits |
| CommandeRepositoryTest | 8 | CRUD commandes |

### Exécution des Tests

```bash
mvn test
```

### Technologies de Test

- **JUnit 5** - Framework de test
- **Spring Boot Test** - Tests d'intégration Spring
- **H2 Database** - Base de données en mémoire pour les tests
- **AssertJ** - Assertions fluentes

---

## Installation et Exécution

### Prérequis

- Java 17+
- Maven 3.8+
- Docker Desktop (pour MySQL)

### Étapes

1. **Cloner le projet**
   ```bash
   git clone https://github.com/R1Sobriquet/springbootsql/edit/Main
   cd springbootsql
   ```

2. **Démarrer MySQL avec Docker**
   ```bash
   docker compose up -d
   ```

3. **Lancer l'application**
   ```bash
   mvn spring-boot:run
   ```

4. **Accéder à l'interface**
   - Ouvrir: http://localhost:8080

### Configuration

Fichier `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=myuser
spring.datasource.password=secret
```

---

## Gestion du Stock

Le système gère automatiquement le stock:

- **Création de commande**: Le stock est décrémenté
- **Annulation de commande**: Le stock est restauré
- **Suppression de commande**: Le stock est restauré
- **Vérification**: Impossible de commander plus que le stock disponible

---

## Sécurité (Recommandations)

Pour une mise en production, il faudrait ajouter:

1. **Spring Security** - Authentification et autorisation
2. **Validation des entrées** - @Valid, @NotNull, etc.
3. **HTTPS** - Chiffrement des communications
4. **CORS** - Configuration des origines autorisées
5. **Limiter les privilèges MySQL** - Comme recommandé dans le guide Spring

---

## Auteur

Projet réalisé dans le cadre d'un cours sur Spring Boot et MySQL.

## Références

- [Guide officiel Spring - Accessing Data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
- [Documentation Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Documentation Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
