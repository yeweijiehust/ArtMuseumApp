# Art Museum Android App

Art Museum Android App is a native Android frontend compatible with the
[ArtMuseum service](https://github.com/yeweijiehust/ArtMuseum). It provides a
bilingual, image-focused museum experience for browsing public artwork and
managing a personal collection.

The app connects to the production service by default:

- API base URL: [https://artmuseum-w9mm.onrender.com](https://artmuseum-w9mm.onrender.com)
- Live OpenAPI document: [https://artmuseum-w9mm.onrender.com/api/docs/json](https://artmuseum-w9mm.onrender.com/api/docs/json)
- Android application ID: `com.yeweijiehust.artmuseum`
- Minimum Android version: Android 8.0 / API 26

## Features

### Public museum

- Browse an adaptive grid of publicly shared artwork.
- Pull to refresh and load additional cursor-paginated results.
- Open artwork details including title, artist, description, dimensions,
  format, and file size.
- Continue browsing cached gallery metadata when the service is unavailable.
- Load and cache remote artwork images with Coil.

### Accounts and personal museum

- Register, log in, restore a saved session, and log out.
- Persist the `am_session` authentication cookie with DataStore.
- Redirect protected workflows to login while leaving the public gallery
  available.
- Browse artwork owned by the signed-in account.
- Edit artwork metadata and delete artwork after confirmation.

### Uploads

- Select an image with the Android Photo Picker.
- Preview an image before upload.
- Upload JPEG, PNG, or WebP images with title, description, and accessibility
  text.
- Validate the API text limits and the 10 MiB image limit before submission.

### Settings and experience

- Use English, Simplified Chinese, or the device language.
- Use Material 3 light and dark themes.
- Replace the API endpoint after a successful `/api/health` check.
- Clear the stored session and cached gallery when changing endpoints.
- Show actionable bilingual prompts for credentials, validation, permissions,
  missing content, connectivity, timeout, rate limiting, server, storage, and
  endpoint failures.

## ArtMuseum API Compatibility

This app is designed for the routes and response models exposed by the
ArtMuseum backend:

| Capability | API route |
| --- | --- |
| Health check | `GET /api/health` |
| Register | `POST /api/auth/register` |
| Login | `POST /api/auth/login` |
| Current account | `GET /api/auth/me` |
| Logout | `POST /api/auth/logout` |
| Public gallery | `GET /api/images` |
| Artwork details | `GET /api/images/{id}` |
| Personal museum | `GET /api/images/mine` |
| Upload artwork | `POST /api/images` |
| Edit artwork | `PATCH /api/images/{id}` |
| Delete artwork | `DELETE /api/images/{id}` |

Retrofit uses absolute URLs so the endpoint can be changed at runtime. Release
builds require HTTPS. Debug builds additionally allow local HTTP endpoints on
`localhost`, `127.0.0.1`, and Android emulator host alias `10.0.2.2`.

## Architecture

The single `app` module follows a Clean Architecture-style package layout:

```text
com.yeweijiehust.artmuseum
|-- data
|   |-- local          Room database, DAO, and cached image metadata
|   |-- preferences    DataStore endpoint, language, and session preferences
|   |-- remote         Retrofit API, serializers, cookies, and failure mapping
|   `-- repository     Repository implementations and cache coordination
|-- di                 Hilt dependency modules
|-- domain
|   |-- model          Domain models and typed failures
|   |-- repository     Repository contracts
|   `-- validation     Input and upload validation
`-- presentation
    |-- localization   Typed English and Simplified Chinese strings
    |-- theme          Material 3 theme
    |-- ui             Compose screens and shared states
    `-- viewmodel      Screen state and workflow coordination
```

Important implementation choices:

- Jetpack Compose and Navigation Compose for the UI and navigation.
- Hilt for dependency injection.
- Retrofit, OkHttp, and kotlinx serialization for API communication.
- Room for read-only offline gallery metadata.
- DataStore for endpoint, language, and session-cookie persistence.
- Coil for image loading and memory/disk caching.
- Coroutines and Flow for asynchronous work and observable state.

Offline content is read-only. Upload, edit, and delete operations require a
working network connection and are not queued for later synchronization.

## Getting Started

### Prerequisites

- Android Studio with Android SDK 36 installed.
- JDK 17.
- An Android device or emulator running API 26 or newer.
- Network access to the configured ArtMuseum service.

### Build and run

1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync the prepared dependencies.
4. Select an API 26+ device or emulator.
5. Run the `app` configuration.

The default production endpoint is already configured. To use another
compatible ArtMuseum deployment, open Settings, enter its base URL, and choose
**Test and save**. The app changes endpoints only when the health response
identifies a compatible `artmuseum-api` service.

Build a debug APK from PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

The generated APK is written under `app/build/outputs/apk/debug/`.

## Testing

The project includes:

- Unit tests for validators, network/API failure mapping, and ViewModel
  behavior.
- Compose/Robolectric tests for bilingual error prompts.
- Roborazzi screenshot regression verification.
- Connected Android tests for application identity and the main navigation
  workflow.
- Live JSON contract tests that download production responses and deserialize
  them with the app's production serializers.

Run the live, non-mutating ArtMuseum contract tests from PowerShell:

```powershell
.\scripts\run-live-contract-tests.ps1
```

The script downloads JSON fixtures for health, gallery, artwork details,
OpenAPI, and an unauthorized account response into the ignored
`app/build/contract-fixtures/` directory. It verifies expected HTTP statuses,
then runs the serializer contract tests. It never performs mutating production
requests.

Run the complete local acceptance suite:

```powershell
.\scripts\run-live-contract-tests.ps1
.\gradlew.bat testDebugUnitTest verifyRoborazziDebug lintDebug assembleDebug connectedDebugAndroidTest
```

`connectedDebugAndroidTest` requires a running emulator or connected device.
Ordinary unit test runs skip the live contract tests unless
`ARTMUSEUM_CONTRACT_FIXTURE_DIR` is set by the contract script.

## Configuration and Generated Data

- `api_doc.json` and `api_docs.json` are intentionally ignored because the
  live OpenAPI document is the source used by contract testing.
- Build output, downloaded contract fixtures, APKs, local SDK settings, and IDE
  files are ignored.
- Changing the API endpoint clears cookies and cached gallery data to prevent
  content or authentication from leaking between servers.

## Backend

The compatible backend source, API behavior, and deployment details live in
the [ArtMuseum repository](https://github.com/yeweijiehust/ArtMuseum).
