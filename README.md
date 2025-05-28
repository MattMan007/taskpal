# TaskPal - Collaborative Task Management

A modern, full-stack collaborative task management application built with Flutter frontend, Spring Boot backend, PostgreSQL database, and Firebase authentication.

## 🚀 Features

### Core Functionality
- **User Authentication**: Secure Firebase authentication with email/password and Google sign-in
- **Project Management**: Create and manage collaborative projects with team members
- **Task Management**: Create, assign, and track tasks with priorities, due dates, and status updates
- **Real-time Collaboration**: Live updates and notifications for team activities
- **Team Management**: Add/remove team members, assign roles and permissions

### Technical Features
- **Cross-platform**: Flutter app works on iOS, Android, Web, and Desktop
- **Scalable Backend**: Spring Boot with PostgreSQL for robust data management
- **Cloud Deployment**: Ready for deployment on Railway, Heroku, or any cloud platform
- **Modern UI/UX**: Material Design 3 with dark/light theme support
- **Offline Support**: Local caching with automatic synchronization

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Flutter App   │    │  Spring Boot    │    │   PostgreSQL    │
│   (Frontend)    │◄──►│   (Backend)     │◄──►│   (Database)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Firebase      │    │   Railway       │    │   Railway       │
│ (Authentication)│    │  (Deployment)   │    │  (Database)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Tech Stack

### Frontend (Flutter)
- **Framework**: Flutter 3.10+
- **Language**: Dart
- **State Management**: Provider
- **Navigation**: GoRouter
- **HTTP Client**: Dio
- **Authentication**: Firebase Auth
- **UI**: Material Design 3

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Authentication**: Firebase Admin SDK
- **Security**: Spring Security
- **ORM**: Spring Data JPA
- **Build Tool**: Maven

### Infrastructure
- **Database**: PostgreSQL (Railway)
- **Authentication**: Firebase
- **Deployment**: Railway
- **Version Control**: Git

## 📁 Project Structure

```
taskpal/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/taskpal/
│   │       ├── config/      # Configuration classes
│   │       ├── controller/  # REST controllers
│   │       ├── dto/         # Data transfer objects
│   │       ├── model/       # JPA entities
│   │       ├── repository/  # Data repositories
│   │       ├── security/    # Security configuration
│   │       └── service/     # Business logic
│   ├── src/main/resources/
│   │   └── application.yml  # Application configuration
│   ├── pom.xml             # Maven dependencies
│   └── README.md           # Backend documentation
│
├── frontend/               # Flutter frontend
│   ├── lib/
│   │   ├── config/         # App configuration
│   │   ├── models/         # Data models
│   │   ├── providers/      # State management
│   │   ├── screens/        # UI screens
│   │   ├── services/       # API services
│   │   ├── utils/          # Utilities and themes
│   │   └── main.dart       # App entry point
│   ├── pubspec.yaml        # Flutter dependencies
│   └── README.md           # Frontend documentation
│
└── README.md               # This file
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Flutter 3.10+
- PostgreSQL (or Railway account)
- Firebase project

### 1. Clone the Repository
```bash
git clone <repository-url>
cd taskpal
```

### 2. Setup Backend
```bash
cd backend

# Configure environment variables
export DATABASE_URL="jdbc:postgresql://localhost:5432/taskpal"
export DATABASE_USERNAME="your_username"
export DATABASE_PASSWORD="your_password"
export FIREBASE_PROJECT_ID="your-firebase-project-id"

# Build and run
mvn clean install
mvn spring-boot:run
```

### 3. Setup Frontend
```bash
cd frontend

# Install dependencies
flutter pub get

# Configure Firebase (see frontend README)
# Update API base URL in lib/services/api_service.dart

# Run the app
flutter run
```

### 4. Setup Database

#### Option A: Local PostgreSQL
```bash
# Install PostgreSQL
# Create database
createdb taskpal
```

#### Option B: Railway (Recommended)
1. Create account at [Railway](https://railway.app/)
2. Create new PostgreSQL service
3. Copy connection details to environment variables

### 5. Setup Firebase
1. Create project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication (Email/Password + Google)
3. Generate service account key for backend
4. Configure Flutter app with Firebase config

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - Register/login user
- `GET /api/auth/me` - Get current user
- `PUT /api/auth/profile` - Update user profile

### Project Endpoints
- `GET /api/projects` - Get user's projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project details
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `POST /api/projects/{id}/members` - Add member
- `DELETE /api/projects/{id}/members/{memberId}` - Remove member

### Task Endpoints
- `GET /api/tasks/my-tasks` - Get user's tasks
- `GET /api/tasks/assigned` - Get assigned tasks
- `GET /api/tasks/project/{projectId}` - Get project tasks
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PUT /api/tasks/{id}/assign` - Assign task

## 🚀 Deployment

### Backend Deployment (Railway)
1. Connect GitHub repository to Railway
2. Set environment variables in Railway dashboard
3. Deploy automatically on push to main branch

### Frontend Deployment

#### Web
```bash
flutter build web --release
# Deploy build/web folder to hosting service
```

#### Mobile
```bash
# Android
flutter build apk --release

# iOS
flutter build ios --release
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
flutter test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📝 Environment Variables

### Backend
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/taskpal
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_CREDENTIALS_PATH=firebase-service-account.json
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
PORT=8080
```

### Frontend
Update `lib/config/firebase_options.dart` and `lib/services/api_service.dart` with your configuration.

## 🐛 Troubleshooting

### Common Issues
1. **Database Connection**: Check PostgreSQL is running and credentials are correct
2. **Firebase Auth**: Ensure Firebase project is configured correctly
3. **CORS Issues**: Check CORS configuration in backend
4. **Build Issues**: Run `mvn clean install` for backend, `flutter clean` for frontend

### Getting Help
- Check the individual README files in `/backend` and `/frontend` directories
- Review the API documentation
- Check Firebase and Railway documentation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Flutter team for the amazing framework
- Spring Boot team for the robust backend framework
- Firebase for authentication services
- Railway for easy deployment
- Material Design team for the beautiful design system

---

**Happy Coding! 🚀** 