# Google Play Points Manager

An Android application that allows users to collect Google Play points and top up their favorite apps using those points.

## Features

- 🔐 **Google Sign-In Authentication**: Secure login using Google credentials
- 💰 **Points Collection**: Earn points from various activities
- 📊 **Points Dashboard**: View total points and transaction history
- 💳 **App Top-Ups**: Use Google Play points to top up apps
- 📱 **Billing Integration**: Seamless Google Play Billing integration
- 💾 **Local Database**: Offline-first data persistence with Room Database
- 🔄 **Sync**: Real-time synchronization with backend

## Project Structure

```
app/src/main/
├── java/com/example/googleplaypoints/
│   ├── data/
│   │   ├── local/        # Room Database DAOs
│   │   ├── model/        # Data models
│   │   ├── remote/       # Retrofit API services
│   │   └── repository/   # Repository pattern implementations
│   └── ui/
│       ├── auth/         # Login screen
│       ├── points/       # Points dashboard
│       ├── topup/        # Top-up feature
│       └── MainActivity.kt
└── res/
    ├── layout/          # XML layouts
    ├── values/          # Colors, strings, styles
    └── drawable/        # Icons and images
```

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **Database**: Room Database
- **Networking**: Retrofit + OkHttp
- **Dependency Injection**: Hilt
- **Authentication**: Google Sign-In SDK
- **Billing**: Google Play Billing Library
- **Coroutines**: Kotlin Coroutines for async operations
- **Data Store**: DataStore Preferences for secure token storage

## Setup Instructions

### Prerequisites
- Android Studio 2022.1 or higher
- Android SDK 24+
- Google Play Services account
- Google Cloud Project with OAuth 2.0 credentials

### Build Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd google-play-points-app
   ```

2. **Configure Google Sign-In**
   - Create a Firebase project at https://console.firebase.google.com
   - Add your Android app to the project
   - Download `google-services.json` and place it in `app/` directory
   - Update the CLIENT_ID in `LoginActivity.kt`

3. **Configure Backend API**
   - Update the base URL in `ApiService.kt` to your backend server
   - Ensure backend endpoints are implemented

4. **Build the app**
   ```bash
   ./gradlew build
   ```

5. **Run on device/emulator**
   ```bash
   ./gradlew installDebug
   ```

## API Endpoints Required

The following backend endpoints should be implemented:

- `POST /auth/login` - User authentication
- `GET /users/{userId}` - Get user profile
- `GET /users/{userId}/points` - Get user points
- `POST /topup/initiate` - Initiate top-up transaction
- `POST /topup/verify/{transactionId}` - Verify top-up completion

## Database Schema

### Users Table
- userId (PK)
- email
- displayName
- profileImageUrl
- totalPoints
- createdAt
- updatedAt

### Points Table
- id (PK)
- userId (FK)
- pointsAmount
- pointsType (PURCHASE, REWARD, REFERRAL, etc.)
- description
- timestamp

### TopUps Table
- id (PK)
- userId (FK)
- packageName
- appName
- topUpAmount
- currency
- transactionId
- status (PENDING, PROCESSING, COMPLETED, FAILED)
- timestamp

## Security Considerations

- 🔒 Use ProGuard/R8 for code obfuscation
- 🔐 Store sensitive data (tokens) in encrypted DataStore
- 🛡️ Validate all API responses
- ✅ Verify all server-side transactions
- 🔑 Implement certificate pinning for API calls

## Future Enhancements

- [ ] Biometric authentication
- [ ] Push notifications for points updates
- [ ] Multi-language support
- [ ] Analytics integration
- [ ] Referral program
- [ ] Leaderboard
- [ ] Point redemption options
- [ ] Transaction receipts/history export

## Contributing

Contributions are welcome! Please follow these guidelines:
1. Create a feature branch
2. Commit your changes
3. Push to the branch
4. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions, please open an issue on GitHub.
