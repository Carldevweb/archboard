# ArchBoard

ArchBoard est une API backend de gestion de tableaux Kanban inspirée d’outils comme Trello.

Ce projet a été développé pour mon portfolio afin de démontrer la conception d’une architecture backend propre avec **Java 21**, **Spring Boot**, **Spring Security JWT**, **PostgreSQL** et une approche **Clean Architecture**.

L'objectif est de construire un backend modulaire, sécurisé et extensible permettant la gestion de tableaux Kanban avec colonnes et cartes.

---

# Stack technique

- Java 21
- Spring Boot
- Spring Security
- JWT (authentification stateless)
- PostgreSQL
- Maven
- JPA / Hibernate

---

# Architecture

Le projet suit une **Clean Architecture** organisée en couches :


api -> controllers + DTO
app -> use cases
domain -> entités métier + interfaces repository
infra -> implémentations techniques (JPA, adapters)


## Structure principale du projet


archboard
├── common
│ ├── access
│ └── events
│
├── security
│
├── user
│
├── workspace
│
├── board
│
├── column
│
├── card
│
└── activity


Chaque module contient généralement :


api
app
domain
infra


---

# Modèle métier

Le modèle métier suit une structure hiérarchique classique d'un système Kanban :


Workspace
└── Board
└── Column
└── Card


---

# Fonctionnalités

## Authentification

- inscription utilisateur
- connexion
- récupération de l'utilisateur courant
- authentification JWT stateless

Endpoints :


POST /api/v1/auth/register
POST /api/v1/auth/login
GET /api/v1/me


---

## Workspaces

Gestion des espaces de travail.

Endpoints :


POST /api/v1/workspaces
GET /api/v1/workspaces
GET /api/v1/workspaces/{id}
PATCH /api/v1/workspaces/{id}
DELETE /api/v1/workspaces/{id}


---

## Boards

Un workspace peut contenir plusieurs boards.

Endpoints :


POST /api/v1/workspaces/{workspaceId}/boards
GET /api/v1/workspaces/{workspaceId}/boards
GET /api/v1/boards/{boardId}
PATCH /api/v1/boards/{boardId}
DELETE /api/v1/boards/{boardId}


---

## Columns

Les colonnes représentent les étapes du workflow Kanban.

Endpoints :


POST /api/v1/boards/{boardId}/columns
GET /api/v1/boards/{boardId}/columns
PATCH /api/v1/columns/{columnId}
PATCH /api/v1/columns/{columnId}/move
DELETE /api/v1/columns/{columnId}


Les colonnes possèdent une **position** permettant de gérer l'ordre dans un board.

---

## Cards

Les cartes représentent les tâches.

Endpoints :


POST /api/v1/columns/{columnId}/cards
GET /api/v1/columns/{columnId}/cards
PATCH /api/v1/cards/{cardId}
PATCH /api/v1/cards/{cardId}/move
DELETE /api/v1/cards/{cardId}


Les cartes peuvent être :

- déplacées dans la même colonne
- déplacées vers une autre colonne

La position est recalculée automatiquement.

---

# Sécurité

L'API utilise **Spring Security avec JWT**.

Caractéristiques :

- authentification stateless
- Bearer Token
- filtres personnalisés
- contrôle d'accès centralisé

Les vérifications d'accès sont gérées via :


AccessService


---

# Domain Events

Le projet utilise un système simple de **Domain Events** pour découpler les effets secondaires.

Exemple :


CardMovedEvent


Lorsqu'une carte est déplacée :


MoveCardUseCase
↓
CardMovedEvent
↓
Activity listener
↓
CreateActivityUseCase


Cela permet d'ajouter facilement :

- audit log
- notifications
- analytics
- websocket

sans modifier les use cases.

---

# Lancer le projet en local

## Prérequis

- Java 21
- Maven
- PostgreSQL

## Lancement


mvn spring-boot:run


L’API sera disponible sur :


http://localhost:8080


---

# Tests API

Les endpoints peuvent être testés avec :

- Postman
- Insomnia
- curl

Le projet a été entièrement testé via **Postman**.

---

# Auteur

Projet développé par **Charles Flahault Knezic** dans le cadre d’un portfolio backend.
