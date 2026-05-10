# Train Ticketing System API

A Spring Boot backend application for managing train schedules, searching for routes, and booking tickets. The system features automated overbooking prevention, simulated email notifications, and comprehensive administrator controls for managing the rail network.

## 🚀 Key Features & Implementation Details

* **🔍 Advanced Route Search:** Finds valid connections between stations, including direct routes and complex multi-train changeovers.
  * *Implementation:* Logic located in `RouteService.java` using a graph-traversal approach.
* **🎟️ Smart Booking System:** Prevents overbooking by dynamically checking available capacity in real-time before saving reservations.
  * *Implementation:* Capacity validation logic found in `BookingService.java`.
* **📧 Email Notifications:** Simulates email confirmations and delay alerts.
  * *Implementation:* Managed by `EmailService.java`, triggered by actions in `BookingService.java` and `TrainService.java`.
* **Admin Dashboard API:** Full CRUD operations for trains, routes, and stations, plus delay management and passenger manifests.
  * *Implementation:* See `StationService.java`, `TrainService.java` and RouteService.java`.
* **🛡️ Role-Based Security:** Protects admin endpoints via a custom interceptor.
  * *Implementation:* See `AuthInterceptor.java` and `WebConfig.java`.

## 🛠 Tech Stack

* **Framework:** Spring Boot 3.1.5 (Java 17)
* **Build Tool:** Gradle (Groovy DSL)
* **Data Access:** Spring Data JPA / Hibernate
* **Database:** MySQL
* **Tooling:** MapStruct (for DTO mapping), Lombok

---

## 🗄️ Database Structure

The application uses a relational database model designed to handle complex route scheduling and capacity management.

| Table | Description | Key Columns |
| :--- | :--- | :--- |
| **`users`** | Application users (customers and admins). | `id`, `name`, `email`, `password`, `role`  |
| **`stations`** | Physical train stations. | `id`, `name`, `city` |
| **`routes`** | A named corridor (e.g. "Budapest - Debrecen"). | `id`, `name`, `description` |
| **`route_stations`** | Junction table tracking the order of stations on a route and the travel time. |`id`, `route_id`, `station_id`, `stopOrder`, `minutesFromOrigin`  |
| **`trains`** | A physical train assigned to a route. | `id`, `trainNumber`, `route_id`, `departureTime`, `totalSeats`, `delayMinutes`  |
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
### ✅ Booking Confirmation Email Example

<img width="1525" height="642" alt="image" src="https://github.com/user-attachments/assets/650ff052-5f23-47b4-9cef-c086d05ed985" />
<img width="718" height="926" alt="image" src="https://github.com/user-attachments/assets/d55cadea-1690-41dc-88d9-ba8057f40bbe" />

### ⚠️ Train Delay Alert Notification

<img width="1497" height="581" alt="image" src="https://github.com/user-attachments/assets/f175dd16-8eba-4a66-aebf-1adb3cb4d71b" />
<img width="757" height="613" alt="image" src="https://github.com/user-attachments/assets/b45e01fa-f1d6-4941-90bf-9b0149d9614c" />

---


## ⚙️ Setup & Installation

1. **Clone the repository:**
```bash
git clone https://github.com/JakabGergo/train-ticketing.git

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


---

## 🛠️ Future Improvements & Roadmap

While the core logic of the system is fully functional, the following features are planned for future releases to make the system production-ready and scalable:

### 🔐 1. Secure User Management
* **User Registration Endpoint:** Implementation of a public API for new users to create accounts.
* **Password Hashing:** Integration of **BCrypt** (Spring Security) to ensure that user passwords are never stored in plain text, protecting against data breaches.
* **JWT Authentication:** Moving away from header-based IDs to a stateless **JSON Web Token** system for improved security.

### 📊 2. API Scalability & Pagination
* **Resource Pagination:** Currently, the system returns all records at once. Future updates will implement `Pageable` in Spring Data JPA for endpoints like `GET /api/admin/trains` and `GET /api/admin/routes`.
* **Efficient Loading:** This ensures the application remains fast and responsive even as the database grows to thousands of trains and bookings.

### 🚆 3. Advanced Vehicle & Train Modeling
* **Vehicle Inventory:** Expanding the `trains` table to link with a `vehicles` or `carriages` table. This will allow the system to:
    * Store specific data for each wagon (e.g., Year of manufacture, Model, Maintenance history).
    * Define seat maps per vehicle (Window vs. Aisle seats).
    * Distinguish between different vehicle types (First Class, Economy, Dining Car, Sleeper).
* **Dynamic Capacity:** Automatically calculating a train's `total_seats` based on the sum of the seats in its attached vehicles.

---
