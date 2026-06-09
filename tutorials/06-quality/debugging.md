# Debugging Guide

## Prerequisites

- [Repository Tour](../00-orientation/repository-tour.md)
- [Testing and Continuous Integration](testing-and-ci.md)

Debugging is the process of turning a symptom into an evidence-backed cause. Start at the visible failure, identify its layer, and narrow the path.

## First Response Checklist

1. Reproduce the problem consistently.
2. Record exact screen, action, account state, endpoint, and build type.
3. Run the narrowest relevant test or Gradle task.
4. Inspect state and logs at layer boundaries.
5. Change one cause at a time.
6. Add or strengthen a test that proves the fix.

## App Does Not Build

Run:

```powershell
.\gradlew.bat assembleDebug --stacktrace
```

Common categories:

- Kotlin compiler error: file and line usually identify syntax/type issue.
- KSP/Hilt error: missing binding, unsupported injection, or generated-code issue.
- Room error: invalid SQL or entity/DAO mismatch.
- Serialization error: unsupported DTO declaration.
- Dependency resolution: repository/network/version problem.
- Resource merge error: malformed XML or duplicate resource.

Read the first meaningful cause, not only the final “build failed” line.

## Hilt Missing Binding

Symptom: compile error saying a type cannot be provided.

Trace:

1. Is it your class? Add/use `@Inject constructor`.
2. Is it an interface? Add a `@Binds` mapping.
3. Is it built by a library/builder? Add a `@Provides` function.
4. Does its lifetime/scope make sense?
5. Is the module installed in `SingletonComponent` or another correct component?

Review [Dependency Injection with Hilt](../03-architecture/dependency-injection.md).

## Gallery Is Empty

Ask in this order:

1. Is `GalleryViewModel.refresh()` running?
2. Does OkHttp log a successful `/api/images?limit=20` response?
3. Does `ImageListDto` deserialize?
4. Does repository transaction write rows and positions?
5. Does `observePublic()` emit?
6. Does combined `GalleryUiState.images` contain items?
7. Which `GalleryScreen` branch is selected?

Run the live contract script to quickly verify production JSON compatibility.

## Gallery Shows Cached Content and an Error

This may be expected. Cached images plus a footer error indicates Room emitted data while refresh/pagination failed. Inspect the specific `UiError` to distinguish offline, timeout, unreachable, and server errors.

## Detail Fails Offline

Check whether that exact image ID was cached. Offline fallback only works for a previously stored row. It also only triggers for `AppFailure.Network`, not server `NotFound` or authorization errors.

## Login Fails

1. Validate email/password boundaries in the screen.
2. Inspect HTTP status and API error code.
3. Confirm `INVALID_CREDENTIALS` maps specifically.
4. Inspect whether `SessionCookieJar.saveFromResponse` receives `am_session`.
5. Confirm cookie matches endpoint URL.
6. Call `/api/auth/me` through session restoration behavior.

Never log raw passwords or session cookie values.

## User Is Unexpectedly Signed Out

Expected clearing paths:

- logout;
- unauthorized session restoration;
- unauthorized protected repository operation;
- cookie expiration;
- endpoint change.

Trace `SessionCookieJar.clear()` callers and `SessionStore.user.value = null` assignments.

## Endpoint Will Not Save

Check:

- valid URI syntax and host;
- HTTPS for non-local endpoint;
- debug build for local HTTP;
- host is exactly allowed local value;
- `/api/health` is reachable;
- response has `ok=true` and `service=artmuseum-api`;
- Android network security config allows the request.

An HTTP URL can pass only in a debug build and only for listed local hosts.

## Upload Fails

Separate local validation from server failure:

- URI missing;
- title invalid;
- MIME unsupported;
- metadata or actual bytes exceed 10 MiB;
- input stream unavailable;
- session unauthorized;
- API validation error;
- storage service failure;
- post-upload public/personal refresh failure.

The final case can make a successful server upload appear as an operation failure because refreshes run before the repository returns.

## Wrong Error Prompt

Trace the pipeline:

```text
exception/status/API code
-> mapNetworkFailure or mapApiFailure
-> AppFailure
-> Throwable.toUiError
-> ErrorState
-> AppStrings language
```

Add a focused mapping test and visible prompt test.

## Screenshot Verification Fails

Inspect generated actual and compare images under `app/build/outputs/roborazzi`.

- If change is accidental, fix UI.
- If intentional and reviewed, update baseline using the repository’s Roborazzi recording workflow, then verify again.
- Check font/environment differences before accepting a baseline.

## Connected Test Fails

Check:

- emulator/device is available and API-compatible;
- app installed and network accessible;
- test waited for asynchronous startup;
- selectors use stable semantics/test tags;
- failure is not production endpoint availability.

## CI-Only Failure

Compare CI environment:

- Windows vs Ubuntu job;
- JDK version;
- Android SDK packages;
- clean checkout without local cache;
- network availability;
- tracked Roborazzi baseline;
- Gradle wrapper timing.

CI is valuable because it reveals assumptions hidden by a prepared local machine.

## Useful Commands

```powershell
git status --short
.\gradlew.bat testDebugUnitTest --stacktrace
.\gradlew.bat lintDebug
.\gradlew.bat verifyRoborazziDebug
.\scripts\run-live-contract-tests.ps1
adb devices
```

## Debugging Habit

Avoid guessing across layers. At every boundary, ask: “What value entered? What value left? What evidence proves it?”
