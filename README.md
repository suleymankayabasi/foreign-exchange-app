# Foreign Exchange Application

This project implements a simple foreign exchange application, providing endpoints to fetch exchange rates, perform currency conversions, and retrieve conversion history.

## Table of Contents

- [Functional Requirements](#functional-requirements)
- [Technical Requirements](#technical-requirements)
- [Optional Features](#optional-features)
- [Setup and Usage](#setup-and-usage)
- [API Endpoints](#api-endpoints)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)
- [License](#license)

## Functional Requirements

### Exchange Rate Endpoint

- **Input:** A pair of currency codes (e.g., USD to EUR).
- **Output:** The current exchange rate between the two currencies.

### Currency Conversion Endpoint

- **Input:** An amount in the source currency, source currency code, and target currency code.
- **Output:** The converted amount in the target currency and a unique transaction identifier.

### Conversion History Endpoint

- **Input:** A transaction identifier or a transaction date for filtering purposes (at least one must be provided).
- **Output:** A paginated list of currency conversions filtered by the provided criteria.

### External Exchange Rate Integration

- The application utilizes an external service provider for fetching exchange rates and optionally for performing currency conversion calculations.

### Error Handling

- Errors are handled gracefully, providing meaningful error messages and specific error codes.

## Technical Requirements

- **Self-Contained Application:** Requires no additional setup or configuration to run.
- **RESTful API Design:** Implemented using Spring Boot following REST principles.
- **Build & Dependency Management:** Uses Maven for building and managing dependencies.
- **Use of Design Patterns:** Implements appropriate design patterns to enhance code quality.
- **Code Structure:** Organized to reflect a clear separation of concerns.
- **Unit Testing:** Includes unit tests to ensure reliability and robustness.
- **API Documentation:** Provides complete and accurate documentation for the API, including request and response examples.
- **Docker:** Containerizes the application with Docker to ensure consistency across different environments.
- **Proper Use of Git:** Maintains code in a Git repository with meaningful commit messages and adheres to best practices for version control.

## Optional Features

- **API Documentation Tooling:** Utilizes Swagger or OpenAPI for generating interactive API documentation.
- **Caching:** Applies caching strategies to improve performance, particularly for exchange rate data.

## Setup and Usage

1. **Prerequisites:**
   - Java Development Kit (JDK)
   - Maven
   - Docker (if running in Docker container)

2. **Clone the repository:**
   ```bash
   git clone https://github.com/suleymankayabasi/foreign-exchange-app.git
   cd foreign-exchange-app

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**
   - **Using Maven:**
     ```bash
     mvn spring-boot:run
     ```
   - **Using Docker:**
     ```bash
     docker build -t foreign-exchange-app .
     docker run -p 8080:8080 foreign-exchange-app
     ```

5. **Access the API documentation:**
   - Open your web browser and go to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (if using Swagger).

## API Endpoints

### 1. Conversion History Endpoint

- **Endpoint:** `GET /api/conversations`
- **Description:** Retrieve conversion history by transaction ID or transaction date.
- **Parameters:**
  - `transactionId` (optional): The transaction ID to filter the conversion history.
  - `transactionDate` (optional): The transaction date to filter the conversion history (format: yyyy-MM-dd HH:mm:ss).
  - `page` (optional, default: 0): Page number for pagination.
  - `size` (optional, default: 5): Page size for pagination.
- **Responses:**
  - `200 OK`: Successfully retrieved conversion history.
  - `400 Bad Request`: Invalid input; 
  - `500 Internal Server Error`: An error occurred on the server.

### 2. Currency Conversion Endpoint

- **Endpoint:** `POST /api/currencies/convert`
- **Description:** Convert currency with given source and target currency codes and amount.
- **Request Body:**
  - `CurrencyConversionRequest`: Includes source currency code, target currency code, and amount.
- **Responses:**
  - `200 OK`: Successfully converted currency.
  - `400 Bad Request`: Invalid input; please provide valid request data.
  - `500 Internal Server Error`: An error occurred on the server.

### 3. Exchange Rate Endpoint

- **Endpoint:** `GET /api/exchange-rates`
- **Description:** Get exchange rate between two currencies.
- **Parameters**
   - `fromCurrency`(required): Source currency code in ISO 4217 format. Must be a 3-letter uppercase string (e.g., "USD").
   - `toCurrency` (required): Target currency code in ISO 4217 format. Must be a 3-letter uppercase string (e.g., "EUR").

- **Responses:**
  - `200 OK`: Successfully retrieved exchange rate.
  - `400 Bad Request`: Invalid input; please provide valid currency codes.
  - `500 Internal Server Error`: An error occurred on the server.

## API Documentation

For detailed API documentation, refer to the interactive Swagger UI or OpenAPI documentation provided at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) after starting the application.

## Testing

Unit tests are included to validate the functionality of the application. To run the tests, use:
```bash
mvn test
```

## Deployment

To deploy the application in a production environment, ensure Docker is installed and follow the Docker deployment steps mentioned above.

## Technologies Used

- **Spring Boot (3.3.2):** A framework that simplifies the development of Spring-based applications. It provides auto-configuration and various built-in features.
- **Spring Data JPA:** Simplifies data access and interaction with the database using Java Persistence API (JPA).
- **Spring Boot Starter Web:** Provides dependencies for building web applications and RESTful services.
- **Spring Boot DevTools:** Enhances development experience by enabling automatic application restarts and live reloads.
- **H2 Database:** A lightweight, in-memory database used for development and testing.
- **Lombok:** Reduces boilerplate code in Java by generating getters, setters, and other methods automatically.
- **Spring Boot Starter Test:** Includes libraries and tools for testing Spring applications, such as JUnit and AssertJ.
- **Spring Boot Starter Cache:** Provides caching support to improve application performance.
- **Spring Boot Configuration Processor:** Processes configuration properties to generate metadata.
- **Caffeine:** A high-performance caching library used for in-memory caching.
- **Springdoc OpenAPI Starter WebMVC UI:** Generates interactive API documentation using OpenAPI (Swagger).
- **Resilience4j:** Provides fault tolerance mechanisms like Circuit Breaker, Rate Limiter, and Retry for Java applications.
- **Spring Cloud Starter OpenFeign:** Simplifies communication with RESTful web services by using Feign clients.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your improvements.

## License

This project is licensed under the [MIT License](LICENSE).
```

This section provides a detailed guide on setup, usage, and deployment, along with information on API endpoints, testing, technologies used, and contributing guidelines.
