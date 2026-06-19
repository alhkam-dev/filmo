# Filmo Application

Filmo is a backend system designed to manage movie ratings.

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


## Database Access (Optional)

If you want to inspect or manage the database using a GUI client like **HeidiSQL** or **DBeaver** create a new connection with the following parameters:

* **Network Type / Driver:** MariaDB or MySQL (TCP/IP)
* **Host/IP:** `127.0.0.1` (or `localhost`)
* **Port:** `3308` *(Mapped in docker-compose-dev.yml)*
* **Database / Schema:** `flights`

You can log in using the following username and password: 'db' | 'db'
