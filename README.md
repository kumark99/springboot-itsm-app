# ITSM Incident Management System

A Spring Boot application for managing IT Service Management (ITSM) incidents. This application provides a web interface for users to report incidents and for agents/admins to manage them. It also exposes a REST API for programmatic access.

## 🛠 Technologies & Dependencies

This project is built using the following technologies:

*   **Java 17**
*   **Spring Boot 3.2.0**
    *   Spring Web (MVC & REST)
    *   Spring Data JPA
    *   Spring Security
*   **Thymeleaf** (Server-side templating)
    *   Thymeleaf Extras Spring Security
*   **Database**: MySQL (configured in `application.properties`)
*   **Frontend**: Bootstrap 5 (via CDN)
*   **Build Tool**: Maven

## 🚀 Project Setup

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd springboot-itsm-app
    ```

2.  **Database Configuration**:
    *   Ensure you have a MySQL database running.
    *   Update `src/main/resources/application.properties` with your database credentials if they differ from the defaults.

3.  **Application Configuration**:
    *   **Port**: The application is configured to run on port `8081` by default. You can change this in `application.properties` (`server.port`).
    *   **Logging**:
        *   Root Level: `INFO`
        *   Application Level (`com.itsm`): `DEBUG` (useful for development)

4.  **Build the project**:
    ```bash
    mvn clean install
    ```

## 💻 Important Commands

### Compiling
To compile the source code:
```bash
mvn compile
```

### Running the Application
To run the Spring Boot application:
```bash
mvn spring-boot:run
```
The application will start at `http://localhost:8081`.

## ☁️ AWS Deployment & Configuration

The application supports a dedicated AWS profile that integrates with **AWS Secrets Manager** for secure database credential management.

### 1. AWS Configuration Files
*   **`pom_aws.xml`**: A specialized Maven build file that includes `spring-cloud-aws-starter-secrets-manager` dependencies.
*   **`src/main/resources/application-aws.properties`**: Configuration for the AWS environment.

### 2. AWS Secrets Manager Setup
Ensure you have a secret created in AWS Secrets Manager in your target region.
*   **Secret Name**: `/itsm/incident-management`
*   **Secret Keys (JSON)**:
    ```json
    {
      "username": "your_db_username",
      "password": "your_db_password"
    }
    ```

### 3. Running in AWS Mode
To run the application using the AWS configuration and dependencies:

```bash
mvn -f pom_aws.xml spring-boot:run -Dspring-boot.run.profiles=aws
```

**Note:** Ensure your environment (EC2, Container, or Local Machine) has proper AWS credentials configured (e.g., via `~/.aws/credentials` or environment variables) to access Secrets Manager.

## 🔐 Default Users & Credentials

The application comes with a `DataSeeder` that initializes the database with the following users.
**Default Password for all users:** `password`

| Username | Role | Permissions / Functionality |
| :--- | :--- | :--- |
| **user** | `ROLE_USER` | Can report new incidents and view their own reported incidents. |
| **agent** | `ROLE_AGENT` | Can view all incidents, assign incidents to themselves, and update incident status/details. |
| **manager** | `ROLE_MANAGER` | Has oversight capabilities (similar to Agent but typically for reporting/management). |
| **admin** | `ROLE_ADMIN` | Full system access. Can manage users, roles, and all incidents. |

## 🔌 REST API & Client Testing

The application exposes a CRUD REST API at `/api/incidents`.

### API Endpoints
*   `GET /api/incidents` - List all incidents
*   `GET /api/incidents/{id}` - Get incident by ID
*   `POST /api/incidents` - Create a new incident
*   `PUT /api/incidents/{id}` - Update an incident
*   `DELETE /api/incidents/{id}` - Delete an incident

### Running the Standalone REST Client
A standalone Java client is included to demonstrate and test the REST endpoints. This client performs a full cycle of CRUD operations (Create, Read, Update, Delete).

**To run the REST Client test:**

1.  Ensure the main application is running (`mvn spring-boot:run`).
2.  Open a new terminal window.
3.  Run the following command:

```bash
mvn compile exec:java -Dexec.mainClass="com.itsm.incident.client.IncidentRestClient"
```

**Expected Output:**
The client will print the status and response body for each operation in the terminal, verifying that the API is functioning correctly.

## 🌍 External API Access & Sample Payload

To create an incident from an external service (like Postman, curl, or another microservice), use the following details:

**Endpoint:** `POST http://localhost:8081/api/incidents`
**Auth:** Basic Auth (Username: `admin`, Password: `password`)
**Headers:** `Content-Type: application/json`

### Sample JSON Payload
```json
{
    "title": "Server Overheating",
    "description": "The main production server is showing high temperature warnings.",
    "priority": "CRITICAL",
    "category": {
        "id": 1
    },
    "status": {
        "id": 1
    },
    "reportedBy": {
        "id": 1
    }
}
```

### Curl Example
```bash
curl -X POST http://localhost:8081/api/incidents \
-u admin:password \
-H "Content-Type: application/json" \
-d '{
    "title": "Email Service Down",
    "description": "Users cannot send emails.",
    "priority": "HIGH",
    "category": {"id": 2},
    "status": {"id": 1},
    "reportedBy": {"id": 1}
}'
```
