# Projet Agile - Docker Architecture Java Spring Boot

Ce projet est une **architecture Docker pour un service Java Spring Boot avec PostgreSQL**, organisée pour **développement (dev) et production (prod)** en utilisant les **profils Docker Compose**.

---

## Prérequis

- Docker >= 24
- Docker Compose >= 2.0
- Java JDK 21 (pour le build local si nécessaire)
- Gradle

---

## Structure du projet
├── Dockerfile # Dockerfile pour la production
├── Dockerfile.dev # Dockerfile pour le développement
├── docker-compose.yml # Configuration Docker Compose avec profils dev/prod
├── gradle/ # Wrapper Gradle
├── src/ # Code source Java
│ ├── main/
│ └── test/
├── build.gradle # Build script Gradle
└── README.md


---

## Services Docker

### 1. Développement (profil `dev`)

- **API** : `api-dev`
  - Container name : `authentification-dev`
  - Dockerfile : `Dockerfile.dev`
  - Port : 8080
  - Connecté à la base dev `auth-dev-db`

- **Database** : `auth-dev-db`
  - Container name : `auth_dev_db`
  - Image : PostgreSQL 18
  - Port : 5433
  - Volume : `pgdata-dev`
  - Database : `auth_db`

**Lancer les services dev :**
docker-compose --profile dev up --build

**Lancer les services prod :**
docker-compose --profile prod up --build
