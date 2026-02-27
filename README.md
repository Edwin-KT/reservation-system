# Reservation System

A client-server reservation application built with Java and Quarkus. The system uses raw TCP sockets for client-server communication and PostgreSQL for data persistence.

## Technologies

* Java 21
* Quarkus
* Hibernate ORM
* PostgreSQL

## Prerequisites

* JDK 21 or higher
* Maven
* A running instance of PostgreSQL

## Setup and Configuration

1. Create a PostgreSQL database named `reservation_db`.
2. Update the database credentials in `src/main/resources/application.properties` if your local setup differs from the defaults:

```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/reservation_db
quarkus.datasource.username=postgres
quarkus.datasource.password=your_password
