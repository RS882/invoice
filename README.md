 
# 💳 Billing System — Java Spring Boot Project

A modular and testable billing system built with **Java 17**, **Spring Boot**, and **H2**, supporting:

- 🧠 Tariff strategy patterns (Strategy Design Pattern)
- 💵 Partial and multi-method payments
- 📜 Invoice generation with VAT and discounts
- 🧾 PDF invoice creation using **iText 7**
- ☁ File upload and download with **MinIO**
- 🧪 Comprehensive test coverage using **JUnit 5** and **Mockito**
- 🔍 Centralized logging with **AOP (AspectJ)**

---

## 🔧 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA + H2**
- **MinIO Java SDK**
- **iText PDF 7**
- **Swagger / OpenAPI 3**
- **JUnit 5, Mockito**

---

## 🧩 Features

### 1. Tariff Strategies (Strategy Pattern)
- `Basic`, `Premium`, `Business` plans
- Monthly fee, GB limits, overage charge
- Discounts based on subscription length
- Dynamic strategy resolution

### 2. Invoicing
- Invoice generation with calculated total
- VAT (19%) and discounts applied
- Invoices persisted in DB and exported to PDF

### 3. Payments
- Supports `CREDIT_CARD`, `PAYPAL`, `BANK_TRANSFER`
- Multi-payment & partial payment support
- Automatic invoice status tracking: `PAID`, `PARTIALLY_PAID`, `PENDING`, `OVERPAID`

### 4. File Management (MinIO)
- PDF invoices uploaded to MinIO
- MinIO path stored in DB
- Downloads available through service

### 5. Logging (AOP)
- All billing operations (calculate, upload, generate) logged via custom aspect
- Includes operation signature and customer/invoice IDs

### 6. Exception Handling
- Centralized global exception handler
- Custom exceptions: `NotFound`, `BadRequest`, `ServerError`
- Structured error responses with validation error details

### 7. Testing
- Service and controller tests with parameterized inputs
- Coverage for discounts, VAT, invalid inputs, edge cases
- Mocked PDF generation and repository layers

---

## 🧪 How to Run

```bash
# Run application (in-memory H2 DB)
./mvnw spring-boot:run
```

Go to `http://localhost:8080/swagger-ui.html` to explore the API.

---

## 📁 Example Scenario

> Customer: Premium, 120GB used, 18 months subscription

- Base: 50€ + (20GB * 1.5€) = 80€
- Discount 5% → 76€
- VAT 19% → **Total: 90.44€**
- Invoice saved in DB + uploaded to MinIO

---

## 🛠 Future Improvements

- Dockerize for local demo
- Testcontainers for integration testing
- CI integration (GitHub Actions)
- Frontend UI with PDF viewer

---

## 📄 Author

Developer: @Ruslan Senkin.
2025

