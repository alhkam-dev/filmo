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

# FILMO - WEB APPLICATION
## Running MariaDB with Docker:

A `docker-compose-dev.yml` file is provided in the root directory to easily spin up the database environment. To start the MariaDB container, run the following command in your terminal:
  ```docker compose -f docker-compose-dev.yml up -d```


## Database Initialization (Required Before Launching)

Since the application is configured to validate the database schema on startup (`spring.jpa.hibernate.ddl-auto=validate`), **you must manually create the tables and populate the initial data before running the Spring Boot application**.

Follow these steps using your preferred GUI client (DBeaver, HeidiSQL,...):

* The DB username and password is `db`/`db`
1. Connect to the `films` database using the credentials provided above.
2. Open and execute the **`001-upgrade.sql`** script (located in src/main/resources/scripts/databases) to generate all tables and relationships.
3. Open and execute the **`002-upgrade.sql`** script (located in src/main/resources/scripts/databases) to insert mandatory system roles (`USER`, `ADMIN`) and the admin user.


## Running the Application
To get the full functional experience of the web portal, both the film-api and film-web services must be running simultaneously.

`film-web` acts as an OAuth2 client that requests data internally from `film-api` (the resource server). If the API is down, the web portal will not be able to display or submit movie ratings.

Follow these steps to launch the ecosystem (from your IDE or via terminal):

### Launch the REST API (film-api) First
The following will launch the service on the port `:8085`
1. Move to the `film-api` module:
```bash
  cd film-api
```

2. Run the application
```bash
  mvn spring-boot:run
````

After that, execute the following steps. It will launch the service on the port `:8080`
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

---

# FILMO - REST SERVICE

## How Security Works Here (Simple Explanation)
To allow our web application (`film-web`) to request ratings movie data from the API (`film-api`), it must first prove its identity. We achieve this using a standard, secure backend-to-backend mechanism called **OAuth2 Client Credentials**.

## 1. Client Credentials (Username & Password)
To authenticate we have one user created. This is configured in the `application.yml` file:

* **Username:** `filmo-client`
* **Password:** `password-filmo`


### 1. Authenticating to get an Access Token
Before consuming protected resources, a client must request a JWT token by sending a `POST` request to the `/authenticate` endpoint.

* **URL:** `http://localhost:8085/authenticate`
* **Method:** `POST`
* **Headers:**
  * `Content-Type: application/x-www-form-urlencoded`
  * `Authorization: Basic <Base64-encoded credentials>`
* **Body parameters:**
  * `grant_type=client_credentials` (Required)

When you send this request you will obtain a jwt token that will give you the following authorities: 'read-resource', 'write-resource'

## Ratings API Endpoints Reference
All endpoints below require a valid **OAuth2 Bearer Token** in the `Authorization` header. You can use the jwt generated with the previous endpoint.

### 1. Get a Specific User's Rating
Retrieves the score and creation time given by a specific user to a film.

* **URL:** `/ratings/films/{filmId}/users/{userId}`
* **Method:** `GET`
* **Success Response (200 OK):** Returns a `RatingDetailsDTO` JSON object.
* **Error Responses:**
  * `401 Unauthorized`: Invalid or expired JWT token.
  * `404 Not Found`: The specified user has not rated this film.

---

### 2. Create a New Rating
Allows an authenticated client to submit a score (between 1 and 5) for a film.

* **URL:** `/ratings`
* **Method:** `POST`
* **Headers:** `Content-Type: application/json`
* **Success Response (201 Created):** Rating successfully saved.
* **Error Responses:**
  * `400 Bad Request`: Invalid input data (e.g., score out of bounds) or a rating already exists for this user and film.
  * `401 Unauthorized`: Invalid or expired JWT token.

---

### 3. Get Film Ratings Average
Retrieves the average score and the total number of ratings accumulated by a film.

* **URL:** `/ratings/films/{filmId}`
* **Method:** `GET`
* **Success Response (200 OK):** Returns a `RatingAverageResponseDTO` JSON object.
* **Error Responses:**
  * `401 Unauthorized`: Invalid or expired JWT token.


---

# FILMO - BATCH PROCESS
Its primary objective is to efficiently and incrementally export the movie catalog into a flat CSV file.

### CONTENT
The `exportFilmsJob` consists of a single Step:

1. **Reader (`JdbcCursorItemReader`):** Fetches movies from the MariaDB database. It applies a filter using a `LEFT JOIN` with the audit log table, selecting only the films that have never been exported before.
2. **Writer (`FlatFileItemWriter`):** Writes the movie records into a file (`films-exports.csv`).
3. **Listener (`ItemWriteListener` + `StepExecutionListener`):** Once a chunk of movies is successfully written to the CSV file, the listener triggers and notifies the business service (`FilmExportService`) to handle the audit logging.

### 📊 Audit Log Table (`film_export_log`)
Each exported movie is recorded with the following fields to prevent reprocessing during subsequent executions:
* `id`: Unique identifier for the log entry.
* `job_id`: Spring Batch execution instance ID.
* `film_id`: ID of the exported movie.
* `exported_at`: Timestamp of when the processing took place.

---

### 🚀 Execution Instructions

#### 1. Package the Project
Generate the executable `.jar` file by running the following command in the module's root directory:
```bash
  mvn clean package
```

#### 2. Manual execution
Once compiled, you can launch the process directly from the target folder inside the module:
```bash
  java -jar target/filmo-batch-1.0.jar
```


#### 2. Run with Maven (Development Mode)
If you want to run the process directly from the source code without manually packaging the JAR file.
1. Move to the `film-batch` module:
```bash
  cd film-batch
```
2. Run the application
```bash
  mvn spring-boot:run
````