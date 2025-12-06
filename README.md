# School Management Application

## Description

This project is a complete School Management Application with:

- **Backend:** Spring Boot  
- **Frontend:** Angular  
- **Database:** MySQL  
- **Dockerized environment**  

It features JWT authentication, CRUD for students, CSV import/export, Swagger documentation, SweetAlert2 & NGX-Toastr notifications.

---

## Features

### Backend (Spring Boot)

- JWT authentication for admins  
- CRUD operations for Student entity  
- Search/filter students by username or level  
- Pagination  
- CSV import/export  
- Swagger documentation  
- Proper HTTP status codes for API responses  
- Unit tests (JUnit)

### Frontend (Angular)

- Login page (stores JWT token)  
- Students page:  
  - List, search, filter students  
  - Create/update/delete student  
  - CSV import/export  
- SweetAlert2 & NGX-Toastr for notifications  

---

## Requirements

- Docker & Docker Compose  
- Node.js & npm  
- Java 17+  

---

## Getting Started

### 1. Clone the project

```bash
git clone https://github.com/shaimaamaidi/school-management-app.git
cd school-management-app
```

### 2. Configure environment variables

Le projet utilise des variables d'environnement pour connecter le backend Spring Boot à la base de données MySQL.

#### Docker Compose

Dans le fichier `docker-compose.yml`, configurez les services `db` et `backend` comme suit :

```yaml
services:
  db:
    image: mysql:8
    restart: always
    environment:
      - MYSQL_ROOT_PASSWORD=<root_mysql_password>
      - MYSQL_DATABASE=school
    ports:
      - "3306:3306"
      
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/school?createDatabaseIfNotExist=true&serverTimezone=UTC
      - SPRING_DATASOURCE_USERNAME=<your_mysql_username>
      - SPRING_DATASOURCE_PASSWORD=<your_mysql_password>
    depends_on:
      - db
```

### 3. Lancer le projet avec Docker Compose

Pour démarrer tous les services (backend, frontend et base de données), utilisez la commande suivante dans le répertoire racine du projet :

```bash
docker-compose up --build
```

### 4. Accéder à l'application

Une fois les containers Docker démarrés, ouvrez votre navigateur et allez sur :

**http://localhost:4200/**

Vous pourrez vous connecter avec un compte admin et accéder à la gestion des étudiants.

---

## Swagger Documentation

Pour tester les API du backend et consulter leur documentation, ouvrez Swagger UI à l'adresse suivante :

**http://localhost:8080/swagger-ui.html**

Toutes les routes API sont listées avec :

- Les statuts HTTP possibles (200, 201, 400, 401, 404, 409, 500)
- Des exemples de requêtes et réponses
- La possibilité de tester les endpoints directement depuis l'interface

---

## Commandes utiles

### Arrêter les containers

```bash
docker-compose down
```

### Voir les logs

```bash
docker-compose logs -f
```

### Reconstruire les images

```bash
docker-compose build --no-cache
```

---

## Structure du projet

```
school-management-app/
├── backend/           # Spring Boot application
├── frontend/          # Angular application
├── docker-compose.yml # Docker configuration
└── README.md
```

---

## Technologies utilisées

### Backend
- Spring Boot 3.x
- Spring Security (JWT)
- Spring Data JPA
- MySQL 8
- Swagger/OpenAPI
- JUnit 5

### Frontend
- Angular 16+
- TypeScript
- TailwindCSS
- SweetAlert2
- NGX-Toastr

### DevOps
- Docker
- Docker Compose

---

## Author

**Shaimaa Maidi**  
[GitHub](https://github.com/shaimaamaidi)