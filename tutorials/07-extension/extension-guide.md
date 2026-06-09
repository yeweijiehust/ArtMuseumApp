# Extension Guide

## Prerequisites

- [Architecture and Data Flow](../03-architecture/architecture-and-data-flow.md)
- [Packages and Responsibilities](../03-architecture/package-map.md)
- [Testing and Continuous Integration](../06-quality/testing-and-ci.md)

This guide shows how to add behavior while preserving the project’s architecture and constraints.

## Before Editing

1. Read `constraints.md`.
2. Identify the user-visible behavior and failure cases.
3. Trace the closest existing workflow.
4. Decide which layers genuinely need change.
5. Choose tests based on risk and observable behavior.
6. Avoid unrelated refactors and forbidden dependency/build changes.

## Feature Design Template

Answer these questions:

- What business concept or action is being added?
- Is it public or protected?
- What API route/JSON shape exists?
- Does it need offline/cache behavior?
- What UI states exist: loading, content, empty, error, success?
- Which errors need distinct prompts?
- Does it affect navigation, localization, or endpoint isolation?
- What evidence will prove it works?

## Adding a New Read-Only API Field

Example: add a `medium` field to artwork.

1. Confirm it exists in OpenAPI and live JSON.
2. Add it to `ImageDto`, with correct nullability/type.
3. Add it to `MuseumImage` if it is business-relevant.
4. Add it to `ImageEntity` if offline display needs it.
5. Update DTO/domain/entity mapping functions.
6. Bump Room database version and define migration if shipping to existing users.
7. Render it in relevant screen.
8. Extend live deserialization and visible behavior tests.

Current database version is 1 and has no migration strategy. Schema changes require careful migration planning.

## Adding a New API Operation

1. Add request/response DTOs.
2. Add Retrofit method to `ArtMuseumApi`.
3. Add a domain repository operation only if upper layers need it.
4. Implement it in the data repository.
5. Map stable failures.
6. Add ViewModel state/action.
7. Add Compose UI and navigation.
8. Add localization fields for every visible string.
9. Add behavior and contract tests.

If orchestration spans several repositories or must be reused, consider introducing a domain use-case class rather than putting complex policy in a ViewModel.

## Adding a New Screen

Define:

- route and arguments;
- protected/public behavior;
- ViewModel ownership;
- immutable UI state;
- events;
- loading/empty/error/content branches;
- navigation after success;
- phone/tablet layout;
- accessibility descriptions;
- tests.

Add the destination in `ArtMuseumApp.kt`. Use `SavedStateHandle` for route arguments in ViewModels.

## Adding a New Error Category

Follow [Endpoint Changes and Error Prompts](../05-walkthroughs/endpoint-and-errors.md#adding-a-new-prompt). Preserve stable backend codes, map once per layer, add both languages, and test mapping plus visible text.

## Adding a New Setting

1. Add domain representation if needed.
2. Add DataStore key and observable Flow.
3. expose through a repository contract;
4. include it in top-level state;
5. render control in Settings;
6. apply it at the appropriate composition or data boundary;
7. test persistence and visible effect.

Use segmented controls for small exclusive choices, switches for binary settings, and inputs only when free-form text is required.

## Extending Offline Behavior

Read-only cache additions should define:

- source of truth;
- freshness policy;
- merge rules;
- invalidation;
- behavior for network failure versus explicit server failure;
- endpoint isolation.

Offline mutations are a substantially larger feature. They require a durable operation queue, retry policy, idempotency, conflict handling, and visible pending/failed states.

## Adding a Dependency

The project constraints prohibit modifying `libs.versions.toml` and Gradle files without user action. First check whether Android, Kotlin, or an existing dependency already provides the capability. If a new dependency is truly necessary, explain why and ask the user to add it.

## Architecture Checklist

- Domain package does not import Android/Retrofit/Room/Compose.
- Presentation depends on repository contracts, not implementations.
- Data representations are explicitly mapped.
- Server remains authority for authentication/authorization.
- Long work runs in coroutines, not blocking UI.
- Flow/StateFlow ownership is clear.
- Endpoint change cannot leak session/cache across servers.
- UI has intentional loading, empty, error, and content behavior.

## Verification Checklist

Run focused tests while developing, then:

```powershell
.\scripts\run-live-contract-tests.ps1
.\gradlew.bat testDebugUnitTest verifyRoborazziDebug lintDebug assembleDebug connectedDebugAndroidTest
```

Connected tests require an emulator/device. Review generated UI and prompts in both languages and dark/light themes when relevant.

## Example: Add Favorites

Favorites look simple but reveal design decisions:

- Is favorite state server-owned or local-only?
- Must it sync across devices?
- Can signed-out users favorite?
- Should favorites work offline?
- Is it another cached ordered collection?

If server-owned, it needs API contract, authentication, DTOs, repository operations, state, UI, failure handling, and contract tests. If local-only, it needs a Room schema/migration and endpoint/account isolation policy. Resolve the product meaning before coding.
