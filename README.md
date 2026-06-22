# Filmo Application

Filmo is a backend system designed to manage movie ratings.

## Project Structure

This project is organized as a Maven multi-module architecture to separate concerns across the system components:

* **`film-app` (Root):** The parent module that coordinates the build configuration and shared dependencies.
* **`film-web`:** The frontend web portal built with Spring Boot and Thymeleaf for user interactions.
* **`film-api`:** The RESTful web services that handle ratings.
* **`film-batch`:** The automated batch processing system for data export tasks.

Tech stack:
* Java 21
* Maven 3.9.9
* Spring Boot 4.1.0
* Database:
    - Development: mariaDB
    - Testing: H2

---
## Running MariaDB with Docker:

A `docker-compose-dev.yml` file is provided in the root directory to easily spin up the database environment. To start the MariaDB container, run the following command in your terminal:
  ```docker compose -f docker-compose-dev.yml up -d```


## Database Initialization (Required Before Launching)

Since the application is configured to validate the database schema on startup (`spring.jpa.hibernate.ddl-auto=validate`), **you must manually create the tables and populate the initial data before running the Spring Boot application**.

Follow these steps using your preferred GUI client (DBeaver, HeidiSQL,...):

* The DB username and password is `db`/`db`
1. Connect to the `films` database using the credentials provided above.
2. Open and execute the **`001-upgrade.sql`** script (located in src/main/resources/scripts/databases) to generate all tables and relationships.
3. Open and execute the **`002-upgrade.sql`** script (located in src/main/resources/scripts/databases) to insert mandatory system roles (`USER`, `ADMIN`) and test users.


## Running the Application

Once the database is up and initialized, you can launch the application from your IDE by running the `FilmWebApplication` main class, or via the terminal using Maven:
1. Move to the `film-web` module:
```bash
  cd film-web
```
2. Run the application
```bash
  mvn spring-boot:run
````


## Default ADMIN user for the project
- username: `admin`
- password: `admin123`


## Database Access (Optional)

If you want to inspect or manage the database using a GUI client like **HeidiSQL** or **DBeaver** create a new connection with the following parameters:

* **Network Type / Driver:** MariaDB or MySQL (TCP/IP)
* **Host/IP:** `127.0.0.1` (or `localhost`)
* **Port:** `3308` *(Mapped in docker-compose-dev.yml)*
* **Database / Schema:** `films`
