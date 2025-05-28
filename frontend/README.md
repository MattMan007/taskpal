# TaskPal Frontend

A beautiful and modern Flutter app for collaborative task management with Firebase authentication and real-time updates.

## Features

- **Modern UI/UX**: Clean, intuitive interface with Material Design 3
- **Firebase Authentication**: Email/password and Google sign-in
- **Real-time Collaboration**: Live updates for team projects
- **Cross-platform**: Works on iOS, Android, Web, and Desktop
- **Offline Support**: Local caching with automatic sync
- **Dark/Light Theme**: Automatic theme switching based on system preference

## Tech Stack

- **Framework**: Flutter 3.10+
- **State Management**: Provider
- **Navigation**: GoRouter
- **Authentication**: Firebase Auth
- **HTTP Client**: Dio
- **UI Components**: Material Design 3
- **Forms**: Flutter Form Builder

## Prerequisites

- Flutter SDK 3.10.0 or higher
- Dart SDK 3.0.0 or higher
- Firebase project with authentication enabled
- TaskPal backend running (see backend README)

## Setup Instructions

### 1. Install Flutter

Follow the official Flutter installation guide: https://docs.flutter.dev/get-started/install

### 2. Clone and Setup

```bash
cd taskpal/frontend
flutter pub get
```

### 3. Firebase Configuration

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication with Email/Password and Google sign-in
3. Add your app to the Firebase project:
   - For Android: Add `android/app/google-services.json`
   - For iOS: Add `ios/Runner/GoogleService-Info.plist`
   - For Web: Update `web/index.html` with Firebase config

4. Update `lib/config/firebase_options.dart` with your Firebase configuration:

```dart
static const FirebaseOptions web = FirebaseOptions(
  apiKey: 'your-web-api-key',
  appId: 'your-web-app-id',
  messagingSenderId: 'your-sender-id',
  projectId: 'your-project-id',
  authDomain: 'your-project-id.firebaseapp.com',
  storageBucket: 'your-project-id.appspot.com',
);
```

### 4. Backend Configuration

Update the API base URL in `lib/services/api_service.dart`:

```dart
static const String baseUrl = 'https://your-backend-url.com/api';
// For local development: 'http://localhost:8080/api'
// For Railway deployment: 'https://your-app.railway.app/api'
```

### 5. Run the App

```bash
# For development
flutter run

# For web
flutter run -d chrome

# For specific device
flutter devices
flutter run -d <device-id>
```

## Project Structure

```
lib/
├── config/
│   └── firebase_options.dart      # Firebase configuration
├── models/
│   ├── user.dart                  # User data model
│   ├── project.dart               # Project data model
│   └── task.dart                  # Task data model
├── providers/
│   ├── auth_provider.dart         # Authentication state management
│   ├── project_provider.dart      # Project state management
│   └── task_provider.dart         # Task state management
├── services/
│   └── api_service.dart           # HTTP API client
├── screens/
│   ├── auth/
│   │   ├── login_screen.dart      # Login interface
│   │   └── register_screen.dart   # Registration interface
│   ├── home/
│   │   └── home_screen.dart       # Dashboard/home screen
│   ├── projects/
│   │   ├── project_list_screen.dart
│   │   ├── project_detail_screen.dart
│   │   └── create_project_screen.dart
│   ├── tasks/
│   │   ├── task_list_screen.dart
│   │   ├── task_detail_screen.dart
│   │   └── create_task_screen.dart
│   └── profile/
│       └── profile_screen.dart    # User profile management
├── utils/
│   └── theme.dart                 # App theme configuration
└── main.dart                      # App entry point
```

## Key Features Implementation

### Authentication Flow
- Firebase Authentication with email/password and Google sign-in
- Automatic token refresh and API integration
- Secure logout with session cleanup

### State Management
- Provider pattern for reactive state management
- Separation of concerns with dedicated providers
- Error handling and loading states

### API Integration
- Dio HTTP client with interceptors
- Automatic Firebase token injection
- Comprehensive error handling
- Request/response logging

### Navigation
- GoRouter for declarative routing
- Authentication-based route guards
- Deep linking support

### UI/UX
- Material Design 3 components
- Responsive design for all screen sizes
- Dark/light theme support
- Smooth animations and transitions

## Building for Production

### Android
```bash
flutter build apk --release
# or for app bundle
flutter build appbundle --release
```

### iOS
```bash
flutter build ios --release
```

### Web
```bash
flutter build web --release
```

### Desktop
```bash
# Windows
flutter build windows --release

# macOS
flutter build macos --release

# Linux
flutter build linux --release
```

## Environment Configuration

Create different configurations for development and production:

### Development
- Local backend URL
- Debug Firebase project
- Verbose logging enabled

### Production
- Production backend URL
- Production Firebase project
- Optimized performance settings

## Testing

```bash
# Run unit tests
flutter test

# Run integration tests
flutter test integration_test/

# Run with coverage
flutter test --coverage
```

## Performance Optimization

- **Image Optimization**: Use `cached_network_image` for efficient image loading
- **List Performance**: Implement lazy loading for large datasets
- **State Management**: Minimize unnecessary rebuilds with proper Provider usage
- **Bundle Size**: Use tree shaking and code splitting for web builds

## Deployment

### Web Deployment
1. Build the web app: `flutter build web --release`
2. Deploy the `build/web` folder to your hosting service
3. Configure routing for single-page application

### Mobile App Stores
1. Follow platform-specific guidelines for app store submission
2. Configure app signing and certificates
3. Test on physical devices before submission

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Make your changes and add tests
4. Commit your changes: `git commit -am 'Add new feature'`
5. Push to the branch: `git push origin feature/new-feature`
6. Submit a pull request

## Troubleshooting

### Common Issues

1. **Firebase Configuration**: Ensure all Firebase config files are properly added
2. **API Connection**: Check backend URL and network connectivity
3. **Build Issues**: Run `flutter clean && flutter pub get`
4. **Platform Issues**: Check platform-specific requirements

### Debug Commands

```bash
# Check Flutter installation
flutter doctor

# Analyze code
flutter analyze

# Check dependencies
flutter pub deps

# Clean build cache
flutter clean
```

## License

This project is licensed under the MIT License - see the LICENSE file for details. 