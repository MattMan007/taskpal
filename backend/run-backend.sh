#!/bin/bash

# Set environment variables for local development
export DATABASE_URL="jdbc:postgresql://localhost:5432/taskpal"
export DATABASE_USERNAME="$(whoami)"
export DATABASE_PASSWORD=""
export FIREBASE_PROJECT_ID="taskpal-demo"
export FIREBASE_CREDENTIALS_PATH="firebase-service-account.json"
export ALLOWED_ORIGINS="http://localhost:3000,http://localhost:8080,http://localhost:*"
export PORT="8080"

# Add Maven to PATH
export PATH="/opt/maven/bin:$PATH"

echo "🚀 Starting TaskPal Backend..."
echo "📊 Database URL: $DATABASE_URL"
echo "👤 Database User: $DATABASE_USERNAME"
echo "🔥 Firebase Project: $FIREBASE_PROJECT_ID"
echo "🌐 Port: $PORT"
echo ""

# Run the Spring Boot application
mvn spring-boot:run 