# Train Ticketing System API

A robust Spring Boot backend application for managing train schedules, searching for routes, and booking tickets. The system features automated overbooking prevention, simulated email notifications, and comprehensive administrator controls for managing the rail network.

## 🚀 Key Features

* **Advanced Route Search:** Finds valid connections between stations (handles both direct routes and changeovers).
* **Smart Booking System:** Prevents overbooking by dynamically checking available capacity before saving reservations.
* **Email Notifications:** Simulates email confirmations for successful bookings and alerts passengers automatically if their train is delayed.
* **Admin Dashboard API:** Full CRUD operations for trains, routes, and stations, plus delay management and passenger manifests.
* **Custom Role-Based Security:** Protects admin endpoints using a custom interceptor requiring an `X-User-Id` header for users with the `ADMIN` role.

## 🛠 Tech Stack

* **Framework:** Spring Boot 3.1.5 (Java 17)
* **Build Tool:** Gradle (Groovy DSL)
* **Data Access:** Spring Data JPA / Hibernate
* **Database:** MySQL (Production) / H2 (Testing)
* **Tooling:** MapStruct (for DTO mapping), Lombok

---

## 🗄️ Database Structure

The application uses a relational database model designed to handle complex route scheduling and capacity management.

| Table | Description | Key Columns |
| :--- | :--- | :--- |
| **`users`** | Application users (customers and admins). | [cite_start]`id`, `name`, `email`, `password`, `role`  |
| **`stations`** | Physical train stations. | [cite_start]`id`, `name`, `city` |
| **`routes`** | A named corridor (e.g. "Budapest - Debrecen"). | [cite_start]`id`, `name`, `description` |
| **`route_stations`** | Junction table tracking the order of stations on a route and the travel time. |`id`, `route_id`, `station_id`, `stopOrder`, `minutesFromOrigin`  |
| **`trains`** | A physical train assigned to a route. | [cite_start]`id`, `trainNumber`, `route_id`, `departureTime`, `totalSeats`, `delayMinutes`  |
| **`bookings`** | Ticket reservations linking a user to a train segment. |`id`, `user_id`, `train_id`, `from_station_id`, `to_station_id`, `numberOfSeats`, `status` |

---

## 🔐 Security & Authentication

Admin endpoints (`/api/admin/**`) are protected by a custom `AuthInterceptor`. To access them, you must pass the ID of a user with the `ADMIN` role in the request header.

* **Header Key:** `X-User-Id`
* **Header Value:** `1` *(Assuming User ID 1 is an admin)*

*Note: For testing, you can insert an admin directly into your database:*
```sql
INSERT INTO users (name, email, password, role) 
VALUES ('Admin User', 'admin@test.com', 'password123', 'ADMIN'); 

```

---

## 🚦 API Documentation & Examples

### 1. Public Operations (Customers)

#### 🔍 1.1 Search Possible Routes

Finds valid paths between two stations. It calculates arrival times dynamically based on stop durations and travel times from the origin.

* **Endpoint:** `GET /api/routes?from={stationId}&to={stationId}`
* **Method:** `GET`

**Example Response (`SearchResponseDTO`):**

```json
[
  {
    "legs": [
      {
        "trainId": 1,
        "fromStationName": "Budapest Keleti",
        "toStationName": "Debrecen",
        "departureTime": "2026-05-10T08:00:00",
        "arrivalTime": "2026-05-10T10:00:00"
      }
    ],
    "isDirect": true,
    "totalDurationMinutes": 120
  }
]

```

#### 🎟️ 1.2 Book Tickets

Allows a user to book one or multiple seats. The system prevents overbooking and triggers a confirmation email upon success.

* **Endpoint:** `POST /api/bookings`
* **Method:** `POST`

**Request Body:**

```json
{
  "userId": 2,
  "trainId": 1,
  "fromStationId": 1,
  "toStationId": 3,
  "numberOfSeats": 2
}

```

---

### 2. Administrator Operations

> **🔒 Required Header:** `X-User-Id` (The ID of a user with the `ADMIN` role).

#### 🚉 2.1 Manage Stations

* **Create Station:** `POST /api/admin/stations`
* **Update Station:** `PUT /api/admin/stations/{id}`
* **List All Stations:** `GET /api/admin/stations`
* **Delete Station:** `DELETE /api/admin/stations/{id}`

#### 🛤️ 2.2 Manage Routes

Define travel paths, stop orders, and durations (minutes from the origin station).

* **Create Route:** `POST /api/admin/routes`

```json
{
  "name": "Budapest - Debrecen Express",
  "description": "Main eastern route",
  "stations": [
    { "stationId": 1, "stopOrder": 0, "minutesFromOrigin": 0 },
    { "stationId": 2, "stopOrder": 1, "minutesFromOrigin": 45 }
  ]
}

```

#### 🚆 2.3 Manage Trains

Assign physical trains to specific routes with seat capacities.

* **Create Train:** `POST /api/admin/trains`

```json
{
  "trainNumber": "IC-101",
  "departureTime": "08:00",
  "totalSeats": 200,
  "routeId": 1
}

```

#### ⚠️ 2.4 Set Train Delays

Updates the delay status and **automatically notifies** all confirmed passengers via email.

* **Endpoint:** `PATCH /api/admin/trains/{id}/delay`
* **Method:** `PATCH`
* **Request Body:**

```json
{
  "delayMinutes": 30
}

```

#### 📋 2.5 View Passenger Manifest

View all bookings made for a specific train to track occupancy and passenger details.

* **Endpoint:** `GET /api/admin/trains/{id}/bookings`

**Example Response:**

```json
[
  {
    "id": 101,
    "userName": "John Doe",
    "userEmail": "john.doe@example.com",
    "trainNumber": "IC-101",
    "fromStation": "Budapest Keleti",
    "toStation": "Debrecen",
    "numberOfSeats": 2,
    "status": "CONFIRMED",
    "createdAt": "2026-05-10T14:30:00"
  }
]

```

---
Email send

<img width="1525" height="642" alt="image" src="https://github.com/user-attachments/assets/650ff052-5f23-47b4-9cef-c086d05ed985" />
<img width="718" height="926" alt="image" src="https://github.com/user-attachments/assets/d55cadea-1690-41dc-88d9-ba8057f40bbe" />


---


## ⚙️ Setup & Installation

1. **Clone the repository:**
```bash
git clone <your-repo-url>

```


2. **Configure Environment:** Create a `.env` file or update `src/main/resources/application.properties`:
* `DB_USERNAME=root`
* `DB_PASSWORD=yourpassword`
* `MAIL_USERNAME=yourgmail@gmail.com`
* `MAIL_PASSWORD=your_app_password`


3. **Run the application:**
```bash
./gradlew bootRun

```


4. **Test with Postman:** Remember to include the `X-User-Id` header for all requests under the `/api/admin/` path to simulate admin authentication.

```

```

