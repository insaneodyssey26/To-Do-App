# Firebase Setup Instructions

## Prerequisites

1. You need a Google account
2. Access to [Firebase Console](https://console.firebase.google.com/)

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name (e.g., "todo-app")
4. Enable Google Analytics (optional)
5. Click "Create project"

## Step 2: Add Android App to Firebase

1. In your Firebase project, click "Add app" and select Android
2. Enter package name: `com.masum.todo`
3. Enter app nickname: `Todo App`
4. Leave SHA-1 blank for now (you can add it later for production)
5. Click "Register app"

## Step 3: Download Configuration File

1. Download the `google-services.json` file
2. Replace the placeholder `google-services.json` in your `app/` directory with the real one

## Step 4: Enable Authentication

1. In Firebase Console, go to "Authentication" > "Sign-in method"
2. Click on "Google" provider
3. Toggle "Enable"
4. Enter your project's support email
5. Click "Save"

## Step 5: Enable Firestore Database

1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Choose "Start in test mode" (for development)
4. Select a location for your database
5. Click "Done"

## Step 6: Get Web Client ID

1. In Firebase Console, go to "Project Settings" (gear icon)
2. Go to "General" tab
3. Scroll to "Your apps" section
4. Find your Android app and expand it
5. Copy the "Web client ID"
6. Replace `YOUR_WEB_CLIENT_ID` in `AuthService.kt` with this value

## Step 7: Update Security Rules (Optional)

For production, update Firestore security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/tasks/{taskId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Step 8: Build and Test

1. Sync your project in Android Studio
2. Build and run the app
3. Test Google Sign-In functionality
4. Verify data sync in Firebase Console

## Troubleshooting

- If you get "API not enabled" error, enable Firebase Authentication API in Google Cloud Console
- If sign-in fails, check SHA-1 fingerprint is correctly configured
- Ensure package name matches exactly: `com.masum.todo`

## Production Considerations

1. Add SHA-1 fingerprint for release builds
2. Update Firestore security rules
3. Enable app check for additional security
4. Set up proper error handling and offline support
