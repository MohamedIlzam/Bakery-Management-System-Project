# Kodikara Bake House Management System

Kodikara Bake House Management System is a **web-based bakery management and sales distribution system** designed to streamline operations for **owners** (who manage master data, view reports, and create jobs) and **salesmen** (who handle daily shop and fair deliveries).

The system manages inventory products, vehicles, drivers, customer shops, daily distribution/sales sheets, payments, outstanding balances, and generates real-time profit and loss reports.

---

## Architecture & Tech Stack

*   **Frontend**: React (TypeScript), Vite, Tailwind CSS, Radix UI / shadcn/ui components.
*   **Backend**: Java Spring Boot, Hibernate / JPA, Spring Security.
*   **Database**: MySQL.
*   **Testing**: Cypress E2E testing framework.

---

## System Requirements

Before running the project on a new machine, ensure you have the following installed:

1.  **Java Development Kit (JDK 17 or JDK 21)**
2.  **Node.js (v18.0.0 or higher)**
3.  **MySQL Server (v8.0 or higher)**
4.  **Git** (for cloning and pulling the repository)

---

## Quick Start Guide

Follow these steps to set up and run the application on your local machine:

### 1. Clone the Repository
Clone the codebase and navigate into the root directory:
```bash
git clone https://github.com/MohamedIlzam/Bakery-Management-System-Project.git
cd Bakery-Management-System-Project
```

---

### 2. Configure & Run the Backend

#### A. Database Setup
1.  Make sure your **MySQL Server** is running.
2.  Open [backend/src/main/resources/application.yml](file:///c:/Users/lap.lk/IdeaProjects/Bakery-Management-System/backend/src/main/resources/application.yml) and verify the MySQL credentials:
    ```yaml
    spring:
      datasource:
        url: "jdbc:mysql://localhost:3306/bakery?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
        username: root
        password: "YourMySQLPasswordHere"  # Update this with your local MySQL password
    ```
    *(Note: Hibernate is configured to automatically create the `bakery` database and its tables on startup).*

#### B. Start the Backend Server
From the project root directory, navigate to the `backend` folder and run the Maven Spring Boot wrapper command:

*   **Windows (PowerShell or Command Prompt):**
    ```powershell
    cd backend
    .\mvnw.cmd spring-boot:run
    ```
*   **macOS / Linux:**
    ```bash
    cd backend
    chmod +x mvnw
    ./mvnw spring-boot:run
    ```
The backend server will launch and listen on **`http://localhost:8081`**.

#### C. Seed the Default Owner User
Once the backend runs for the first time (which creates the schema tables), you need to insert an initial admin/owner user to log in.

We have included a helper script [backend/insert_owner.js](file:///c:/Users/lap.lk/IdeaProjects/Bakery-Management-System/backend/insert_owner.js) to automate this:
1.  Open a new terminal window, navigate to the `backend` folder, and run:
    ```bash
    npm install mysql2
    node insert_owner.js
    ```
2.  Alternatively, you can manually execute this SQL query inside your MySQL client:
    ```sql
    USE bakery;
    INSERT INTO user (user_id, password, role, username) 
    VALUES ('USR001', '$2b$10$nBQ8N3YmOcJo//gYe5/UoeJvZWfZcGu8AfnYBoGO7wjcGGMvmdnom', 'ROLE_OWNER', 'owner');
    ```
    *This creates a default owner account:*
    *   **Username:** `owner`
    *   **Password:** `owner1`

---

### 3. Install & Run the Frontend

With the backend running, open another terminal window and navigate to the `frontend` folder:

1.  **Install dependencies:**
    ```bash
    cd frontend
    npm install
    ```
2.  **Start the Vite development server:**
    ```bash
    npm run dev
    ```
The frontend application will start and be accessible at **`http://localhost:8082`**. Open this URL in your browser to log in using the credentials:
*   **Username:** `owner`
*   **Password:** `owner1`

---

## Running Automated E2E Tests (Cypress)

The project includes pre-configured E2E tests for form validations, page routing, and CRUD/payment mock operations.

To run these tests locally:

1.  Navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  **Run Cypress in interactive mode (real-time in browser):**
    ```bash
    npm run cypress:open
    ```
    *This opens the Cypress Test Runner UI. Choose **E2E Testing**, select your preferred browser, and click on any spec file (e.g., [manage.cy.ts](file:///c:/Users/lap.lk/IdeaProjects/Bakery-Management-System/frontend/cypress/e2e/manage.cy.ts) or [deliveries.cy.ts](file:///c:/Users/lap.lk/IdeaProjects/Bakery-Management-System/frontend/cypress/e2e/deliveries.cy.ts)) to watch the tests run in real-time.*

3.  **Run Cypress in headless CLI mode:**
    ```bash
    npm run cypress:run
    ```

---

## Troubleshooting Port Conflicts
If you encounter `403: Forbidden` or `DevTools connection refused` errors when opening Cypress, verify that port `8080` is not in use by background processes like `ApplicationWebServer` or legacy servers. 

The application is pre-configured to run on port **`8082`** (frontend) and **`8081`** (backend) to prevent these conflicts. Ensure both dev servers are restarted after pull/clone.
