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
- [API Documentation](#-api-documentation)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

The **ERP-Backend** is the server-side engine for an education management platform. It serves three primary user roles — **Admin**, **Teacher**, and **Student** — and orchestrates everything from authentication and attendance tracking to assignment management, doubt resolution, timetable scheduling, notifications, and file uploads.

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
| API Docs | SpringDoc OpenAPI (Scalar UI) 2.8.15 |
| Build Tool | Maven |
| Containerization | Docker (multi-stage build) |
| Monitoring | Spring Boot Actuator |
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
| `controller/auth` | Authentication (Login, Logout, Forgot/Reset Password, Current User) |
| `controller/academic` | Assignments, Doubts, Study Resources, Subjects, Timetable |
| `controller/operations` | Attendance, Dashboard Stats, Notifications, Teacher Attendance |
| `controller/users` | Admin, Student Profile, Teacher Profile, User management |
| `model/users` | User, Student, Teacher entities + Role enum |
| `model/academic` | Subject, StudyMaterial, Doubt, Assignment, TimetableEntry, Solution |
| `model/operations` | Attendance, Notification, Branch entities |
| `repository/*` | Spring Data JPA Repositories |
| `security/*` | OAuth2 user service and login success handler |
| `service/*` | Business logic layer |
| `dto/*` | Data Transfer Objects (request/response bodies) |
| `config/*` | SecurityConfig, WebConfig (CORS), OpenApiConfig, GlobalExceptionHandler, JwtAuthenticationFilter |
| `util/*` | JWT Utility class |

---

## ✨ Features

### 👤 Authentication & User Management
- 📧 Email/Password login with **JWT-based stateless authentication**
- 🔐 **Forgot Password** with secure token-based email reset flow
- 🌐 **OAuth2 / Google Social Login** support
- Role-based access control with three roles: `ADMIN`, `TEACHER`, `STUDENT`
- Fetch currently authenticated user profile via `/api/auth/me`

### 📊 Attendance Management
- Teachers/Admins can **mark attendance** per subject and semester
- Auto-filters students by **branch and semester** for accurate class management
- Students can view their own attendance records with **per-subject summaries**
- Teachers can **update/correct** individual attendance records
- Teachers get a dedicated view of their assigned subjects and class rosters

### 📝 Assignment Management
- Teachers can create, update, and delete assignments (with optional file attachments)
- Students can submit assignments (with optional file uploads)
- Teachers can **grade** submissions (triggers grade-update notifications)
- Students can view **their own submission history**
- Dashboard shows **pending** and **ungraded** assignment counts

### 📢 Notifications & Notices
- Targeted notifications: send to a **specific batch**, **branch + semester class**, or **everyone**
- Support for **file attachments** on notices (via Cloudinary)
- Users can **mark notifications as read** (individually or all at once)
- Unread **notification count** endpoint for frontend badge support
- Notification types: `ASSIGNMENT`, `NOTICE`, `GRADE_UPDATE`, `DOUBT_REPLY`
- Admin/Teacher can view all sent notifications and delete notification batches

### ❓ Doubt Resolution System
- Students can raise **subject-specific doubts** (with optional bounty points)
- Any authenticated user can post **solutions**
- Doubts owner can mark a solution as **accepted**
- Query doubts by subject, student, or across all subjects
- Dedicated endpoints for students to view their own doubts and received solutions

### 📚 Study Materials
- Teachers can **upload study materials** (PDF, PPT, DOC, etc.) per subject
- Materials can be tagged by unit (`Unit 1`, `Unit 2`, `Previous Year Papers`, etc.)
- Files are stored via **Cloudinary**; teachers can also delete uploaded materials

### 🗓️ Timetable Management
- Admins can **add timetable entries** (day, time slot, subject, room)
- Students and Teachers can view their own **personalized schedule**

### 👤 User Profiles
- **Students** can view and update their profile (name, phone, CGPA, guardian phone, etc.) and upload a profile image
- **Teachers** can view and update their profile and upload a profile image
- **Admin** can view all students/teachers, update student details, and promote an entire batch to the next semester

### 📈 Dashboard Stats
- Role-specific dashboard stats:
  - **Student**: Attendance %, Pending Assignments, Active Notices
  - **Teacher**: Total Students, Ungraded Assignments, Active Notices
  - **Admin**: Total Students, Active Notices

### 🏢 Academic Structure
- Manage **Branches** (departments/streams) with unique codes
- Manage **Subjects** scoped to a branch and semester
- Admin can assign teachers to subjects
- Semester-based student organization with batch promotion support

---

## 📁 Project Structure

```
ERP-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/edu/erpbackend/
│   │   │   ├── config/            # SecurityConfig, WebConfig (CORS), OpenApiConfig,
│   │   │   │                      # GlobalExceptionHandler, JwtAuthenticationFilter
│   │   │   ├── controller/
│   │   │   │   ├── auth/          # AuthController
│   │   │   │   ├── academic/      # AssignmentController, DoubtController,
│   │   │   │   │                  # ResourceController, SubjectController, TimetableController
│   │   │   │   ├── operations/    # AttendanceController, DashboardController,
│   │   │   │   │                  # NotificationController, TeacherAttendanceController
│   │   │   │   └── users/         # AdminController, StudentController,
│   │   │   │                      # TeacherController, UserController
│   │   │   ├── dto/               # LoginRequest, LoginResponse, DashboardStats,
│   │   │   │                      # StudentProfileResponse, AttendanceSummaryResponse, etc.
│   │   │   ├── model/
│   │   │   │   ├── academic/      # Subject, StudyMaterial, Doubt, Solution,
│   │   │   │   │                  # Assignment, Submission, TimetableEntry
│   │   │   │   ├── operations/    # Attendance, Notification, Branch
│   │   │   │   └── users/         # User, Student, Teacher, Role
│   │   │   ├── repository/        # Spring Data JPA Repositories
│   │   │   ├── security/          # CustomOAuth2UserService, OAuth2LoginSuccessHandler
│   │   │   ├── service/
│   │   │   │   ├── auth/          # AuthService
│   │   │   │   ├── common/        # FileService
│   │   │   │   ├── academic/      # AssignmentService, DoubtService, TimetableService
│   │   │   │   ├── operations/    # AttendanceService, NotificationService
│   │   │   │   └── users/         # StudentService, TeacherService
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
- A **Google Cloud** project with OAuth2 credentials (for Google Login)
- An **SMTP email** provider such as Gmail (for password reset emails)

---

### Local Setup

**1. Clone the repository**
```bash
git clone https://github.com/iamchirag06/ERP-Backend.git
cd ERP-Backend
```

**2. Configure Environment Variables**

Set the required environment variables (see [Environment Variables](#-environment-variables) section below) in your shell or in a `.env` file before running the application.

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
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/erpdb \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=yourpassword \
  -e JWT_SECRET=your_base64_secret \
  -e MAIL_USERNAME=your@email.com \
  -e MAIL_PASSWORD=your_app_password \
  -e CLIENT_ID=your_google_client_id \
  -e CLIENT_SECRET=your_google_client_secret \
  -e CLOUDINARY_CLOUDNAME=your_cloud_name \
  -e CLOUDINARY_APIKEY=your_api_key \
  -e CLOUDINARY_APISECRET=your_api_secret \
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
      DB_URL: jdbc:postgresql://db:5432/erpdb
      DB_USERNAME: postgres
      DB_PASSWORD: yourpassword
      JWT_SECRET: your_base64_encoded_secret
      MAIL_USERNAME: your@email.com
      MAIL_PASSWORD: your_app_password
      CLIENT_ID: your_google_client_id
      CLIENT_SECRET: your_google_client_secret
      CLOUDINARY_CLOUDNAME: your_cloud_name
      CLOUDINARY_APIKEY: your_api_key
      CLOUDINARY_APISECRET: your_api_secret
```

---

## 🔧 Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/erpdb` |
| `DB_USERNAME` | DB Username | `postgres` |
| `DB_PASSWORD` | DB Password | `yourpassword` |
| `JWT_SECRET` | Base64-encoded secret key for signing JWTs | `dGhpcyBpcyBhIHNlY3JldA==` |
| `MAIL_USERNAME` | Gmail address used for sending emails | `your@gmail.com` |
| `MAIL_PASSWORD` | Gmail App Password (not your account password) | `xxxx xxxx xxxx xxxx` |
| `CLIENT_ID` | Google OAuth2 Client ID | `123456-abc.apps.googleusercontent.com` |
| `CLIENT_SECRET` | Google OAuth2 Client Secret | `GOCSPX-...` |
| `CLOUDINARY_CLOUDNAME` | Cloudinary Cloud Name | `my-cloud` |
| `CLOUDINARY_APIKEY` | Cloudinary API Key | `123456789012345` |
| `CLOUDINARY_APISECRET` | Cloudinary API Secret | `aBcDeFgHiJkLmNoPqRsTuVwXyZ` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed CORS origins (optional, defaults to `*`) | `https://myapp.com` |
| `PORT` | Server port (optional, defaults to `6767`) | `8080` |

---

## 📡 API Endpoints

> 📘 Full interactive API documentation is available via the Scalar UI at **`http://localhost:6767/swagger-ui.html`** once the server is running.

### 🔐 Auth — `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Login with email & password, returns JWT |
| `POST` | `/api/auth/forgot-password` | Public | Send password reset email |
| `POST` | `/api/auth/reset-password` | Public | Reset password with token |
| `POST` | `/api/auth/logout` | Authenticated | Logout (client-side token removal) |
| `GET` | `/api/auth/me` | Authenticated | Get the currently logged-in user's profile |

### 📊 Attendance — `/api/attendance`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/attendance/mark` | Teacher, Admin | Mark attendance for a class |
| `GET` | `/api/attendance/student/{studentId}` | Student, Teacher, Admin | Get all attendance records for a student |
| `PUT` | `/api/attendance/update/{id}` | Teacher | Correct a single attendance record |
| `GET` | `/api/attendance/summary/{studentId}` | Student, Teacher, Admin | Get attendance percentage summary per subject |

### 📊 Teacher Attendance Helper — `/api/teacher/attendance`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/teacher/attendance/subjects` | Teacher | Get teacher's assigned subjects (light payload) |
| `GET` | `/api/teacher/attendance/students` | Teacher | Get student roster for a specific subject |

### 📝 Assignments — `/api/assignments`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/assignments/create` | Teacher | Create a new assignment (with optional file) |
| `PUT` | `/api/assignments/{id}` | Teacher | Update an existing assignment |
| `DELETE` | `/api/assignments/{id}` | Teacher | Delete an assignment |
| `POST` | `/api/assignments/grade` | Teacher | Grade a student submission |
| `GET` | `/api/assignments/{assignmentId}/submissions` | Authenticated | View all submissions for an assignment |
| `POST` | `/api/assignments/submit` | Student | Submit an assignment (with optional file) |
| `GET` | `/api/assignments/subject/{subjectId}` | Authenticated | Get all assignments for a subject |
| `GET` | `/api/assignments/my-submissions` | Student | View all of the student's own submissions |

### ❓ Doubts — `/api/doubts`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/doubts/ask` | Authenticated | Post a new doubt |
| `POST` | `/api/doubts/answer` | Authenticated | Add a solution to a doubt |
| `POST` | `/api/doubts/accept/{solutionId}` | Authenticated | Accept a solution |
| `GET` | `/api/doubts/subject/{subjectId}` | Authenticated | Get all doubts for a subject |
| `GET` | `/api/doubts/{doubtId}/solutions` | Authenticated | Get all solutions for a doubt |
| `GET` | `/api/doubts/all` | Authenticated | Get all doubts across all subjects |
| `GET` | `/api/doubts/my-subjects` | Student | Get doubts for student's enrolled subjects |
| `GET` | `/api/doubts/student/{studentId}` | Authenticated | Get all doubts posted by a specific student |
| `GET` | `/api/doubts/solutions/student/{studentId}` | Authenticated | Get solutions for a student's doubts |
| `GET` | `/api/doubts/solutions/my` | Student | Get all of the current student's doubts with solutions |

### 📚 Study Resources — `/api/resources`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/resources/upload` | Teacher | Upload study material for a subject |
| `GET` | `/api/resources/subject/{subjectId}` | Authenticated | Get all study materials for a subject |
| `DELETE` | `/api/resources/{id}` | Teacher | Delete a study material |

### 📖 Subjects — `/api/subjects`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/subjects/me` | Teacher | Get teacher's own assigned subjects |
| `GET` | `/api/subjects/{branchId}/{semester}` | Authenticated | Get subjects by branch and semester |
| `GET` | `/api/subjects/{subjectId}/students` | Teacher | Get student roster for a specific subject |

### 🗓️ Timetable — `/api/timetable`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/timetable/add` | Admin | Add a timetable entry |
| `GET` | `/api/timetable/my-schedule` | Authenticated | Get the current user's timetable |

### 📢 Notifications — `/api/notifications`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/notifications` | Authenticated | Get my notifications |
| `GET` | `/api/notifications/unread-count` | Authenticated | Get unread notification count |
| `PUT` | `/api/notifications/{id}/read` | Authenticated | Mark a notification as read |
| `PUT` | `/api/notifications/read-all` | Authenticated | Mark all notifications as read |
| `DELETE` | `/api/notifications/{notificationId}` | Authenticated | Delete a notification |
| `POST` | `/api/notifications/send` | Teacher, Admin | Send a notice (supports file attachments) |
| `DELETE` | `/api/notifications/batch/{batchId}` | Teacher, Admin | Delete a notification batch from all users |
| `GET` | `/api/notifications/admin/sent` | Teacher, Admin | Get all sent notifications |
| `GET` | `/api/notifications/admin/batch/{batchId}` | Teacher, Admin | Get details of a specific notification batch |

### 👤 Student Profile — `/api/student/profile`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/student/profile` | Student | Get own profile |
| `PUT` | `/api/student/profile` | Student | Update own profile |
| `POST` | `/api/student/profile/image` | Student | Upload profile image |
| `GET` | `/api/student/profile/branches` | Student | Get student's assigned branch |
| `GET` | `/api/student/profile/subjects` | Student | Get subjects for student's branch and semester |

### 👩‍🏫 Teacher Profile — `/api/teacher/profile`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/teacher/profile` | Teacher | Get own profile |
| `PUT` | `/api/teacher/profile` | Teacher | Update own profile |
| `POST` | `/api/teacher/profile/image` | Teacher | Upload profile image |
| `GET` | `/api/teacher/profile/branches` | Teacher | Get all branches |

### 🛡️ Admin — `/api/admin`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/admin/add-teacher` | Admin | Register a new teacher |
| `POST` | `/api/admin/add-student` | Admin | Register a new student |
| `POST` | `/api/admin/add-branch` | Admin | Create a new branch |
| `GET` | `/api/admin/branches` | Admin | List all branches |
| `POST` | `/api/admin/add-subject/{branchId}` | Admin | Add a subject to a branch |
| `GET` | `/api/admin/subjects/{branchId}/{semester}` | Admin | List subjects for a branch/semester |
| `PUT` | `/api/admin/subjects/{subjectId}/assign-teacher/{teacherId}` | Admin | Assign a teacher to a subject |
| `GET` | `/api/admin/students` | Admin | List all students (filterable by branch, semester, batch) |
| `PUT` | `/api/admin/student/{id}` | Admin | Update a student's details |
| `DELETE` | `/api/admin/student/{id}` | Admin | Delete a student |
| `GET` | `/api/admin/teachers` | Admin | List all teachers (filterable by department) |
| `DELETE` | `/api/admin/teacher/{id}` | Admin | Delete a teacher |
| `POST` | `/api/admin/promote-batch` | Admin | Promote all students in a batch to the next semester |

### 📈 Dashboard — `/api/dashboard/stats`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/dashboard/stats` | Authenticated | Get role-specific dashboard statistics |

### 👥 Users — `/api/users`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/users/me` | Authenticated | Get current user details |
| `GET` | `/api/users/students` | Admin, Teacher | List students with optional filters |
| `GET` | `/api/users/teachers` | Authenticated | List teachers with optional filters |

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

## 📘 API Documentation

Interactive API documentation is auto-generated by **SpringDoc OpenAPI** and served via the **Scalar UI**:

- **URL**: `http://localhost:6767/swagger-ui.html`
- Explore and test all endpoints directly from the browser.
- Authentication: Click **Authorize** and enter your JWT token as `Bearer <token>`.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature-name`
3. **Commit** your changes: `git commit -m 'feat: add some feature'`
4. **Push** to the branch: `git push origin feature/your-feature-name`
5. **Open a Pull Request**

Please ensure your changes follow the existing code style and include appropriate tests where applicable.

---

## 📄 License

This project is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2025 Chirag

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Author

**Chirag** — [@iamchirag06](https://github.com/iamchirag06)

---

<p align="center">Made with ❤️ using Spring Boot</p>