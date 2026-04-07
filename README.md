# Tabila - QR Restaurant Ordering System

Monorepo pour une solution de commande restaurant via QR code.

Le projet contient:

- un backend Spring Boot (API + auth + logique metier)
- un frontend client (application menu/commande)
- un backoffice admin/super-admin (gestion restaurant)

## Sommaire

- [Tabila - QR Restaurant Ordering System](#tabila---qr-restaurant-ordering-system)
  - [Sommaire](#sommaire)
  - [Architecture](#architecture)
  - [Stack technique](#stack-technique)
  - [Prerequis](#prerequis)
  - [Demarrage rapide](#demarrage-rapide)
    - [Option A - demarrage complet via Makefile](#option-a---demarrage-complet-via-makefile)
    - [Option B - commandes manuelles](#option-b---commandes-manuelles)
    - [Postgres local (optionnel)](#postgres-local-optionnel)
  - [Comptes seed (dev)](#comptes-seed-dev)
  - [Configuration backend](#configuration-backend)
  - [Scripts utiles](#scripts-utiles)
  - [Tests et build](#tests-et-build)
  - [API et endpoints](#api-et-endpoints)

## Architecture

Structure principale:

- `backend/` : API Spring Boot (port 8080)
- `frontend/` : app client Vue (port 5173)
- `backoffice/` : app admin Vue (port 5174)
- `docker-compose.yml` : Postgres local optionnel
- `Makefile` : commandes de demarrage simplifiees
- `requirements.md` : specification fonctionnelle MVP

## Stack technique

Backend:

- Java 21
- Spring Boot 3.3.5
- Spring Security + JWT
- Spring Data JPA (H2 en defaut, Postgres en option)
- Spring Mail

Frontend / Backoffice:

- Vue 3 + Vite 8
- Backoffice: Iconify Hugeicons + Tailwind CSS (preflight desactive)

## Prerequis

- Java 21+
- Maven 3.9+
- Node.js (version compatible `^20.19.0 || >=22.12.0`)
- npm
- (Optionnel) Docker + Docker Compose pour Postgres

## Demarrage rapide

### Option A - demarrage complet via Makefile

Depuis la racine du projet:

```bash
make all
```

Cela lance:

- backend sur `http://localhost:8080`
- frontend sur `http://localhost:5173`
- backoffice sur `http://localhost:5174`

Autres cibles utiles:

```bash
make back   # backend uniquement
make fback  # frontend + backoffice
```

### Option B - commandes manuelles

Backend:

```bash
mvn -f backend/pom.xml spring-boot:run
```

Frontend (client):

```bash
npm --prefix frontend install
npm --prefix frontend run dev -- --host 0.0.0.0 --port 5173
```

Backoffice:

```bash
npm --prefix backoffice install
npm --prefix backoffice run dev -- --host 0.0.0.0 --port 5174
```

### Postgres local (optionnel)

Le backend fonctionne par defaut avec H2 en memoire.

Pour utiliser Postgres local via Docker:

```bash
docker compose up -d postgres
```

Port expose par defaut: `5433` (mapping vers `5432` du container).

## Comptes seed (dev)

Le `DataInitializer` cree des comptes de demo:

- Super admin:
  - email: `superadmin@tabila.local`
  - mot de passe: `superadmin123`
- Admin restaurant:
  - email: `admin@tabila.local`
  - mot de passe: `admin123`

Important: ces identifiants sont uniquement pour le developpement local.

## Configuration backend

Fichier source: `backend/src/main/resources/application.properties`

Variables principales:

- `DB_URL` (defaut H2)
- `DB_USER`
- `DB_PASSWORD`
- `DB_DRIVER`
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`
- `CORS_ALLOWED_ORIGINS`
- `APP_MAIL_FROM`
- `APP_BACKOFFICE_URL`

Exemple pour Postgres local docker-compose:

```bash
export DB_URL=jdbc:postgresql://localhost:5433/tabila
export DB_USER=tabila
export DB_PASSWORD=tabila
export DB_DRIVER=org.postgresql.Driver
```

## Scripts utiles

Frontend:

```bash
npm --prefix frontend run dev
npm --prefix frontend run build
npm --prefix frontend run preview
```

Backoffice:

```bash
npm --prefix backoffice run dev
npm --prefix backoffice run build
npm --prefix backoffice run preview
```

Backend:

```bash
mvn -f backend/pom.xml spring-boot:run
mvn -f backend/pom.xml test
```

## Tests et build

Backend tests:

```bash
mvn -f backend/pom.xml test -q
```

Build frontend:

```bash
npm --prefix frontend run build
npm --prefix backoffice run build
```

## API et endpoints

Base URL API: `http://localhost:8080`

Exemples:

- Auth: `POST /api/auth/login`
- Admin: `/api/admin/*`
- Super admin: `/api/super-admin/*`
- Public/menu: `/api/menu` et `/menu`

Documentation OpenAPI (JSON):

- `GET /v3/api-docs`

UI docs:

- endpoint Scalar expose par le backend (voir `ScalarController`).
