# RideLink Â· Inter-Service API Contracts & Specifications

This document defines the official synchronous REST API contracts between RideLink's 4 microservices. All services must strictly adhere to these JSON schemas and HTTP response codes.

---

## 1. Account Service (Port: 8080 Â· Owner: Sasiru Â· IT24200741)

### 1.1 Register User
- **Method:** `POST`
- **Path:** `/api/v1/auth/register`
- **Request Body:**
```json
{
  "name": "Kamal Perera",
  "email": "kamal@example.com",
  "password": "SecurePass123!",
  "phone": "+94771234567",
  "role": "PASSENGER"
}
```
- **Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": "usr_abc123",
    "name": "Kamal Perera",
    "email": "kamal@example.com",
    "role": "PASSENGER",
    "createdAt": "2026-09-15T10:00:00Z"
  }
}
```

### 1.2 Login & Token Generation
- **Method:** `POST`
- **Path:** `/api/v1/auth/login`
- **Request Body:**
```json
{
  "email": "kamal@example.com",
  "password": "SecurePass123!"
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "USR-1",
    "role": "PASSENGER",
    "expiresIn": 86400
  }
}
```

### 1.3 View User Profile
- **Method:** `GET`
- **Path:** `/api/v1/users/:id`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": "USR-1",
    "name": "Kamal Perera",
    "email": "kamal@example.com",
    "phone": "+94771234567",
    "role": "PASSENGER",
    "status": "ACTIVE",
    "createdAt": "2026-09-15T10:00:00"
  }
}
```

### 1.4 Update User Profile
- **Method:** `PUT`
- **Path:** `/api/v1/users/:id`
- **Request Body:**
```json
{
  "name": "Kamal Perera Updated",
  "phone": "+94779876543"
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": "USR-1",
    "name": "Kamal Perera Updated",
    "email": "kamal@example.com",
    "phone": "+94779876543",
    "role": "PASSENGER",
    "status": "ACTIVE",
    "createdAt": "2026-09-15T10:00:00"
  }
}
```

### 1.5 Account Status Management (Activate / Suspend)
- **Method:** `PATCH`
- **Path:** `/api/v1/users/:id/status`
- **Request Body:**
```json
{
  "status": "SUSPENDED"
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Account status updated successfully",
  "data": {
    "userId": "USR-1",
    "name": "Kamal Perera",
    "email": "kamal@example.com",
    "role": "PASSENGER",
    "status": "SUSPENDED"
  }
}
```

---

## 2. Driver & Vehicle Service (Port: 8081 Â· Owner: Randi Sithma Â· IT24104341)

### 2.1 Update Driver Status
- **Method:** `PATCH`
- **Path:** `/api/v1/drivers/:driverId/status`
- **Request Body:**
```json
{
  "status": "AVAILABLE"
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "driverId": "drv_xyz789",
    "status": "AVAILABLE",
    "updatedAt": "2026-09-15T10:05:00Z"
  }
}
```

### 2.2 Query Eligible Drivers for Ride Dispatch
- **Method:** `POST`
- **Path:** `/api/v1/drivers/query-eligible`
- **Request Body:**
```json
{
  "pickupLat": 6.9271,
  "pickupLng": 79.8612,
  "vehicleType": "CAR",
  "maxRadiusKm": 5.0
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "count": 1,
  "data": [
    {
      "driverId": "drv_xyz789",
      "driverName": "Sunil Silva",
      "phone": "+94719876543",
      "vehicle": {
        "model": "Toyota Aqua",
        "licensePlate": "WP-CAD-1234",
        "color": "White"
      },
      "currentLocation": {
        "lat": 6.9280,
        "lng": 79.8620
      },
      "distanceKm": 0.8
    }
  ]
}
```

---

## 3. Ride Management Service (Port: 8082 Â· Owner: Bhanuka Â· IT24103298)

### 3.1 Request a Ride
- **Method:** `POST`
- **Path:** `/api/v1/rides`
- **Request Body:**
```json
{
  "passengerId": "usr_abc123",
  "pickupLocation": {
    "name": "SLIIT Malabe Campus",
    "lat": 6.9147,
    "lng": 79.9729
  },
  "destinationLocation": {
    "name": "Kaduwela Town",
    "lat": 6.9344,
    "lng": 79.9840
  },
  "vehicleType": "CAR"
}
```
- **Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "rideId": "ride_998877",
    "passengerId": "usr_abc123",
    "status": "REQUESTED",
    "estimatedDistanceKm": 3.5,
    "createdAt": "2026-09-15T10:10:00Z"
  }
}
```

### 3.2 Update Ride Status (State Machine)
- **Method:** `PATCH`
- **Path:** `/api/v1/rides/:rideId/status`
- **Request Body:**
```json
{
  "status": "COMPLETED"
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "rideId": "ride_998877",
    "status": "COMPLETED",
    "completedAt": "2026-09-15T10:35:00Z"
  }
}
```

---

## 4. Fare & Payment Service (Port: 8083 Â· Owner: Nethmini Perera Â· IT24104027)

### 4.1 Estimate Fare
- **Method:** `POST`
- **Path:** `/api/v1/fares/estimate`
- **Request Body:**
```json
{
  "distanceKm": 3.5,
  "estimatedMinutes": 15
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "currency": "LKR",
    "baseFare": 150.00,
    "distanceFare": 280.00,
    "timeFare": 75.00,
    "estimatedTotal": 505.00
  }
}
```

### 4.2 Process Payment
- **Method:** `POST`
- **Path:** `/api/v1/payments/process`
- **Request Body:**
```json
{
  "rideId": "ride_998877",
  "passengerId": "usr_abc123",
  "amount": 505.00,
  "currency": "LKR",
  "paymentMethod": "CARD",
  "simulateFailure": false
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "paymentId": "pay_445566",
    "rideId": "ride_998877",
    "amount": 505.00,
    "status": "SUCCESSFUL",
    "receiptUrl": "/api/v1/payments/pay_445566/receipt",
    "processedAt": "2026-09-15T10:36:00Z"
  }
}
```
