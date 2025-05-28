#!/bin/bash

echo "🚀 Starting TaskPal Frontend..."
echo ""

# Navigate to the frontend directory
cd "$(dirname "$0")"

# Check if Flutter is available
if ! command -v flutter &> /dev/null; then
    # Try to use Flutter from the Documents path
    export PATH="$PATH:$HOME/Documents/Master Software Engineering/SoftwareMethodologies/flutter/bin"
fi

# Verify Flutter is now available
if ! command -v flutter &> /dev/null; then
    echo "❌ Flutter not found. Please install Flutter first."
    exit 1
fi

echo "✅ Flutter found: $(flutter --version | head -1)"
echo ""

# Install dependencies
echo "📦 Installing dependencies..."
flutter pub get

echo ""
echo "🌐 Starting Flutter web app..."
echo "🔗 The app will open in your browser at: http://localhost:3000"
echo "📱 Backend should be running at: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop the app"
echo ""

# Run the app on web
flutter run -d chrome --web-port=3000 