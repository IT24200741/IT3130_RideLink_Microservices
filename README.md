# 🚗 RideLink · Backend Microservices for On-Demand Ride-Sharing

[![Course](https://img.shields.io/badge/Course-IT3130%20Application%20Development-blue.svg)](https://courseweb.sliit.lk)
[![Architecture](https://img.shields.io/badge/Architecture-Microservices-orange.svg)](#system-architecture)
[![Database](https://img.shields.io/badge/Pattern-Database--per--Service-green.svg)](#database-isolation)
[![Target Deadline](https://img.shields.io/badge/Deadline-Oct%2001%2C%202026-red.svg)](#project-schedule)

**RideLink** is a distributed backend platform for a ride-sharing ecosystem. It is composed of four loosely coupled, independently deployable microservices communicating via lightweight synchronous REST APIs, strictly adhering to the **Database-per-Service** architectural pattern.

---

## 👥 Microservice Allocation & Team Ownership

Each team member is exclusively responsible for the design, implementation, automated unit testing, and documentation of one independent microservice:

| # | Microservice | Port | Owner | Key Responsibilities |
|---|---|---|---|---|
| **1** | **Account Service** | 5001 | **Sasiru** | Passenger & Driver registration, BCrypt password hashing, JWT token authentication, Role-based access control (Passenger, Driver, Admin), profile management. |
| **2** | **Driver & Vehicle Service** | 5002 | **Randi Sithma** | Driver operational profiles, vehicle registration, Availability status toggle (AVAILABLE, BUSY, OFFLINE), simulated GPS tracking, driver discovery query. |
| **3** | **Ride Management Service** | 5003 | **Bhanuka** | Ride lifecycle orchestrator (REQUESTED -> ACCEPTED -> IN_PROGRESS -> COMPLETED), dispatching to Driver Service, status state machine validation. |
| **4** | **Fare & Payment Service** | 5004 | **Nethmini Perera** | Fare estimation algorithm (Base + Distance + Time), final fare calculation upon ride completion, payment simulation (Success/Failed), digital receipt generation. |

---

## 🏛️ System Architecture & Service Interactions

`
                          ┌───────────────────────────┐
                          │   Client / Postman / API  │
                          └─────────────┬─────────────┘
                                        │
           ┌────────────────────────────┼────────────────────────────┐
           │ HTTP                       │ HTTP                       │ HTTP
           ▼                            ▼                            ▼
┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐
│ 1. Account Service    │   │ 3. Ride Management    │   │ 4. Fare & Payment     │
│    (Port 5001)        │   │    (Port 5003)        │   │    (Port 5004)        │
│   [Owner: Sasiru]     │   │   [Owner: Bhanuka]    │   │ [Owner: Nethmini P.]  │
└──────────┬────────────┘   └───────┬───────┬───────┘   └───────────┬───────────┘
           │                        │       │                       │
      (Users DB)                    │       └───────────────────────┤
                                    │        Trigger payment        │
                                    ▼        on completion     (Payments DB)
                        ┌───────────────────────┐
                        │ 2. Driver & Vehicle   │
                        │    (Port 5002)        │
                        │ [Owner: Randi Sithma] │
                        └───────────┬───────────┘
                                    │
                               (Driver DB)
`

### 🗄️ Database-per-Service Isolation Rule
- **Strict Isolation:** Each microservice has its own isolated database instance/schema.
- **Zero Cross-DB Access:** Direct cross-database SQL queries, multi-database transactions, or foreign joins are **strictly forbidden**.
- **API Communication Only:** Services exchange state exclusively through documented JSON REST endpoints.

---

## 🚀 Quick Start & Local Setup

### 1. Prerequisites
- **Node.js**: 18.x or higher
- **npm**: 9.x or higher
- **Postman**: For API testing and evaluation
- **Git**: Configured with your university email/name

### 2. Clone Repository
`ash
git clone https://github.com/YourOrg/IT3130_GroupXX_RideLink.git
cd IT3130_GroupXX_RideLink
`

### 3. Environment Setup
Copy the sample environment file to each service:
`ash
cp services/account-service/.env.example services/account-service/.env
cp services/driver-service/.env.example services/driver-service/.env
cp services/ride-service/.env.example services/ride-service/.env
cp services/payment-service/.env.example services/payment-service/.env
`

### 4. Install Dependencies & Start Services

Open 4 separate terminal windows (or run via concurrency script):

`ash
# Terminal 1: Account Service (Port 5001)
cd services/account-service
npm install
npm run dev

# Terminal 2: Driver & Vehicle Service (Port 5002)
cd services/driver-service
npm install
npm run dev

# Terminal 3: Ride Management Service (Port 5003)
cd services/ride-service
npm install
npm run dev

# Terminal 4: Fare & Payment Service (Port 5004)
cd services/payment-service
npm install
npm run dev
`

---

## 🧪 Automated Unit Testing (Rubric I2: 3 Marks)

Each microservice includes an automated unit test suite with positive and negative test cases:

`ash
# Run tests for Account Service
cd services/account-service && npm test

# Run tests for Driver Service
cd services/driver-service && npm test

# Run tests for Ride Service
cd services/ride-service && npm test

# Run tests for Payment Service
cd services/payment-service && npm test
`

---

## 📮 Postman Collection & Evaluation Workflow (Rubric G3: 3 Marks)

Import the provided Postman collection located in /postman:
1. RideLink_API_Collection.json
2. RideLink_Local_Environment.json

### Recommended Happy Path Execution Order:
1. **Account Service**: Register & Login Passenger (POST /api/v1/auth/register) -> Extracts JWT Token.
2. **Account Service**: Register & Login Driver (POST /api/v1/auth/register).
3. **Driver Service**: Create Driver Profile & Set Status AVAILABLE (PATCH /api/v1/drivers/:id/status).
4. **Driver Service**: Update current GPS location (PATCH /api/v1/drivers/:id/location).
5. **Fare Service**: Estimate Ride Fare (POST /api/v1/fares/estimate).
6. **Ride Service**: Create Ride Request (POST /api/v1/rides).
7. **Ride Service**: Discover & Assign Driver (POST /api/v1/rides/:id/assign).
8. **Ride Service**: Update ride status to IN_PROGRESS -> COMPLETED.
9. **Payment Service**: Process Ride Payment (POST /api/v1/payments/process).
10. **Payment Service**: Get Receipt (GET /api/v1/payments/:id/receipt).

---

## 🌿 Git Branching Strategy (Rubric I3: 3 Marks)

To ensure full individual contribution marks:
- **main**: Production-ready, stable releases only. Protected branch.
- **develop**: Integration branch for combining tested services.
- **Feature Branches**: Each member works strictly on their individual branch:
  - eature/account-auth (Sasiru)
  - eature/driver-management (Randi Sithma)
  - eature/ride-lifecycle (Bhanuka)
  - eature/fare-billing (Nethmini Perera)

### Commit Message Conventions:
- eat: add passenger registration endpoint with bcrypt
- ix: correct driver status transition to OFFLINE
- 	est: add unit tests for ride status validator
- docs: update OpenAPI specification for payment webhook

---

## 📄 License & Academic Integrity
Developed as part of the **IT3130 Application Development** course module at SLIIT. All code submitted is original group work in accordance with the SLIIT Academic Honesty Policy.
