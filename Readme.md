# Stock API - Documentation

## Overview
The Stock API is a **Spring Boot-based RESTful service** that:
- Fetches stock price data from **Polygon.io API**.
- Stores the data in a **MySQL database**.
- Provides endpoints to retrieve stock prices.
- Uses **SHA-256 (128-bit) hashed IDs** to prevent duplicate records and ensure scalability.
- Implements error handling for API rate limits and data integrity.

---

## Table of Contents
- [Technology Stack](#-technology-stack)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [Hash-Based ID Generation](#-hash-based-id-generation)
- [Installation & Setup](#-installation--setup)
- [Testing with Postman](#-testing-with-postman)
- [Error Handling](#-error-handling)
- [Performance Optimization](#-performance-optimization)

---

## Technology Stack
- **Spring Boot** (REST API & JPA)
- **MySQL** (Data Storage)
- **Hibernate** (ORM Framework)
- **Polygon.io API** (Stock Market Data Source)
- **SHA-256 Hashing** (Ensuring Unique IDs)
- **JUnit & Spring Boot Test** (Testing)

---

## Database Schema

The `stocks` table is structured as follows:


| Field            | Type         | Description                          |
|-----------------|-------------|--------------------------------------|
| `id`            | `BINARY(16)` | Unique SHA-256 (128-bit) ID         |
| `company_symbol`| `VARCHAR(10)` | Stock ticker symbol (e.g., AAPL)   |
| `date`          | `DATE`       | Stock price date                    |
| `open_price`    | `DOUBLE`     | Opening stock price                 |
| `close_price`   | `DOUBLE`     | Closing stock price                 |
| `high_price`    | `DOUBLE`     | Highest stock price                 |
| `low_price`     | `DOUBLE`     | Lowest stock price                  |
| `volume`        | `BIGINT`     | Number of shares traded             |

---

## API Endpoints

### 1 Fetch and Store Stock Data
**Endpoint:**
```http
POST /stocks/fetch
```
**Request Parameters:**
- `companySymbol` (String) – Stock ticker symbol (e.g., `AAPL`)
- `fromDate` (String, format: `YYYY-MM-DD`)
- `toDate` (String, format: `YYYY-MM-DD`)

**Example Request:**
```http
POST http://localhost:8080/stocks/fetch?companySymbol=AAPL&fromDate=2025-03-01&toDate=2025-03-05
```

**Response:**
```json
Stock data fetched and stored successfully.
```

### 2️ Get Stock Data by Symbol and Date
**Endpoint:**
```http
GET /stocks/{companySymbol}?date=YYYY-MM-DD
```
**Example Request:**
```http
GET http://localhost:8080/stocks/AAPL?date=2025-03-01
```

**Response:**
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

---

## Hash-Based ID Generation

To prevent duplicate records, each entry's **ID** is generated using a **SHA-256 hash function** applied to the `companySymbol` and `date`.

**Hash Generation Logic:**
```java
public static byte[] generateShortHash(String companySymbol, LocalDate date) {
    try {
        String input = companySymbol + date.toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        byte[] shortHash = new byte[16];
        System.arraycopy(hash, 0, shortHash, 0, 16);
        return shortHash;
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error generating hash", e);
    }
}
```

---

## Installation & Setup

### 1 Set Up MySQL Database
```sql
CREATE DATABASE stockdb;
USE stockdb;
```

### 2️ Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stockdb
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
```

### 3️ Run the Application
```bash
mvn clean spring-boot:run
```

---

## 🛠️ Testing with Postman

1. **Fetch and Store Stock Data:**
   - `POST` → `http://localhost:8080/stocks/fetch?companySymbol=AAPL&fromDate=2025-03-01&toDate=2025-03-05`
   - Check **MySQL**: `SELECT * FROM stocks;`

2. **Retrieve Stock Data:**
   - `GET` → `http://localhost:8080/stocks/AAPL?date=2025-03-01`

---

## Error Handling

| Error Type              | Description |
|-------------------------|-------------|
| `HttpClientErrorException` | Handles API failures (Rate Limit). |
| `DateTimeParseException`  | Handles incorrect date formats. |
| `DuplicateKeyException`   | Prevents duplicate stock entries. |
| `IllegalArgumentException` | Handles invalid input parameters. |

---

## Performance Optimization

### 1️ Bulk Inserts to Improve Efficiency
Using `saveAll()` for batch inserts:
```java
List<Stock> stocksToSave = new ArrayList<>();
for (Map<String, Object> result : results) {
    Stock stock = new Stock(companySymbol, date, openPrice, closePrice, highPrice, lowPrice, volume);
    stocksToSave.add(stock);
}
stockRepository.saveAll(stocksToSave);
```

## Conclusion
This Stock API is **optimized for scalability**, ensures **data integrity using hash-based IDs**, and efficiently handles **large-scale stock data fetching**. 🚀

