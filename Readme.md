# Stock API - Documentation

## Overview
The Stock API is a **Spring Boot-based RESTful service** that:
- Fetches stock price data from **Polygon.io API**.
- Stores the data in a **MySQL database**.
- Uses **DTOs (Data Transfer Objects)** to structure API responses.
- Implements **Exponential Backoff Retry** to handle API rate limits.
- **Supports only one stock record per request**, ensuring data integrity and preventing inconsistencies.

---

## Table of Contents
- [Technology Stack](#-technology-stack)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [Installation & Setup](#-installation--setup)
- [Exponential Backoff Retry](#-exponential-backoff-retry)
- [Error Handling](#-error-handling)
- [Performance Optimization](#-performance-optimization)

---

## Technology Stack
- **Spring Boot** (REST API & JPA)
- **MySQL** (Data Storage)
- **Hibernate** (ORM Framework)
- **Polygon.io API** (Stock Market Data Source)
- **Spring Retry** (Automatic Retry Mechanism)
- **JUnit & Spring Boot Test** (Testing)

---

## Database Schema

The `stocks` table is structured as follows:

| Field            | Type         | Description                          |
|-----------------|-------------|--------------------------------------|
| `id`            | `BIGINT` (Auto-increment) | Unique identifier |
| `company_symbol`| `VARCHAR(10)` | Stock ticker symbol    |
| `date`          | `DATE`       | Stock price date                    |
| `open_price`    | `DOUBLE`     | Opening stock price                 |
| `close_price`   | `DOUBLE`     | Closing stock price                 |
| `high_price`    | `DOUBLE`     | Highest stock price                 |
| `low_price`     | `DOUBLE`     | Lowest stock price                  |
| `volume`        | `BIGINT`     | Number of shares traded             |

**The API allows only one record per `company_symbol` and `date`.**  
If multiple records exist, the GET request will fail.

---

## API Endpoints

### 1 Fetch and Store Stock Data
**Endpoint:**  
`POST /stocks/fetch`

**Request Parameters:**  
- `companySymbol` (String) – Stock ticker symbol (e.g., `AAPL`)
- `fromDate` (String, format: `YYYY-MM-DD`)
- `toDate` (String, format: `YYYY-MM-DD`)

**Example cURL Request:**
```bash
curl -X POST "http://localhost:8080/stocks/fetch?companySymbol=AAPL&fromDate=2025-03-01&toDate=2025-03-05"
```

**Response:**
```json
{
  "message": "Stock data fetched and stored successfully."
}
```

---

### 2 Get Stock Data by Symbol and Date
**Endpoint:**  
`GET /stocks/{companySymbol}?date=YYYY-MM-DD`

**Example cURL Request:**
```bash
curl -X GET "http://localhost:8080/stocks/AAPL?date=2025-03-03"
```

**Response (DTO Structure):**
```json
{
  "companySymbol": "AAPL",
  "date": "2025-03-01",
  "openPrice": 174.25,
  "closePrice": 176.10,
  "highPrice": 178.00,
  "lowPrice": 172.80,
  "volume": 58900000
}
```

**If more than one result is found, the API will return an error.**

---

# Stock API - Installation & Setup

## Prerequisites
Before running the project, ensure you have the following installed:

- **Git** (to clone the repository)
- **MySQL** (if running locally)
- **Maven** (if running locally)
- **Docker & Docker Compose** (if running with Docker)

## 1. Clone the Repository
First, clone the project from GitHub and navigate to the project directory:

```bash
git clone <repository_url>
cd <repository_directory>
```

---

## Option 1: Running Locally

### 2. Install and Configure MySQL
Make sure MySQL is installed and running. Then, create a new database:

```sql
CREATE DATABASE stockdb;
USE stockdb;
```

### 3. Configure `application.properties`
Update your `src/main/resources/application.properties` file with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stockdb
spring.datasource.username=stock_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
```

### 4. Run the Application
Ensure you have Maven installed, then execute:

```bash
mvn clean spring-boot:run
```
---
## Option 2: Running with Docker

### 2. Build and Run the Containers
Make sure you are in the root directory of the project (where `docker-compose.yml` is located), then run:

```bash
docker-compose up --build
```

This will start both the MySQL database and the Spring Boot application inside Docker containers.

### 3. Verify Everything is Running
To check running containers:
```bash
docker ps
```
---

## Exponential Backoff Retry
To handle **rate limiting (HTTP 429 Too Many Requests)** from **Polygon.io**, the system uses **Spring Retry** with exponential backoff.

### 🔹 Configuration in Code
```java
@Retryable(
    value = HttpClientErrorException.class,
    maxAttempts = 7,
    backoff = @Backoff(delay = 1000, multiplier = 2) // 1s → 2s → 4s → 8s → 16s → 32s → 64s
)
```

If the API returns `429 Too Many Requests`, retry will **automatically trigger**.  
If all retries fail, an error will be thrown.

---

## Error Handling

| Error Type                  | Description |
|-----------------------------|-------------|
| `HttpClientErrorException` | Captures API failures (Rate Limit, Not Found, etc.). |
| `IncorrectResultSizeDataAccessException` | Thrown when multiple results are found for a unique query. |
| `DateTimeParseException`  | Handles incorrect date formats. |
| `IllegalArgumentException` | Thrown when invalid parameters are received. |

---

## Performance Optimization

### 1 Batch Inserts to Improve Efficiency
The API uses **`saveAll()`** to optimize bulk inserts:

```java
stockRepository.saveAll(stocks);
```
**This reduces the number of database calls, improving performance.**

---
