# 🚗 Parking Lot Management System

A complete Parking Lot Management System designed as a full-stack software project. The system models real-world parking operations including vehicle entry, parking spot allocation, ticket management, vehicle exit, and fare calculation.

The project is built with a **Java backend**, **JUnit 5 testing**, and a **vanilla HTML/CSS/JavaScript frontend**. The architecture is designed to keep the core parking logic independent from the user interface.

### ✨ Live Link: https://parkinglotsystem-7bad.onrender.com/

---
## 📌 Complete Class Diagram
<img width="694" height="607" alt="image" src="https://github.com/user-attachments/assets/de0d878d-ac11-4ed7-8c11-5717f4856220" />

## 📌 Project Overview

The system manages the complete lifecycle of a vehicle:

```text
                    ┌─────────────────────┐
                    │       User          │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      Frontend       │
                    │  HTML / CSS / JS    │
                    └──────────┬──────────┘
                               │
                         HTTP / JSON
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Java Backend     │
                    │                     │
                    │  Parking Lot Logic  │
                    └──────────┬──────────┘
                               │
             ┌─────────────────┼─────────────────┐
             ▼                 ▼                 ▼
        Vehicle Manager   Parking Manager   Fare Calculator
             │                 │                 │
             ▼                 ▼                 ▼
         Vehicles         Parking Spots       Strategies
                               │
                               ▼
                            Ticket
```

---

## ✨ Features

### 🚘 Vehicle Management

The system supports different vehicle types:

- Motorcycle
- Car
- Truck

Each vehicle contains:

- License plate
- Vehicle size
- Vehicle-specific characteristics

### 🅿️ Parking Spot Management

The system supports different parking spot types:

- Compact Spot
- Regular Spot
- Handicapped Spot
- Oversized Spot

The `ParkingManager` finds an appropriate available spot based on vehicle size.

### 🎫 Ticket Management

When a vehicle enters the parking lot:

1. Vehicle is registered.
2. A suitable parking spot is found.
3. The vehicle is assigned to the spot.
4. A parking ticket is generated.
5. Entry time is recorded.

When a vehicle exits:

1. Ticket is validated.
2. Exit time is recorded.
3. Parking duration is calculated.
4. Fare is calculated.
5. Parking spot is released.

### 💰 Fare Calculation

Fare calculation is implemented using the **Strategy Design Pattern**.

Current strategies:

- Base Fare Strategy
- Peak Hours Fare Strategy

Vehicle rates:

| Vehicle | Size | Base Rate |
|---|---|---:|
| Motorcycle | Small | 1.0 |
| Car | Medium | 2.0 |
| Truck | Large | 3.0 |

Peak hours apply a `1.5x` multiplier.

### 🌐 Web Frontend

The frontend is intentionally built without a frontend framework.

Technologies:

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

The frontend can provide:

- Vehicle entry form
- Vehicle type selection
- Parking spot visualization
- Ticket information
- Vehicle exit form
- Fare display
- Parking availability

### 🧪 Automated Testing

JUnit 5 is used to test the parking system.

The vehicle journey test verifies:

- Vehicle entry
- Ticket creation
- Parking spot allocation
- Vehicle lookup
- Vehicle exit
- Exit time
- Parking spot release

---

# 🏗️ Architecture

The project follows a separation-of-concerns approach.

```text
┌──────────────────────────────────────────────────────────┐
│                      Presentation                        │
│                                                          │
│                  HTML + CSS + JavaScript                 │
└───────────────────────────┬──────────────────────────────┘
                            │
                            │ HTTP / JSON
                            ▼
┌──────────────────────────────────────────────────────────┐
│                         API Layer                        │
│                                                          │
│                  REST / HTTP Endpoints                   │
└───────────────────────────┬──────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────┐
│                     Business Layer                       │
│                                                          │
│  ParkingLot │ ParkingManager │ FareCalculator │ Ticket   │
└───────────────────────────┬──────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────┐
│                       Domain Layer                       │
│                                                          │
│     Vehicle │ ParkingSpot │ VehicleSize │ FareStrategy   │
└──────────────────────────────────────────────────────────┘
```

This separation makes it possible to change the frontend without rewriting the parking-domain logic.

---

# 📁 Project Structure

```text
ParkingLotSystem/
│
├── Dockerfile
├── .dockerignore
│
├── backend/
│   └── src/
│       └── main/
│           └── java/
│               └── parkingLot/
│                   │
│                   ├── ParkingLot.java
│                   ├── Ticket.java
│                   │
│                   ├── fare/
│                   │   ├── FareStrategy.java
│                   │   ├── FareCalculator.java
│                   │   ├── BaseFareStrategy.java
│                   │   └── PeakHoursFareStrategy.java
│                   │
│                   ├── spot/
│                   │   ├── ParkingSpot.java
│                   │   ├── ParkingManager.java
│                   │   ├── CompactSpot.java
│                   │   ├── RegularSpot.java
│                   │   ├── HandicappedSpot.java
│                   │   └── OversizedSpot.java
│                   │
│                   └── vehicle/
│                       ├── Vehicle.java
│                       ├── VehicleSize.java
│                       ├── Motorcycle.java
│                       ├── Car.java
│                       └── Truck.java
│
├── server/
│   └── parkingLot/
│       └── server/
│           └── ParkingServer.java
│
└── frontend/
    ├── index.html
    ├── css/
    │   └── style.css
    └── js/
        └── app.js
```

---

# 🧠 Core Domain Model

## Vehicle

```text
              Vehicle
                 │
       ┌─────────┼─────────┐
       │         │         │
       ▼         ▼         ▼
  Motorcycle    Car      Truck
    SMALL      MEDIUM     LARGE
```

The common `Vehicle` abstraction allows the parking system to work with different vehicle types without coupling the system to a particular implementation.

---

## Parking Spot

```text
                   ParkingSpot
                        │
        ┌───────────────┼────────────────┐
        │               │                │
        ▼               ▼                ▼
 CompactSpot       RegularSpot      OversizedSpot
    SMALL            MEDIUM            LARGE
```

A parking spot maintains:

- Spot number
- Current vehicle
- Availability
- Supported vehicle size

---

# 🎯 Design Principles

The project demonstrates several important software engineering concepts.

## Encapsulation

Internal state is kept private and exposed through controlled methods.

Example:

```java
private Vehicle vehicle;
```

## Abstraction

Interfaces define common behavior.

Example:

```java
public interface Vehicle {
    String getLicensePlate();
    VehicleSize getSize();
}
```

## Polymorphism

Different vehicle implementations can be treated as a common `Vehicle`.

```java
Vehicle vehicle;
```

This allows the parking manager to work with motorcycles, cars, and trucks through the same abstraction.

## Separation of Concerns

Different classes have focused responsibilities:

| Component | Responsibility |
|---|---|
| `ParkingLot` | Coordinates the parking workflow |
| `ParkingManager` | Manages parking spots and vehicle allocation |
| `ParkingSpot` | Represents a physical parking space |
| `Vehicle` | Represents a vehicle |
| `Ticket` | Represents a parking session |
| `FareCalculator` | Calculates final parking fare |
| `FareStrategy` | Defines a fare calculation strategy |

---

# 🧩 Design Patterns

## Strategy Pattern

Fare calculation uses the Strategy Pattern.

```text
                 FareStrategy
                      │
             ┌────────┴────────┐
             │                 │
             ▼                 ▼
     BaseFareStrategy   PeakHoursFareStrategy
```

The `FareCalculator` can execute multiple strategies sequentially.

This makes it easy to introduce new pricing rules without heavily modifying existing code.

---

# 🔄 Vehicle Lifecycle

## Vehicle Entry

```text
Vehicle
   │
   ▼
ParkingLot.enterVehicle()
   │
   ▼
ParkingManager.findSpotForVehicle()
   │
   ▼
Available ParkingSpot
   │
   ▼
ParkingManager.parkVehicle()
   │
   ▼
Ticket Created
```

## Vehicle Exit

```text
Ticket
   │
   ▼
ParkingLot.leaveVehicle()
   │
   ▼
Record Exit Time
   │
   ▼
Calculate Parking Duration
   │
   ▼
FareCalculator
   │
   ├── Base Fare
   └── Peak Hours Adjustment
   │
   ▼
Release Parking Spot
```

---

# 🌐 Frontend

The frontend uses only browser-native technologies.

```text
HTML
 │
 ├── Structure
 │
CSS
 │
 ├── Layout
 ├── Styling
 └── Responsive Design
 │
JavaScript
 │
 ├── User Interaction
 ├── Form Validation
 ├── API Requests
 └── Dynamic UI Updates
```

No frontend framework is required.

### Example API request

```javascript
fetch("/api/parking/enter", {
    method: "POST",
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify({
        licensePlate: "ABC123",
        vehicleType: "CAR"
    })
});
```

---

# 🔌 API Design

The application can expose endpoints such as:

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/parking/enter` | Park a vehicle |
| `POST` | `/api/parking/exit` | Remove a vehicle and calculate fare |
| `GET` | `/api/parking/spots` | Retrieve parking spot status |
| `GET` | `/api/parking/vehicles` | Retrieve currently parked vehicles |

### Enter Vehicle

```http
POST /api/parking/enter
Content-Type: application/json
```

```json
{
    "licensePlate": "ABC123",
    "vehicleType": "CAR"
}
```

Example response:

```json
{
    "ticketId": "T001",
    "licensePlate": "ABC123",
    "vehicleType": "CAR",
    "spotNumber": 1
}
```

---

# 🧪 Testing

JUnit 5 is used for automated testing.

The primary vehicle journey test covers:

```text
Create Vehicle
      ↓
Enter Parking Lot
      ↓
Generate Ticket
      ↓
Assign Parking Spot
      ↓
Find Vehicle
      ↓
Exit Parking Lot
      ↓
Record Exit Time
      ↓
Release Parking Spot
```

Run tests from IntelliJ:

```text
Right Click ParkingLotTest.java
        ↓
Run 'ParkingLotTest'
```

---

# 📊 Example User Flow

### 1. User opens the dashboard

```text
Parking Lot Management System
```

### 2. User enters:

```text
License Plate: ABC123
Vehicle Type: Car
```

### 3. Frontend sends request

```text
POST /api/parking/enter
```

### 4. Backend finds a suitable spot

```text
Car → MEDIUM
MEDIUM → Regular Spot #1
```

### 5. Ticket is generated

```text
Ticket ID: T001
Vehicle: ABC123
Spot: 1
```

### 6. User exits

The user provides the ticket.

The backend:

```text
Calculates duration
        ↓
Calculates base fare
        ↓
Applies peak-hour strategy if applicable
        ↓
Returns final fare
        ↓
Releases parking spot
```

---

# 🛡️ Error Handling

The application should handle situations such as:

- No parking spot available
- Invalid vehicle type
- Invalid license plate
- Invalid ticket
- Vehicle already parked
- Vehicle not found
- Attempt to leave an empty parking spot
- Invalid API request

A production implementation should return appropriate HTTP status codes such as:

```text
200 OK
201 Created
400 Bad Request
404 Not Found
409 Conflict
500 Internal Server Error
```

---

# 🛠️ Technology Stack

## Backend

- Java
- Object-Oriented Programming
- Collections Framework
- Java Time API
- `BigDecimal`

## Testing

- JUnit 5

## Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

## Development

- IntelliJ IDEA
- Git
- GitHub

---

# 📚 What This Project Demonstrates

This project is intended to demonstrate practical understanding of:

- Object-Oriented Programming
- SOLID principles
- Interfaces and abstraction
- Encapsulation
- Polymorphism
- Java Collections
- Design Patterns
- Strategy Pattern
- Clean separation of responsibilities
- Unit testing
- REST API architecture
- HTTP and JSON
- Frontend/backend communication
- Vanilla JavaScript
- Software architecture
- Git and version control

---

## ⭐ Project Status

**Status:** 🚧 In Development

The project is being developed incrementally, starting from the core Java parking domain and extending toward a complete full-stack parking management application.

---

⭐ If you find this project useful, consider giving the repository a star.
