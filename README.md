# VW DigitalHub Vending Machine Backend

Backend service **Spring Boot** application for managing dispensers and products in a digital vending machine.

---

## 📋 Table of Contents

- [Description](#description)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
    - [Create Dispenser](#create-dispenser)
    - [Get Dispenser](#get-dispenser)
    - [Get Dispenser Status](#get-dispenser-status)
    - [Create Product](#create-product)
    - [Get Product Stock](#get-product-stock)
    - [Purchase Product](#purchase-product)
- [OpenAPI / Swagger](#openapi--swagger)
- [Testing](#testing)
- [Contributing](#contributing)

---

## Description

This microservice exposes endpoints to:

- Register new **Dispensers**
- Add **Products** to a dispenser
- Get the stock of a product in a dispenser
- Purchases a product and change returns

The architecture follows **Hexagonal / Clean Architecture** and **DDD** principles:

- **domain**: entities & repository interfaces
- **application**: use-case implementations
- **infrastructure**: adapters (JPA, Web)
- **adapter/web**: REST controllers

---

## Features

- UUID-based IDs for Dispensers & Products
- Persistence via **Spring Data JPA**
- In-memory **H2** (for tests) and **PostgreSQL** (production / Testcontainers)
- Auto-generated API docs with **Springdoc OpenAPI**
- Unit & integration tests with **JUnit 5**, **Mockito**, **Testcontainers**

---

## Technology Stack

- **Java 11**
- **Spring Boot 2.7.12**
- **Spring Data JPA**
- **H2**, **PostgreSQL**
- **Testcontainers** (PostgreSQL, Kafka)
- **Springdoc OpenAPI** (Swagger UI)
- **Lombok**, **MapStruct**
- **Maven**

---

## Requirements

- **JDK 11**
- **Maven 3.6+**
- **Docker**

---

## Getting Started

1. **Clone the repository**

    ```bash
    git clone https://github.com/Litunidam/vw-digitalhub-vending-machine-backend.git
    cd vw-digitalhub-vending-machine-backend

2. **Docker Compose**

   A docker-compose.yml is included to launch PostgreSQL and any other required services.
    ```bash
    docker-compose up -d
   
3. **Build and start the application**

    ```bash
    mvn clean install
    mvn spring-boot:run

The service will be available at http://localhost:8080.

---

## API Endpoints

- ## Create Dispenser

  ```bash
    POST /api/v1/vending/dispenser
    Content-Type: application/json
  
- Request Body:

    ```json
      {
        "products": [
          {
            "name": "Fanta",
            "price": 2.70,
            "stock": 25,
            "expiration": "2026-07-31"
          }
        ],
        "initialCoins": {
          "coins": {
          "EUR_2": 10,
          "EUR_1": 10,
          "CENT_50": 20,
          "CENT_20": 20,
          "CENT_10": 20,
          "CENT_05": 20
          }
        },
        "status": "AVAILABLE"
      }
  
- Response (200 OK):

    ```json
    {
      "id": "c06b256e-fd00-4baf-ae1e-e5150fec80b3",
      "products": {
        "92a7a7d9-7bf7-4d20-a83f-90f86eae5ee9": {
          "id": "92a7a7d9-7bf7-4d20-a83f-90f86eae5ee9",
          "name": "Fanta",
          "price": 2.7,
          "stock": 25,
          "expirationDate": "2026-07-31"
        }
      },
      "dispenserMoney": {
        "coins": {
          "EUR_2": 10,
          "EUR_1": 10,
          "CENT_50": 20,
          "CENT_20": 20,
          "CENT_10": 20,
          "CENT_05": 20
        }
      },
      "status": "AVAILABLE",
      "insertedMoney": null
    }

- ## Get Dispenser Status

    ```bash
    GET /api/v1/vending/dispenser/{dispenserId}/status

- Response (200 OK):

    ```json
    {
      "status": "AVAILABLE"
    }

- ## Get Dispenser

    ```bash
    GET /api/v1/vending/dispenser/{dispenserId}

- Response (200 OK):

    ```json
    {
      "id": "c06b256e-fd00-4baf-ae1e-e5150fec80b3",
      "products": {
        "92a7a7d9-7bf7-4d20-a83f-90f86eae5ee9": {
          "id": "92a7a7d9-7bf7-4d20-a83f-90f86eae5ee9",
          "name": "Fanta",
          "price": 2.7,
          "stock": 25,
          "expirationDate": "2026-07-31"
        }
      },
      "dispenserMoney": {
        "coins": {
          "EUR_2": 10,
          "EUR_1": 10,
          "CENT_50": 20,
          "CENT_20": 20,
          "CENT_10": 20,
          "CENT_05": 20
        }
      },
      "status": "AVAILABLE",
      "insertedMoney": null
    }

- ## Create Product

    ```bash
    POST /api/v1/vending/product
    Content-Type: application/json
  
- Request Body:

    ```json
    {
      "dispenserId": "00000000-0000-0000-0000-000000000001",
      "name": "Fanta",
      "price": 2.70,
      "stock": 25,
      "expiration": "2026-07-31"
    }
  
- Response (200 OK):

    ```json
    {
      "id": "00000000-0000-0000-0000-000000000001",
      "name": "Fanta",
      "price": 2.7,
      "stock": 10,
      "expirationDate": "2026-12-01"
    }

- ## Get Product Stock

    ```bash
    GET /api/v1/vending/product/{productId}
  
- Response (200 OK):

    ```json
    {
      "stock": "10"
    }

- ## Purchase Product

    ```bash
    POST /api/v1/vending/dispenser/{dispenserId}/purchase
    Content-Type: application/json

- Request Body:

    ```json
    {
      "productId": "00000000-0000-0000-0000-000000000001",
      "coins": {
        "EUR_2": 1,
        "EUR_1": 1
      },
      "confirmed": true
    }

- Response (200 OK):

    ```json
    {
      "product" : {
        "id": "00000000-0000-0000-0000-000000000001",
        "name": "Fanta",
        "price": 2.7,
        "stock": 10,
        "expirationDate": "2026-12-01"
      },
      "change": {
        "coins": {
          "CENT_20": 1,
          "CENT_10": 1
        }
      }
    }
  
## OpenAPI / Swagger

- Interactive API documentation is available at:

    ```bash
    http://localhost:8080/swagger-ui/index.html

## Testing

- Run tests

    ```bash
    mvn test -Dtest=*Test
  
- Integration Tests requires Docker & Testcontainers

## Contributing

Fork the repository

Create a feature branch

Commit your changes with descriptive messages

Push and open a Pull Request

All changes are welcome