# 🚗 RideLink — Driver & Vehicle Service

The **Driver & Vehicle Service** is an independent microservice in the RideLink platform responsible for managing driver profiles, vehicle registrations, real-time driver availability, live GPS location tracking, and intelligent proximity-based driver dispatch matching.

---

## 📌 Service Overview

| Property | Details |
|---|---|
| **Service Name** | `driver-service` |
| **Port** | `8082` |
| **Gateway Port** | `8080` (routes `/api/drivers/**` and `/api/vehicles/**`) |
| **Framework** | Spring Boot 4.1.x / Java 21 |
| **Database** | MongoDB (Database: `ridelink_driver_db`) |
| **Architecture** | Controller ➔ Service ➔ Repository ➔ MongoDB |
| **Interactive Docs** | Swagger UI: `http://localhost:8082/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8082/v3/api-docs` |

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
                 /api/drivers/**     │     /api/vehicles/**
                                     ▼
                     +───────────────────────────────+
                     |  DRIVER & VEHICLE SERVICE     |
                     |            (:8082)            |
                     +───────────────┬───────────────+
                                     │
                                     ▼
                     +───────────────────────────────+
                     |      MongoDB (localhost:27017)|
                     |      DB: ridelink_driver_db   |
                     |      ├── driver_profiles      |
                     |      └── vehicles             |
                     +───────────────────────────────+
```

---

## 📊 Domain Models

### 1. `DriverProfile` Collection (`driver_profiles`)
| Field | Type | Description |
|---|---|---|
| `id` | `String` | MongoDB Document ID (Primary Key) |
| `accountId` | `String` | Reference ID to the user in `account-service` |
| `licenseNumber` | `String` | Driver driving license number (Unique) |
| `availability` | `Availability` | Enum: `AVAILABLE`, `UNAVAILABLE`, `ON_RIDE` (Default: `UNAVAILABLE`) |
| `serviceArea` | `String` | Operational city/region (e.g. `Colombo`, `Kandy`) |
| `currentLatitude` | `Double` | Current GPS latitude (-90.0 to 90.0) |
| `currentLongitude` | `Double` | Current GPS longitude (-180.0 to 180.0) |
| `rating` | `Double` | Driver star rating (Default: `5.0`) |
| `totalRides` | `Integer` | Total completed rides (Default: `0`) |
| `activeVehicleId` | `String` | ID of the currently assigned vehicle |
| `createdAt` | `LocalDateTime` | Profile creation timestamp |
| `updatedAt` | `LocalDateTime` | Last profile update timestamp |

### 2. `Vehicle` Collection (`vehicles`)
| Field | Type | Description |
|---|---|---|
| `id` | `String` | MongoDB Document ID (Primary Key) |
| `driverId` | `String` | ID of the owner driver profile |
| `make` | `String` | Vehicle manufacturer (e.g., `Toyota`, `Honda`) |
| `model` | `String` | Vehicle model name (e.g., `Prius`, `Civic`) |
| `year` | `Integer` | Manufacturing year (e.g., `2021`) |
| `color` | `String` | Vehicle color (e.g., `Pearl White`) |
| `licensePlate` | `String` | License plate number (Unique, e.g. `WP CAB-1234`) |
| `vehicleType` | `VehicleType` | Enum: `CAR`, `MOTORCYCLE`, `THREE_WHEELER`, `VAN`, `LUXURY` |
| `capacity` | `Integer` | Passenger seating capacity |
| `isActive` | `Boolean` | Whether vehicle is currently active (Default: `true`) |
| `createdAt` | `LocalDateTime` | Registration timestamp |
| `updatedAt` | `LocalDateTime` | Last update timestamp |

---

## 📡 Complete REST API Endpoints

All endpoints are accessible via the **API Gateway (`http://localhost:8080`)** or directly on **Driver Service (`http://localhost:8082`)**.

### 🚗 A. Driver Endpoints (`/api/drivers/**`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/drivers` | Register a new driver profile |
| `GET` | `/api/drivers` | List all drivers (filter by `availability`, `serviceArea`) |
| `GET` | `/api/drivers/{id}` | Get driver profile by Driver ID |
| `GET` | `/api/drivers/account/{accountId}` | Get driver profile by Account Service User ID |
| `PUT` | `/api/drivers/{id}` | Update driver details (license, service area, active vehicle) |
| `PUT` | `/api/drivers/{id}/availability` | Update driver availability (`AVAILABLE`, `UNAVAILABLE`, `ON_RIDE`) |
| `PUT` | `/api/drivers/{id}/location` | Update live GPS coordinates (`latitude`, `longitude`) |
| `GET` | `/api/drivers/eligible` | **Find eligible nearby drivers** (Haversine proximity dispatch) |
| `DELETE` | `/api/drivers/{id}` | Delete driver profile & associated vehicles |

---

### 🚙 B. Vehicle Endpoints (`/api/vehicles/**`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/vehicles` | Register a new vehicle for a driver |
| `GET` | `/api/vehicles` | List all vehicles (filter by `vehicleType`, `activeOnly`) |
| `GET` | `/api/vehicles/{id}` | Get vehicle details by ID |
| `GET` | `/api/vehicles/driver/{driverId}` | Get all vehicles owned by a specific driver |
| `PUT` | `/api/vehicles/{id}` | Update vehicle details |
| `DELETE` | `/api/vehicles/{id}` | Delete vehicle record |

---

## 🧪 Postman Testing Guide & Example Payloads

### 1. Create Driver Profile
```http
POST http://localhost:8080/api/drivers
Content-Type: application/json

{
  "accountId": "6ab4885449dadbf46a7ce771",
  "licenseNumber": "B1234567",
  "serviceArea": "Colombo",
  "currentLatitude": 6.9271,
  "currentLongitude": 79.8612
}
```
**Expected Response (`201 Created`):**
```json
{
  "id": "6ab4900149dadbf46a7ce780",
  "accountId": "6ab4885449dadbf46a7ce771",
  "licenseNumber": "B1234567",
  "availability": "UNAVAILABLE",
  "serviceArea": "Colombo",
  "currentLatitude": 6.9271,
  "currentLongitude": 79.8612,
  "rating": 5.0,
  "totalRides": 0,
  "activeVehicleId": null,
  "createdAt": "2026-09-24T15:30:00",
  "updatedAt": "2026-09-24T15:30:00"
}
```

---

### 2. Register a Vehicle for the Driver
```http
POST http://localhost:8080/api/vehicles
Content-Type: application/json

{
  "driverId": "6ab4900149dadbf46a7ce780",
  "make": "Toyota",
  "model": "Prius",
  "year": 2022,
  "color": "Pearl White",
  "licensePlate": "WP CAB-1234",
  "vehicleType": "CAR",
  "capacity": 4
}
```
**Expected Response (`201 Created`):**
```json
{
  "id": "6ab4905549dadbf46a7ce785",
  "driverId": "6ab4900149dadbf46a7ce780",
  "make": "Toyota",
  "model": "Prius",
  "year": 2022,
  "color": "Pearl White",
  "licensePlate": "WP CAB-1234",
  "vehicleType": "CAR",
  "capacity": 4,
  "isActive": true,
  "createdAt": "2026-09-24T15:32:00",
  "updatedAt": "2026-09-24T15:32:00"
}
```
*(Notice: The driver's `activeVehicleId` is automatically updated to this vehicle).*

---

### 3. Set Driver Availability to `AVAILABLE`
```http
PUT http://localhost:8080/api/drivers/6ab4900149dadbf46a7ce780/availability
Content-Type: application/json

{
  "availability": "AVAILABLE"
}
```
**Expected Response (`200 OK`):**
```json
{
  "id": "6ab4900149dadbf46a7ce780",
  "availability": "AVAILABLE",
  "updatedAt": "2026-09-24T15:35:00"
}
```

---

### 4. Update Driver GPS Location
```http
PUT http://localhost:8080/api/drivers/6ab4900149dadbf46a7ce780/location
Content-Type: application/json

{
  "latitude": 6.9319,
  "longitude": 79.8478
}
```

---

### 5. Find Eligible Nearby Drivers (Dispatch Algorithm)
```http
GET http://localhost:8080/api/drivers/eligible?latitude=6.9271&longitude=79.8612&radiusKm=10.0&vehicleType=CAR
```
**Expected Response (`200 OK`):**
```json
[
  {
    "driverId": "6ab4900149dadbf46a7ce780",
    "accountId": "6ab4885449dadbf46a7ce771",
    "licenseNumber": "B1234567",
    "availability": "AVAILABLE",
    "serviceArea": "Colombo",
    "currentLatitude": 6.9319,
    "currentLongitude": 79.8478,
    "rating": 5.0,
    "totalRides": 0,
    "distanceKm": 1.58,
    "vehicle": {
      "id": "6ab4905549dadbf46a7ce785",
      "make": "Toyota",
      "model": "Prius",
      "licensePlate": "WP CAB-1234",
      "vehicleType": "CAR",
      "capacity": 4
    }
  }
]
```

---

## 📐 Dispatch Proximity Algorithm (Haversine Formula)

The `findEligibleDrivers()` method calculates the spherical distance between the passenger pickup GPS coordinate $(lat_1, lon_1)$ and the driver's live GPS coordinate $(lat_2, lon_2)$:

$$a = \sin^2\left(\frac{\Delta lat}{2}\right) + \cos(lat_1) \cdot \cos(lat_2) \cdot \sin^2\left(\frac{\Delta lon}{2}\right)$$
$$c = 2 \cdot \text{atan2}\left(\sqrt{a}, \sqrt{1-a}\right)$$
$$d = R \cdot c \quad (\text{where } R = 6371\text{ km})$$

**Dispatch Rules:**
1. Only drivers with `availability == AVAILABLE` are evaluated.
2. Drivers outside `maxRadiusKm` (default: `10.0 km`) are filtered out.
3. Drivers are filtered by the requested `VehicleType` (e.g. `CAR`, `VAN`, `THREE_WHEELER`).
4. Results are automatically sorted in **ascending order of distance** (closest driver first).

---

## 🛡️ Error Handling & HTTP Status Codes

| Scenario | HTTP Status | Response Format |
|---|---|---|
| Driver / Vehicle Not Found | `404 Not Found` | `{"status": 404, "error": "Not Found", "message": "..."}` |
| Duplicate License Plate / Account ID | `409 Conflict` | `{"status": 409, "error": "Conflict", "message": "..."}` |
| Validation Errors (Blank fields, invalid ranges) | `400 Bad Request` | `{"status": 400, "error": "Validation Failed", "fieldErrors": {...}}` |
| Invalid Enum Values | `400 Bad Request` | `{"status": 400, "error": "Validation Failed", ...}` |

---

## 💡 Viva Q&A Reference for IT3130

**Q1: How does the Driver Service maintain database isolation?**  
> **A:** Driver Service connects exclusively to `ridelink_driver_db` on port 27017 through a dedicated `MongoDatabaseFactory` configured in `MongoConfig.java`. It has no direct connection or credentials to `account-service`, `ride-service`, or `fare-payment-service` databases.

**Q2: How are Drivers and Vehicles related without SQL Foreign Keys?**  
> **A:** Using document referencing. The `Vehicle` document stores the `driverId`, and the `DriverProfile` document stores the `activeVehicleId`. Relational integrity is maintained at the service layer (`VehicleService` and `DriverProfileService`).

**Q3: How does the Dispatch / Eligible Driver feature work?**  
> **A:** It uses the Haversine formula to compute great-circle distances between passenger pickup coordinates and driver live GPS coordinates, filtering only `AVAILABLE` drivers within the configured search radius and sorting by proximity.
