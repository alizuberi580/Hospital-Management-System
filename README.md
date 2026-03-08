# 🏥 Hospital Management System

> Spring Boot 3.5 · Java 21 · PostgreSQL · JWT + OAuth2 · REST API

A layered hospital management backend with two progressive modules:
**Module 1** — JPA fundamentals & entity relationships.
**Module 2** — Secured REST API with JWT auth, OAuth2, and role-based access.

---

## ⚙️ Stack

- **Spring Boot 3.5** / Spring MVC / Spring Data JPA / Hibernate
- **Spring Security** — JWT (jjwt 0.13) + Google OAuth2
- **PostgreSQL** — with auto-schema generation & seed data
- **Lombok** · **ModelMapper** · **Maven**

---

## 🗂️ Architecture

```
hospitalManagement/                         → Module 1: Core JPA
  entity/    Patient, Doctor, Appointment, Department, Insurance
  repository/  Derived queries, JPQL, native queries, pagination
  service/     Transactional business logic

spring-boot-data-jpa-hospital-management-system/  → Module 2: Secured API
  controller/  AuthController, AdminController, DoctorController, PatientController
  security/    JwtAuthFilter, OAuth2SuccessHandler, WebSecurityConfig
  error/       GlobalExceptionHandler
  dto/         Request/Response DTOs with ModelMapper
```

---

## 🔐 Security Model

```
/public/**   → open
/auth/**     → open (signup, login)
everything   → JWT required (Bearer token)
```

- Stateless sessions, CSRF disabled
- BCrypt password hashing
- Google OAuth2 with custom success handler
- `JwtAuthFilter` intercepts every request before controllers

---

## 🚀 Quick Start

**1.** Create DB
```sql
CREATE DATABASE hospitalDB;
```

**2.** Update `src/main/resources/application.properties`
```properties
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
```

**3.** Run
```bash
cd hospitalManagement
./mvnw spring-boot:run        # starts on :8081
```

> Module 2 serves under `/api/v1` — e.g. `localhost:8081/api/v1/public/doctors`

---

## 📡 API Reference

```
POST  /auth/signup             → Register
POST  /auth/login              → JWT token

GET   /public/doctors          → List doctors (open)

GET   /admin/patients          → All patients (paginated)
GET   /doctors/appointments    → Doctor's appointments
GET   /patients/profile        → Patient profile
POST  /patients/appointments   → Book appointment
```

---

## 🗄️ Entity Relationships

```
Patient  ──1:1──  Insurance
Patient  ──1:N──  Appointment
Doctor   ──1:N──  Appointment
Department ──M:N── Doctor      (join table: my_dpt_doctors)
Department ──1:1── Doctor      (headDoctor)
User (app_user)                (local + OAuth2 credentials)
```

---

## 📦 Key Dependencies

```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-oauth2-client
postgresql
lombok
modelmapper              3.2.0
jjwt-api / impl / jackson   0.13.0
```

---

## 🧪 Tests

```bash
./mvnw test
```
