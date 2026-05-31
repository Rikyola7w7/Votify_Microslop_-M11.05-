# Votify — Academic Project Voting and Evaluation Platform

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot 4.0.3](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vaadin 25.0.6](https://img.shields.io/badge/Vaadin-25.0.6-orange.svg?logo=vaadin&logoColor=white)](https://vaadin.com/)
[![Flyway DB](https://img.shields.io/badge/Flyway-Migrations-red.svg?logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Database PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue.svg?logo=postgresql&logoColor=white)](https://postgresql.org/)
[![License MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)

**Votify** is a production-grade web application developed as an **academic project**. Its fundamental purpose is the comprehensive administration of competitions and the detailed evaluation of projects within science fairs, hackathons, university symposiums, or academic thesis defenses.

Built with **Spring Boot 4** on the backend and **Vaadin 25** on the frontend, the platform leverages a secure, reactive server-side architecture to bypass traditional REST controllers, offering a rich, fast, and responsive user experience (UX).

---

## 🚀 Key Features

Votify goes beyond a basic popularity voting system by incorporating professional-grade tools:

*   **🗳️ Multiple Voting Modalities:**
    *   *Standard Vote (Classic):* Direct popularity vote (1 vote per user per category).
    *   *Rubric/Scale Voting:* Allows rating a project within a numerical range based on quality criteria.
    *   *Checklist Voting:* Detailed evaluation based on multiple predefined checkpoints (highly useful for technical jury checklists).
*   **🧠 AI-Assisted Evaluation (AI Feedback):**
    *   Integrated support for generating automated constructive critiques and valuable insights for projects, helping participants understand their technical strengths and target improvement areas based on received evaluations.
*   **📜 Dynamic PDF Certificate Generation:**
    *   Advanced rendering engine to compile and download official certificates (PDF) for both competition winners and student/project participation certificates.
*   **👥 Role and User Management:**
    *   Independent workflows for **Administrators**, **Judges**, and **Voters/Participants**.
    *   Dynamic invitation system (`Invitations`) to recruit judges and enable controlled access to specific categories.
*   **🔔 Real-Time Notification Panel:**
    *   Automated notification delivery regarding competition lifecycle events (voting openings, results release, administrative alerts).
*   **📊 Rankings and Administrative Auditing:**
    *   Real-time standings calculation for Judge Rankings and Popular Rankings.
    *   Administrative override capabilities to manually reclassify project positions, edit vote tallies, or disqualify projects under audit.
*   **🎨 Premium Design and Accessibility:**
    *   Dark/Light mode support and components based on Vaadin's Lumo design system, featuring fluid transitions, interactive loading animations (simulating ballot boxes and falling votes), and strict adherence to WCAG 2.2 accessibility standards.

---

## 🏛️ System Architecture

The application is designed under a clean **Multilayer Architecture**. By utilizing **Vaadin**, frontend views run directly in the server's JVM. This cuts out REST APIs, JSON serialization bottlenecks, and public API exposure risks.

```
                  ┌──────────────────────────────┐
                  │      CLIENT (Web Browser)    │
                  └──────────────┬───────────────┘
                                 │ WebSockets / HTTP UI Interactions
                                 ▼
         ┌──────────────────────────────────────────────┐
         │              VIEW LAYER (Vaadin)             │
         │ (Server-Side UI — no Controllers required)  │
         └───────────────────────┬──────────────────────┘
                                 │ Constructor Dependency Injection
                                 ▼
         ┌──────────────────────────────────────────────┐
         │             SERVICE LAYER (Business)         │
         │   (Transactional services & AI logic)        │
         └───────────────────────┬──────────────────────┘
                                 │ Spring Data JPA
                                 ▼
         ┌──────────────────────────────────────────────┐
         │          REPOSITORY LAYER (Access)           │
         │      (JpaRepository / Query Builders)        │
         └───────────────────────┬──────────────────────┘
                                 │ ORM / Hibernate
                                 ▼
         ┌──────────────────────────────────────────────┐
         │                 DOMAIN MODEL                 │
         │     (Jakarta Persistence Entities & DDL)     │
         └──────────────────────────────────────────────┘
```

### 📂 Detailed Package Structure (com.microslop)

*   **`entity/` (Data Model):** Represents the problem domain.
    *   `User`: Credentials, security roles, and user profile information.
    *   `Competition`: Manages start/end dates, competition statuses, and voting configurations.
    *   `Project` / `PendingProjectSubmission`: Enrolled projects and their administrative approval queue.
    *   `Category`: Projects grouping within a competition, each configured with its own custom vote type.
    *   `Vote` / `ChecklistVote`: Granular ballot storage supporting classic, scale, and checklist modes.
    *   `ChecklistItem`: Dynamic assessment criteria defined for technical jury checklists.
    *   `AiFeedback`: Holds evaluation reports generated by AI models.
    *   `Certificate`: Entity tracking certificates, diplomas, and type mappings.
    *   `Invitation` / `Judge` / `Voter`: Handles user enrollment and access mapping.
*   **`repository/` (Data Access):** Spring Data JPA interfaces (`UserRepository`, `CompetitionRepository`, `VoteRepository`, etc.) extending `JpaRepository` and `JpaSpecificationExecutor` for optimized database operations.
*   **`service/` (Business Logic):** Encapsulates core transaction rules.
    *   `LocalizationService`: Comprehensive i18n localization translation support.
    *   `AiFeedbackService`: Manages prompt assembly and communication with AI evaluation engines.
    *   `CertificatePdfGenerator`: Professional-grade PDF compiler and canvas drawer.
*   **`views/` (Presentation Layer):** Built with premium Vaadin layouts:
    *   `AdminDashboardView`: Global monitoring, system logs, and administrative controls.
    *   `ConfigureCompetitionView` / `ManageCompetitionView`: Detailed setup for categories, vote schemas, and checklists.
    *   `VotingView` / `CategorySelectionView`: Optimized interfaces for voters and jurors.
    *   `AiFeedbackView` / `CertificatesView`: Delivery panels for feedback summaries and printable diplomas.

---

## 🛠️ Technology Stack & Requirements

*   **Runtime:** Java 21 (Eclipse Temurin JDK recommended).
*   **Backend Framework:** Spring Boot 4.0.3.
*   **Frontend UI:** Vaadin 25.0.6 (Server-Side Java Framework).
*   **Database:**
    *   *Development/Production:* PostgreSQL (optimized for serverless environments like Neon).
    *   *Testing:* In-memory H2 database with auto-generated schemas (`create-drop` profile).
*   **Migrations:** Flyway migrations placed inside `src/main/resources/db/migration/` (V1 to V8) for database schema versioning.
*   **PDF Generator:** OpenPDF / iText compilation libraries.

---

## 💻 Running the Project

### Prerequisites

*   Java Development Kit (JDK) 21.
*   An active PostgreSQL database (or system environment credentials configured for Neon).

### 1. Environment Variables

Create a local `.env` file or export system variables to define database credentials:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST>:<PORT>/<DATABASE>
SPRING_DATASOURCE_USERNAME=<USERNAME>
SPRING_DATASOURCE_PASSWORD=<PASSWORD>
```

### 2. Development Mode

The Maven Wrapper (`mvnw`) is included in the root folder. Start the development server by running:

```bash
./mvnw spring-boot:run
```

*The application will compile classes, automatically trigger Flyway database migrations, and launch on [http://localhost:8080](http://localhost:8080).*

### 3. Running Automated Tests

The codebase includes an extensive suite of integration tests that automatically activate the testing profile (`application-test.properties`) running H2 in-memory:

```bash
./mvnw test
```

### 4. Compiling for Production

To create a production-optimized package (which minifies Vaadin frontend assets and bundles all dependencies into a single executable Spring Boot JAR):

```bash
./mvnw package -Pproduction
```

To build and run within a Docker container:

```bash
docker build -t votify:latest .
docker run -p 8080:8080 --env-file .env votify:latest
```

---

## ⚖️ License and Academic Usage

This project is licensed under the terms of the **[MIT License](LICENSE.md)**.

### Academic Citation Guidelines:
If you use **Votify** as a base template, learning reference, or tool in course assignments, senior capstones, or academic research, we encourage you to:
1.  **Reference it:** Cite this official repository and its contributors in your references or project report.
2.  **Learn and Extend:** Use the codebase as a model to study server-side web architectures, transaction management in Spring Boot, and robust relational modeling with Jakarta Persistence.
