# TaskPal Backend

A collaborative task management backend built with Spring Boot, PostgreSQL, and Firebase Authentication.

## Features

- **Firebase Authentication**: Secure user authentication using Firebase
- **Project Management**: Create and manage collaborative projects
- **Task Management**: Create, assign, and track tasks with priorities and due dates
- **Team Collaboration**: Add team members to projects and assign tasks
- **Real-time Updates**: Track task progress and project statistics
- **PostgreSQL Database**: Robust data persistence with JPA/Hibernate

## Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **Authentication**: Firebase Admin SDK
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Firebase project with service account credentials

## Environment Variables

Set the following environment variables:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/taskpal
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

# Firebase Configuration
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_CREDENTIALS_PATH=path/to/firebase-service-account.json

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080

# Server Configuration
PORT=8080
```

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd taskpal/backend
```

### 2. Configure Firebase

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication and choose your preferred sign-in methods
3. Generate a service account key:
   - Go to Project Settings > Service Accounts
   - Click "Generate new private key"
   - Save the JSON file as `firebase-service-account.json` in `src/main/resources/`

### 3. Configure Database

#### Local PostgreSQL Setup:
```bash
# Install PostgreSQL
# Create database
createdb taskpal

# Update application.yml with your database credentials
```

#### Railway PostgreSQL Setup:
1. Create a new project on [Railway](https://railway.app/)
2. Add a PostgreSQL service
3. Copy the connection details and set the `DATABASE_URL` environment variable

### 4. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register/login user
- `GET /api/auth/me` - Get current user
- `PUT /api/auth/profile` - Update user profile

### Projects
- `GET /api/projects` - Get user's projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project details
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `POST /api/projects/{id}/members` - Add member to project
- `DELETE /api/projects/{id}/members/{memberId}` - Remove member from project

### Tasks
- `GET /api/tasks/my-tasks` - Get user's tasks
- `GET /api/tasks/assigned` - Get assigned tasks
- `GET /api/tasks/project/{projectId}` - Get project tasks
- `POST /api/tasks` - Create new task
- `GET /api/tasks/{id}` - Get task details
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PUT /api/tasks/{id}/assign` - Assign task to user
- `GET /api/tasks/status/{status}` - Get tasks by status
- `GET /api/tasks/overdue` - Get overdue tasks
- `GET /api/tasks/due-soon` - Get tasks due soon

### Health Check
- `GET /api/health` - Application health status

## Authentication

All endpoints (except `/api/auth/register` and `/api/health`) require Firebase authentication. Include the Firebase ID token in the Authorization header:

```
Authorization: Bearer <firebase-id-token>
```

## Database Schema

### Users Table
- `id` (Primary Key)
- `firebase_uid` (Unique)
- `name`
- `email` (Unique)
- `profile_picture_url`
- `created_at`
- `updated_at`

### Projects Table
- `id` (Primary Key)
- `name`
- `description`
- `color`
- `owner_id` (Foreign Key to Users)
- `created_at`
- `updated_at`

### Tasks Table
- `id` (Primary Key)
- `title`
- `description`
- `status` (TODO, IN_PROGRESS, REVIEW, DONE)
- `priority` (LOW, MEDIUM, HIGH, URGENT)
- `due_date`
- `project_id` (Foreign Key to Projects)
- `creator_id` (Foreign Key to Users)
- `assignee_id` (Foreign Key to Users)
- `created_at`
- `updated_at`

### Project Members Table (Join Table)
- `project_id` (Foreign Key to Projects)
- `user_id` (Foreign Key to Users)

## Deployment

### Railway Deployment

1. Connect your GitHub repository to Railway
2. Set environment variables in Railway dashboard
3. Deploy automatically on push to main branch

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/taskpal-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Development

### Running Tests
```bash
mvn test
```

### Code Style
The project follows standard Java conventions and Spring Boot best practices.

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. 