# 🚗 RideLink · Backend Microservices for On-Demand Ride-Sharing

[![Course](https://img.shields.io/badge/Course-IT3130%20Application%20Development-blue.svg)](https://courseweb.sliit.lk)
[![Architecture](https://img.shields.io/badge/Architecture-Microservices-orange.svg)](#system-architecture)
[![Database](https://img.shields.io/badge/Pattern-Database--per--Service-green.svg)](#database-isolation)
[![Target Deadline](https://img.shields.io/badge/Deadline-Oct%2001%2C%202026-red.svg)](#project-schedule)

**RideLink** is a distributed backend platform for a ride-sharing ecosystem. It is composed of four loosely coupled, independently deployable microservices communicating via lightweight synchronous REST APIs, strictly adhering to the **Database-per-Service** architectural pattern.

---

## 👥 Microservice Allocation & Team Ownership

Each team member is exclusively responsible for the design, implementation, automated unit testing, and documentation of one independent microservice:

| # | Microservice | Port | Owner & Student ID | Key Responsibilities |
|---|---|---|---|---|
| **1** | **Account Service** | `5001` | **Sasiru** (`IT24200741`) | Passenger & Driver registration, BCrypt password hashing, JWT token authentication, Role-based access control (Passenger, Driver, Admin), profile management. |
| **2** | **Driver & Vehicle Service** | `5002` | **Randi Sithma** (`IT24104341`) | Driver operational profiles, vehicle registration, Availability status toggle (`AVAILABLE`, `BUSY`, `OFFLINE`), simulated GPS tracking, driver discovery query. |
| **3** | **Ride Management Service** | `5003` | **Bhanuka** (`IT24103298`) | Ride lifecycle orchestrator (`REQUESTED` -> `ACCEPTED` -> `IN_PROGRESS` -> `COMPLETED`), dispatching to Driver Service, status state machine validation. |
| **4** | **Fare & Payment Service** | `5004` | **Nethmini Perera** (`IT24104027`) | Fare estimation algorithm (Base + Distance + Time), final fare calculation upon ride completion, payment simulation (Success/Failed), digital receipt generation. |

---

## 🏛️ System Architecture & Service Interactions

```mermaid
flowchart TD
    Client["Client / Postman / Swagger UI"]
    
    subgraph S1["1. Account Service (Port 5001)"]
        Sasiru["Sasiru (IT24200741)"]
        DB1[("Users DB")]
    end
    
    subgraph S3["3. Ride Management Service (Port 5003)"]
        Bhanuka["Bhanuka (IT24103298)"]
        DB3[("Rides DB")]
    end
    
    subgraph S2["2. Driver and Vehicle Service (Port 5002)"]
        Randi["Randi Sithma (IT24104341)"]
        DB2[("Drivers DB")]
    end
    
    subgraph S4["4. Fare and Payment Service (Port 5004)"]
        Nethmini["Nethmini Perera (IT24104027)"]
        DB4[("Payments DB")]
    end

    Client -->|REST API| S1
    Client -->|REST API| S3
    Client -->|REST API| S4
    
    S3 -->|1. Query and Assign Driver| S2
    S3 -->|2. Trigger Final Payment| S4
```

### 🗄️ Database-per-Service Isolation Rule
- **Strict Isolation:** Each microservice has its own isolated database instance/schema.
- **Zero Cross-DB Access:** Direct cross-database SQL queries, multi-database transactions, or foreign joins are **strictly forbidden**.
- **API Communication Only:** Services exchange state exclusively through documented JSON REST endpoints.

---

## 🚀 Quick Start & Local Setup

### 1. Prerequisites
- **Node.js**: `>= 18.x` (Version 18 or higher)
- **npm**: `>= 9.x` (Version 9 or higher)
- **Postman**: For API testing and demonstration
- **Git**: Configured with your SLIIT university email and ID

### 2. Clone Repository
```bash
git clone https://github.com/IT24200741/IT3130_RideLink_Microservices.git
cd IT3130_RideLink_Microservices
```

### 3. Environment Setup
Copy the sample environment file to each service:
```bash
cp services/account-service/.env.example services/account-service/.env
cp services/driver-service/.env.example services/driver-service/.env
cp services/ride-service/.env.example services/ride-service/.env
cp services/payment-service/.env.example services/payment-service/.env
```

### 4. Install Dependencies & Start Services

Open 4 separate terminal windows:

```bash
# Terminal 1: Account Service (Port 5001 - Sasiru IT24200741)
cd services/account-service
npm install
npm run dev

# Terminal 2: Driver & Vehicle Service (Port 5002 - Randi Sithma IT24104341)
cd services/driver-service
npm install
npm run dev

# Terminal 3: Ride Management Service (Port 5003 - Bhanuka IT24103298)
cd services/ride-service
npm install
npm run dev

# Terminal 4: Fare & Payment Service (Port 5004 - Nethmini Perera IT24104027)
cd services/payment-service
npm install
npm run dev
```

---

## 🧪 Automated Unit Testing (Rubric I2: 3 Marks)

Each microservice includes an automated unit test suite with positive and negative test cases:

```bash
# Run tests for Account Service (Sasiru IT24200741)
cd services/account-service && npm test

# Run tests for Driver Service (Randi Sithma IT24104341)
cd services/driver-service && npm test

# Run tests for Ride Service (Bhanuka IT24103298)
cd services/ride-service && npm test

# Run tests for Payment Service (Nethmini Perera IT24104027)
cd services/payment-service && npm test
```

---

## 📮 Postman Collection & Evaluation Workflow (Rubric G3: 3 Marks)

Import the provided Postman collection located in `/postman`:
1. `RideLink_API_Collection.json`
2. `RideLink_Local_Environment.json`

### Recommended Happy Path Execution Order:
1. **Account Service**: Register & Login Passenger (`POST /api/v1/auth/register`) -> Extracts JWT Token.
2. **Account Service**: Register & Login Driver (`POST /api/v1/auth/register`).
3. **Driver Service**: Create Driver Profile & Set Status `AVAILABLE` (`PATCH /api/v1/drivers/:id/status`).
4. **Driver Service**: Update current GPS location (`PATCH /api/v1/drivers/:id/location`).
5. **Fare Service**: Estimate Ride Fare (`POST /api/v1/fares/estimate`).
6. **Ride Service**: Create Ride Request (`POST /api/v1/rides`).
7. **Ride Service**: Discover & Assign Driver (`POST /api/v1/rides/:id/assign`).
8. **Ride Service**: Update ride status to `IN_PROGRESS` -> `COMPLETED`.
9. **Payment Service**: Process Ride Payment (`POST /api/v1/payments/process`).
10. **Payment Service**: Get Receipt (`GET /api/v1/payments/:id/receipt`).

---

## 🌿 Git Branching Strategy (Rubric I3: 3 Marks)

To ensure full individual contribution marks:
- **`main`**: Production-ready, stable releases only. Protected branch.
- **`develop`**: Integration branch for combining tested services.
- **Feature Branches**: Each member works strictly on their individual branch:
  - `feature/account-service` (**Sasiru** - `IT24200741`)
  - `feature/driver-service` (**Randi Sithma** - `IT24104341`)
  - `feature/ride-service` (**Bhanuka** - `IT24103298`)
  - `feature/payment-service` (**Nethmini Perera** - `IT24104027`)

### Commit Message Conventions:
- `feat(account): add passenger registration endpoint with bcrypt`
- `fix(driver): correct driver status transition to OFFLINE`
- `test(ride): add unit tests for ride status state machine`
- `docs(payment): update OpenAPI specification for payment receipt`

---

## 📄 License & Academic Integrity
Developed as part of the **IT3130 Application Development** course module at SLIIT. All code submitted is original group work in accordance with the SLIIT Academic Honesty Policy.
