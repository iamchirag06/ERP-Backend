# 🎓 ERP-Backend

A robust, production-ready **Educational ERP (Enterprise Resource Planning) Backend** built with **Spring Boot 4.0**, designed for managing all core academic and operational workflows in an educational institution.

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Docker Setup](#docker-setup)
- [Environment Variables](#-environment-variables)
- [API Endpoints](#-api-endpoints)
- [Security](#-security)
- [Contributing](#-contributing)

---

## 🌟 Overview

The **ERP-Backend** is the server-side engine for an education management platform. It serves three primary user roles — **Admin**, **Teacher**, and **Student** — and orchestrates everything from authentication and attendance tracking to assignment management, doubt resolution, notifications, and file uploads.

> Built on **Spring Boot 4.0.2** with **Java 17**, backed by **PostgreSQL**, secured with **JWT + Spring Security**, and containerized with **Docker**.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.2 |
| ORM | Spring Data JPA (Hibernate) |
| Database | PostgreSQL |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| OAuth2 | Spring OAuth2 Client (Google Login) |
| File Storage | Cloudinary |
| Mailing | Spring Boot Mail |
| Validation | Spring Boot Validation |
| API Docs | SpringDoc OpenAPI (Scalar UI) |
| Build Tool | Maven |
| Containerization | Docker (multi-stage build) |
| Utilities | Lombok |

---

## 🏗️ Architecture

The project follows a clean, layered **MVC architecture**:

```
Request → Controller → Service → Repository → Database
                ↓
           DTOs / Models
                ↓
           Security (JWT Filter)
```

### Package Structure (Domain-Driven)

The codebase is organized into the following top-level domains under `com.edu.erpbackend`:

| Package | Responsibility |
|---|---|
| `controller/auth` | Authentication (Login, Logout, Forgot/Reset Password) |
| `controller/operations` | Attendance, Assignments, Doubts, Notifications, Grades |
| `model/users` | User, Student, Teacher entities + Role enum |
| `model/academic` | Subject, StudyMaterial, Doubt, Assignment entities |
| `model/operations` | Attendance, Notification, Branch entities |
| `repository/*` | Spring Data JPA Repositories |
| `service/*` | Business logic layer |
| `dto/*` | Data Transfer Objects (request/response bodies) |
| `config/*` | Security config, CORS config, OpenAPI config |
| `util/*` | JWT Utility class |

---

## ✨ Features

### 👤 Authentication & User Management
- 📧 Email/Password login with **JWT-based stateless authentication**
- 🔐 **Forgot Password** with secure token-based email reset flow
- 🌐 **OAuth2 / Google Social Login** support
- Role-based access control with three roles: `ADMIN`, `TEACHER`, `STUDENT`

### 📊 Attendance Management
- Teachers/Admins can **mark attendance** per subject and semester
- Auto-filters students by **branch and semester** for accurate class management
- Students can view their own attendance records
- Teachers can **update/correct** individual attendance records

### 📝 Assignment Management
- Teachers can create and manage assignments
- Students can submit assignments
- Teachers can **grade** submissions
- Dashboard shows **pending** and **ungraded** assignment counts

### 📢 Notifications & Notices
- Targeted notifications: send to a **specific batch**, **branch + semester class**, or **everyone**
- Support for **file attachments** on notices (via Cloudinary)
- Users can **mark notifications as read**
- Notification types: `ASSIGNMENT`, `NOTICE`, `GRADE_UPDATE`, `DOUBT_REPLY`

### ❓ Doubt Resolution System
- Students can raise **subject-specific doubts**
- Teachers/Students can post **solutions**
- Solutions can be marked as **accepted**
- Query doubts by status or subject

### 📚 Study Materials
- Teachers can **upload study materials** (PDF, PPT, DOC) per subject
- Materials can be tagged by unit (`Unit 1`, `Unit 2`, `Previous Year Papers`)
- Files are stored via the **FileService (Cloudinary)**

### 📈 Dashboard Stats
- Role-specific dashboard stats:
  - **Student**: Attendance %, Pending Assignments
  - **Teacher**: Total Students, Ungraded Assignments
  - **Common**: Active Notices count

### 🏢 Academic Structure
- Manage **Branches** (departments/streams) with unique codes
- Semester-based student organization

---

## 📁 Project Structure

```
ERP-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/edu/erpbackend/
│   │   │   ├── config/            # SecurityConfig, WebConfig (CORS), OpenAPI Config
│   │   │   ├── controller/
│   │   │   │   ├── auth/          # AuthController
│   │   │   │   └── operations/    # AttendanceController, NotificationController, etc.
│   │   │   ├── dto/               # LoginRequest, LoginResponse, DashboardStats, etc.
│   │   │   ├── model/
│   │   │   │   ├── academic/      # Subject, StudyMaterial, Doubt, Assignment
│   │   │   │   ├── operations/    # Attendance, Notification, Branch
│   │   │   │   └── users/         # User, Student, Teacher, Role
│   │   │   ├── repository/
│   │   │   │   ├── academic/      # BranchRepository, AttendanceRepository, etc.
│   │   │   │   ├── operations/    # DoubtRepository, etc.
│   │   │   │   └── users/         # UserRepository, StudentRepository, etc.
│   │   │   ├── service/
│   │   │   │   ├── auth/          # AuthService
│   │   │   │   ├── common/        # FileService
│   │   │   │   └── operations/    # AttendanceService, NotificationService, etc.
│   │   │   └── util/              # JwtUtil
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── pom.xml
├── mvnw
└── mvnw.cmd
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **PostgreSQL** (running locally or via Docker)
- **Docker** (optional, for containerized setup)
- A **Cloudinary** account (for file uploads)
- An **SMTP email** provider (for password reset emails)

---

### Local Setup

**1. Clone the repository**
```bash
git clone https://github.com/iamchirag06/ERP-Backend.git
cd ERP-Backend
```

**2. Configure Environment Variables**

Create an `application.properties` (or `application.yml`) under `src/main/resources/` and set the required variables (see [Environment Variables](#-environment-variables) section below).

**3. Build and Run**
```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or build the JAR first
./mvnw clean package -DskipTests
java -jar target/ERP-Backend-0.0.1-SNAPSHOT.jar
```

The application will start on **port `6767`** by default.

---

### Docker Setup

The project includes a **multi-stage Dockerfile** for optimized production builds.

**1. Build the Docker image**
```bash
docker build -t erp-backend:latest .
```

**2. Run the container**
```bash
docker run -d \
  -p 6767:6767 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/erpdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  -e JWT_SECRET=your_base64_secret \
  -e CLOUDINARY_CLOUD_NAME=your_cloud_name \
  -e CLOUDINARY_API_KEY=your_api_key \
  -e CLOUDINARY_API_SECRET=your_api_secret \
  --name erp-backend \
  erp-backend:latest
```

**3. (Optional) Using Docker Compose**
```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: erpdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: yourpassword
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "6767:6767"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/erpdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: yourpassword
      JWT_SECRET: your_base64_encoded_secret
```

---

## 🔧 Environment Variables

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/erpdb` |
| `SPRING_DATASOURCE_USERNAME` | DB Username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB Password | `yourpassword` |
| `JWT_SECRET` | Base64-encoded secret key for signing JWTs | `dGhpcyBpcyBhIHNlY3JldA==` |
| `SPRING_MAIL_HOST` | SMTP host for email sending | `smtp.gmail.com` |
| `SPRING_MAIL_PORT` | SMTP port | `587` |
| `SPRING_MAIL_USERNAME` | Email address | `your@email.com` |
| `SPRING_MAIL_PASSWORD` | Email password / App password | `your_app_password` |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary Cloud Name | `my-cloud` |
| `CLOUDINARY_API_KEY` | Cloudinary API Key | `123456789012345` |
| `CLOUDINARY_API_SECRET` | Cloudinary API Secret | `aBcDeFgHiJkLmNoPqRsTuVwXyZ` |

---

## 📡 API Endpoints

> 📘 Full interactive API documentation is available at **`http://localhost:6767/swagger-ui.html`** (powered by SpringDoc OpenAPI / Scalar UI) once the server is running.

### 🔐 Auth — `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Login with email & password, returns JWT |
| `POST` | `/api/auth/forgot-password` | Public | Send password reset email |
| `POST` | `/api/auth/reset-password` | Public | Reset password with token |
| `POST` | `/api/auth/logout` | Authenticated | Logout (client-side token removal) |

### 📊 Attendance — `/api/attendance`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/attendance/mark` | Teacher, Admin | Mark attendance for a class |
| `GET` | `/api/attendance/student/{studentId}` | Student, Teacher, Admin | Get attendance records for a student |
| `PUT` | `/api/attendance/update/{id}` | Teacher | Correct an attendance record |

### 📢 Notifications — `/api/notifications`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/notifications` | Authenticated | Get my notifications |
| `PUT` | `/api/notifications/{id}/read` | Authenticated | Mark a notification as read |
| `POST` | `/api/notifications/send` | Teacher, Admin | Send a notice (supports file attachments) |

---

## 🔒 Security

The application uses a **stateless JWT-based security model**:

1. **Login** → Server validates credentials → Issues a **signed JWT** (10-hour expiry) containing the user's email and role.
2. **Subsequent Requests** → Client sends the JWT in the `Authorization: Bearer <token>` header.
3. **JWT Filter** → Intercepts every request, validates the token signature and expiry, and loads the user into the Spring Security context.
4. **Role-Based Authorization** → Endpoints are protected using `@PreAuthorize` annotations (`hasRole('ADMIN')`, `hasRole('TEACHER')`, etc.).
5. **CORS** → Configured globally in `WebConfig` to allow all origins with credentials for seamless frontend integration.
6. **OAuth2** → Google Sign-In is supported via Spring OAuth2 Client.

### JWT Token Structure

```json
{
  "sub": "user@example.com",
  "role": "TEACHER",
  "iat": 1700000000,
  "exp": 1700036000
}
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature-name`
3. **Commit** your changes: `git commit -m 'feat: add some feature'`
4. **Push** to the branch: `git push origin feature/your-feature-name`
5. **Open a Pull Request**

---

## 👨‍💻 Author

**Chirag** — [@iamchirag06](https://github.com/iamchirag06)

---

<p align="center">Made with ❤️ using Spring Boot</p>