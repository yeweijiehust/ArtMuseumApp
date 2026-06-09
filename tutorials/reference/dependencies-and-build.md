# Dependencies and Build System

## Prerequisites

- [Programming, Web, and Android Foundations](../01-foundations/programming-web-android.md)
- [Repository Tour](../00-orientation/repository-tour.md)

## What Gradle Does

Gradle is the project build system. It resolves dependencies, invokes compilers and code generators, merges manifests/resources, runs tests, and packages APKs.

Use the repository’s wrapper:

```powershell
.\gradlew.bat assembleDebug
```

The wrapper makes the build use the project-selected Gradle version rather than relying on a machine-global installation.

## Project Files

| File | Role |
| --- | --- |
| `settings.gradle.kts` | Names the project, includes `:app`, and declares plugin/dependency repositories |
| `build.gradle.kts` | Declares root-level plugins without applying them |
| `gradle/libs.versions.toml` | Central version catalog and dependency aliases |
| `app/build.gradle.kts` | Configures Android app, plugins, SDKs, build types, tests, and dependencies |
| `gradle.properties` | Gradle/Android project settings |
| `gradle/wrapper/*` | Wrapper version and launcher support |

`.kts` means Gradle’s Kotlin DSL: build configuration written with Kotlin syntax.

## Android Build Configuration

Important values in `app/build.gradle.kts`:

- namespace/application ID: `com.yeweijiehust.artmuseum`;
- minimum SDK 26: oldest supported Android API;
- target/compile SDK 36.1;
- Java/Kotlin JVM target 17;
- Compose and generated `BuildConfig` enabled;
- release minification disabled;
- Android-resource-aware local unit tests enabled.

## Plugins

- Android Application: compiles and packages an Android app.
- Kotlin Compose: enables Compose compiler integration.
- Hilt: dependency injection Android integration.
- KSP: Kotlin Symbol Processing for generated code.
- Kotlin Serialization: generates serializers for `@Serializable` classes.
- Roborazzi: screenshot recording/verification tasks.

Code generation is why annotations such as `@Dao`, `@Serializable`, and `@HiltViewModel` can produce substantial behavior without handwritten implementations.

## Runtime Dependency Families

### AndroidX Core and Lifecycle

Android compatibility helpers, lifecycle-aware coroutines/state collection, and ViewModel Compose integration.

### Compose and Material 3

Declarative UI primitives, graphics, previews, Material components, and UI tooling. The Compose BOM aligns compatible Compose library versions.

### Navigation Compose

Route graph, back stack, arguments, and destination-aware ViewModels.

### Hilt

Generated dependency graph and Compose navigation integration.

### Retrofit, OkHttp, and kotlinx Serialization

Typed REST API, HTTP transport/cookies/logging, and JSON conversion.

### Room

SQLite-backed entity/DAO/database generation and Flow-observable queries.

### DataStore Preferences

Asynchronous key-value persistence for endpoint, language, and cookie.

### Coil

Image loading, Compose integration, and OkHttp-backed remote image fetching.

### Media3

Media libraries are declared but no current production source imports them. They are prepared dependencies rather than an active feature.

## Test Dependency Families

- JUnit 4: test structure and runner;
- AndroidX JUnit/Espresso/Compose test: Android and UI testing;
- MockK: mocking library, though current tests mainly use hand-written fakes;
- Truth: readable assertions;
- kotlinx coroutines test and Turbine: coroutine/Flow testing support; current tests use coroutine test directly;
- Robolectric: Android behavior on the local JVM;
- Roborazzi: screenshot capture and comparison;
- Coil test: image-test support, currently prepared.

## Build Variants and Source Sets

The active build types are debug and release. Android merges matching source sets:

```text
src/main + src/debug -> debug app
src/main + src/release (if present) -> release app
```

The debug manifest replaces the main network security config, enabling carefully limited local HTTP support.

## Common Tasks

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat verifyRoborazziDebug
.\gradlew.bat connectedDebugAndroidTest
```

The connected task requires an emulator or physical device. The live JSON contract script is separate because ordinary unit tests intentionally skip live fixtures.

## Project Constraints

`constraints.md` prohibits source-code comments and changes to dependency declarations/build files unless explicitly permitted by the user. Before adding a library, check whether an existing dependency already provides the needed capability.

## Where to Continue

- [Testing and Continuous Integration](../06-quality/testing-and-ci.md)
- [Debugging Guide](../06-quality/debugging.md)
- [Extension Guide](../07-extension/extension-guide.md)
