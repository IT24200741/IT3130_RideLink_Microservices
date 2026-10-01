# 💳 RideLink — Fare & Payment Service

The **Fare & Payment Service** is an independent microservice in the RideLink platform responsible for calculating ride fare estimates, computing accurate final trip fares (incorporating base fare, distance, time, surge pricing, tolls, and discounts), and managing the end-to-end payment settlement lifecycle with printable digital receipts.

---

## 📌 Service Overview

| Property | Details |
|---|---|
| **Service Name** | `fare-payment-service` |
| **Port** | `8084` |
| **Gateway Port** | `8080` (routes `/api/fares/**` and `/api/payments/**`) |
| **Framework** | Spring Boot 4.1.x / Java 21 |
| **Database** | MongoDB (Database: `ridelink_fare_payment_db`) |
| **Architecture** | Controller ➔ Service ➔ Repository ➔ MongoDB |
| **Interactive Docs** | Swagger UI: `http://localhost:8084/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8084/v3/api-docs` |

---

## 🏛️ Architecture & Data Isolation

```
                         Client / Postman / Frontend
                                     │
                                     ▼
                     +───────────────────────────────+
                     |       API GATEWAY (:8080)     |
                     +───────────────┬───────────────+
                                     │
                 /api/fares/**       │     /api/payments/**
                                     ▼
                     +───────────────────────────────+
                     |    FARE & PAYMENT SERVICE     |
                     |            (:8084)            |
                     +───────────────┬───────────────+
                                     │
                                     ▼
                     +───────────────────────────────+
                     |      MongoDB (localhost:27017)|
                     |   DB: ridelink_fare_payment_db|
                     |      ├── fares                |
                     |      └── payments             |
                     +───────────────────────────────+
```

---

## 📊 Domain Models

### 1. `Fare` Collection (`fares`)
| Field | Type | Description |
|---|---|---|
| `id` | `String` | MongoDB Document ID (Primary Key) |
| `rideId` | `String` | Associated Ride ID in `ride-service` |
| `distanceKm` | `double` | Estimated or actual distance in kilometers |
| `durationMinutes` | `double` | Estimated or actual duration in minutes |
| `baseFare` | `double` | Base starting charge (Fixed: `100.00 LKR`) |
| `distanceCharge` | `double` | Distance charge (`50.00 LKR / km`) |
| `timeCharge` | `double` | Duration charge (`5.00 LKR / min`) |
| `surgeMultiplier` | `double` | Demand surge factor (e.g. `1.2` for peak hours) |
| `tollCharges` | `double` | Highway / expressway tolls (e.g. `300.00 LKR`) |
| `discountAmount` | `double` | Promo code / promotional discount |
| `totalFare` | `double` | Final computed payable amount (LKR) |
| `currency` | `String` | Currency code (Default: `LKR`) |
| `fareType` | `String` | `"ESTIMATE"` or `"FINAL"` |
| `calculatedAt` | `LocalDateTime` | Calculation timestamp |

### 2. `Payment` Collection (`payments`)
| Field | Type | Description |
|---|---|---|
| `id` | `String` | MongoDB Document ID (Primary Key) |
| `rideId` | `String` | Associated Ride ID in `ride-service` |
| `passengerId` | `String` | Passenger ID who pays |
| `driverId` | `String` | Driver ID who receives payment |
| `amount` | `double` | Payable amount in LKR |
| `currency` | `String` | Currency code (`LKR`) |
| `paymentMethod` | `PaymentMethod` | Enum: `CASH`, `CREDIT_CARD`, `DEBIT_CARD`, `WALLET` |
| `status` | `PaymentStatus` | Enum: `PENDING`, `COMPLETED`, `FAILED`, `REFUNDED` |
| `transactionReference` | `String` | Unique generated transaction code (e.g. `TXN-RL-8F3A29B1`) |
| `note` | `String` | Optional payment memo / notes |
| `createdAt` | `LocalDateTime` | Payment record created timestamp |
| `completedAt` | `LocalDateTime` | Settlement completion timestamp |

---

## 📡 Complete REST API Endpoints

All endpoints are accessible via the **API Gateway (`http://localhost:8080`)** or directly on **Fare & Payment Service (`http://localhost:8084`)**.

### 🏷️ A. Fare Calculation Endpoints (`/api/fares/**`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/fares/estimate` | Calculate initial estimated fare for a ride request |
| `POST` | `/api/fares/final` | Calculate actual final fare upon ride completion (with tolls, discounts, surge) |
| `GET` | `/api/fares/ride/{rideId}` | Get fare calculation breakdown for a specific ride |
| `GET` | `/api/fares/{id}` | Get fare record by ID |

---

### 💳 B. Payment Management Endpoints (`/api/payments/**`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/payments` | Create a pending payment request (`status = PENDING`) |
| `POST` | `/api/payments/{id}/process` | Process / Settle payment (`PENDING` ➔ `COMPLETED` + generates TXN code) |
| `GET` | `/api/payments/{id}` | Get payment details by ID |
| `GET` | `/api/payments/ride/{rideId}` | Get payment record for a specific ride |
| `GET` | `/api/payments/passenger/{passengerId}` | Get all payments made by a passenger |
| `GET` | `/api/payments` | List all payments (supports `?status=COMPLETED` filter) |
| `GET` | `/api/payments/{id}/receipt` | **Generate detailed digital receipt** with full fare breakdown |

---

## 🧪 Postman Testing Walkthrough

### 1. Calculate Initial Fare Estimate
```http
POST http://localhost:8080/api/fares/estimate
Content-Type: application/json

{
  "rideId": "6ab5000149dadbf46a7ce800",
  "distanceKm": 5.0,
  "durationMinutes": 15.0
}
```
**Response (`201 Created`):**
```json
{
  "id": "6ab6000149dadbf46a7ce900",
  "rideId": "6ab5000149dadbf46a7ce800",
  "distanceKm": 5.0,
  "durationMinutes": 15.0,
  "baseFare": 100.0,
  "distanceCharge": 250.0,
  "timeCharge": 75.0,
  "totalFare": 425.0,
  "currency": "LKR"
}
```

---

### 2. Calculate Final Fare upon Trip Completion
```http
POST http://localhost:8080/api/fares/final
Content-Type: application/json

{
  "rideId": "6ab5000149dadbf46a7ce800",
  "actualDistanceKm": 8.5,
  "actualDurationMinutes": 22.0,
  "surgeMultiplier": 1.2,
  "tollCharges": 300.00,
  "discountAmount": 50.00
}
```
**Response (`201 Created`):**
```json
{
  "id": "6ab6000249dadbf46a7ce910",
  "rideId": "6ab5000149dadbf46a7ce800",
  "distanceKm": 8.5,
  "durationMinutes": 22.0,
  "baseFare": 100.0,
  "distanceCharge": 425.0,
  "timeCharge": 110.0,
  "surgeMultiplier": 1.2,
  "tollCharges": 300.0,
  "discountAmount": 50.0,
  "totalFare": 992.0,
  "currency": "LKR",
  "fareType": "FINAL",
  "calculatedAt": "2026-09-24T16:35:00"
}
```

---

### 3. Create a Payment Record
```http
POST http://localhost:8080/api/payments
Content-Type: application/json

{
  "rideId": "6ab5000149dadbf46a7ce800",
  "passengerId": "6ab4885449dadbf46a7ce771",
  "driverId": "6ab4900149dadbf46a7ce780",
  "amount": 992.00,
  "paymentMethod": "CREDIT_CARD",
  "note": "Payment for Ride #6ab5000149dadbf46a7ce800"
}
```
**Response (`201 Created`):**
```json
{
  "id": "6ab7000149dadbf46a7ce950",
  "rideId": "6ab5000149dadbf46a7ce800",
  "passengerId": "6ab4885449dadbf46a7ce771",
  "driverId": "6ab4900149dadbf46a7ce780",
  "amount": 992.0,
  "currency": "LKR",
  "paymentMethod": "CREDIT_CARD",
  "status": "PENDING",
  "transactionReference": null,
  "createdAt": "2026-09-24T16:36:00"
}
```

---

### 4. Process / Settle Payment
```http
POST http://localhost:8080/api/payments/6ab7000149dadbf46a7ce950/process
Content-Type: application/json

{
  "paymentMethod": "CREDIT_CARD",
  "transactionNote": "Card authorized successfully"
}
```
**Response (`200 OK`):**
```json
{
  "id": "6ab7000149dadbf46a7ce950",
  "rideId": "6ab5000149dadbf46a7ce800",
  "amount": 992.0,
  "status": "COMPLETED",
  "transactionReference": "TXN-RL-7A9B3E1F",
  "completedAt": "2026-09-24T16:37:00"
}
```

---

### 5. Generate Digital Payment Receipt
```http
GET http://localhost:8080/api/payments/6ab7000149dadbf46a7ce950/receipt
```
**Response (`200 OK`):**
```json
{
  "receiptNumber": "REC-A7CE950",
  "paymentId": "6ab7000149dadbf46a7ce950",
  "rideId": "6ab5000149dadbf46a7ce800",
  "passengerId": "6ab4885449dadbf46a7ce771",
  "driverId": "6ab4900149dadbf46a7ce780",
  "totalAmount": 992.0,
  "currency": "LKR",
  "paymentMethod": "CREDIT_CARD",
  "paymentStatus": "COMPLETED",
  "transactionReference": "TXN-RL-7A9B3E1F",
  "fareBreakdown": {
    "baseFare": 100.0,
    "distanceCharge": 425.0,
    "timeCharge": 110.0,
    "surgeMultiplier": 1.2,
    "tollCharges": 300.0,
    "discountAmount": 50.0,
    "totalFare": 992.0,
    "currency": "LKR"
  },
  "issuedAt": "2026-09-24T16:37:00"
}
```

---

## 💡 Viva Q&A Reference for IT3130

**Q1: What is the difference between estimated fare and final fare?**  
> **A:** Estimated fare is computed before the ride based on estimated distance and duration. Final fare is calculated after trip completion incorporating actual GPS distance, actual elapsed minutes, real-time demand surge multiplier, expressway toll fees, and promotional discounts.

**Q2: How does the payment lifecycle work in this microservice?**  
> **A:** When a ride ends, a `PENDING` payment is created via `POST /api/payments`. When the rider confirms card/cash settlement, `POST /api/payments/{id}/process` validates the payment, marks it `COMPLETED`, generates a unique `transactionReference`, and enables digital receipt retrieval via `GET /api/payments/{id}/receipt`.
