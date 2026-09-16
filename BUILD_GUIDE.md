# Google Play Points Manager - Build & Release Guide

## 📦 Building the App

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 24+ (API level 24+)
- Java 8 or higher
- Gradle 8.0+

### Build Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/belfadilabdelhak13-crypto/google-play-points-app.git
cd google-play-points-app
```

#### 2. Configure Google Sign-In
1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create a new project or select existing
3. Enable Google Sign-In API
4. Create OAuth 2.0 credentials for Android
5. Add your app's signing certificate SHA-1:
   ```bash
   # Get SHA-1 for debug
   ./gradlew signingReport
   ```
6. Download `google-services.json` and place in `app/` directory
7. Update CLIENT_ID in `LoginActivity.kt`:
   ```kotlin
   val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
       .requestIdToken("YOUR_GOOGLE_CLIENT_ID")  // Replace this
       .requestEmail()
       .build()
   ```

#### 3. Open in Android Studio
```bash
# Option 1: Command line
studio . &

# Option 2: Through Android Studio GUI
# File → Open → Select project folder
```

#### 4. Build Debug APK
```bash
# Using Gradle
./gradlew clean assembleDebug

# Or using Android Studio
# Build → Build Bundle(s)/APK(s) → Build APK(s)
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

#### 5. Install on Device/Emulator
```bash
./gradlew installDebug

# Or manually
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔐 Building Release APK

### Step 1: Create Keystore

```bash
# Generate keystore (only do this once)
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias release_key

# You'll be prompted to enter:
# - Keystore password: (enter a strong password)
# - Key password: (same or different)
# - Other details (name, org, city, etc.)
```

### Step 2: Configure Signing in build.gradle

The signing config is already set up. You can provide credentials via:

**Option A: Environment Variables**
```bash
export KEYSTORE_FILE=release.keystore
export KEYSTORE_PASSWORD=your_keystore_password
export KEY_ALIAS=release_key
export KEY_PASSWORD=your_key_password
```

**Option B: gradle.properties**
```properties
KEYSTORE_FILE=release.keystore
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=release_key
KEY_PASSWORD=your_key_password
```

**Option C: Edit app/build.gradle directly**
```gradle
release {
    storeFile file("release.keystore")
    storePassword "your_password"
    keyAlias "release_key"
    keyPassword "your_password"
}
```

### Step 3: Build Release APK

```bash
# Clean build
./gradlew clean build

# Build release APK
./gradlew assembleRelease

# Or build release Bundle (for Google Play)
./gradlew bundleRelease
```

**Output**: 
- APK: `app/build/outputs/apk/release/app-release.apk`
- Bundle: `app/build/outputs/bundle/release/app-release.aab`

### Step 4: Verify APK Signature

```bash
# Check APK signature
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

---

## 📱 Installing Release APK

```bash
# Via adb
adb install app/build/outputs/apk/release/app-release.apk

# Or manually drag to Android Studio emulator
```

---

## 🚀 Publishing to Google Play Store

### Requirements
1. Google Play Developer Account ($25 one-time fee)
2. Signed APK or App Bundle
3. App icon (512x512 PNG)
4. Screenshots (2-8 per device type)
5. App description & privacy policy

### Steps
1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app
3. Fill in app details
4. Go to **Release** → **Production**
5. Upload app bundle or APK
6. Set version and release notes
7. Review and publish

---

## 🔧 Configuration

### Update API Endpoint
Edit `app/src/main/java/com/example/googleplaypoints/di/AppModule.kt`:

```kotlin
fun provideApiService(okHttpClient: OkHttpClient): ApiService {
    return Retrofit.Builder()
        .baseUrl("https://your-api-domain.com/api/")  // Update this
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
```

### Backend API Endpoints Required
```
POST /auth/login
GET  /users/{userId}
GET  /users/{userId}/points
POST /topup/initiate
POST /topup/verify/{transactionId}
```

---

## ✅ Testing Checklist

- [ ] Google Sign-In works
- [ ] Points display correctly
- [ ] Can add points
- [ ] Top-up flow works
- [ ] Billing integration responds
- [ ] Offline mode works
- [ ] Error messages display
- [ ] App handles poor network
- [ ] Database persists data
- [ ] All permissions granted

---

## 🐛 Troubleshooting

### Build Fails
```bash
# Clean and rebuild
./gradlew clean build --stacktrace
```

### Gradle Sync Issues
- File → Sync Now
- Or: `./gradlew --refresh-dependencies`

### Google Sign-In Fails
- Verify SHA-1 in Google Cloud Console
- Check `google-services.json` is in `app/`
- Ensure CLIENT_ID is correct

### App Crashes on Start
- Check Logcat: `adb logcat | grep GooglePlayPoints`
- Ensure `MyApplication` is in manifest
- Verify Hilt annotations

---

## 📊 APK Size & Performance

**Typical Release APK**: 15-20 MB
**Memory Usage**: 50-100 MB (depends on data)
**Min SDK**: API 24 (Android 7.0)
**Target SDK**: API 34 (Android 14)

---

## 📝 Version Management

Update in `app/build.gradle`:
```gradle
defaultConfig {
    versionCode 1      // Increment for each release
    versionName "1.0.0" // Follow semantic versioning
}
```

---

## 📞 Support

For issues:
1. Check GitHub Issues
2. Review Logcat output
3. Verify all configurations
4. Run `./gradlew clean build -v` for verbose output

---

**Your app is now ready for production! 🎉**