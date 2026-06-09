# Compose, State, and Navigation

## Prerequisites

- [Kotlin From Zero](../01-foundations/kotlin-from-zero.md)
- [Asynchronous and Reactive Programming](../01-foundations/async-and-reactive.md)
- [Architecture and Data Flow](../03-architecture/architecture-and-data-flow.md)

## Declarative UI

Jetpack Compose is a declarative UI toolkit. Instead of creating widgets and manually changing them, a composable function describes what should be visible for current inputs.

```kotlin
@Composable
fun EmptyState(message: String) {
    Text(message)
}
```

`@Composable` marks a function that participates in Compose. If `message` changes, Compose may call the function again. That process is **recomposition**.

## State Determines UI

The detail screen chooses a branch from `DetailUiState`:

```kotlin
when {
    state.loading -> LoadingState()
    state.error != null -> ErrorState(state.error!!, viewModel::load)
    state.image != null -> { /* render detail */ }
}
```

Business purpose: every visible state has an intentional UI. Implementation logic: the first true branch renders.

The `viewModel::load` syntax is a function reference. It passes the ViewModel’s `load` function as the retry callback without calling it immediately.

## Local State Versus ViewModel State

Compose-local state is appropriate for temporary UI input:

```kotlin
var email by remember { mutableStateOf("") }
```

- `mutableStateOf` creates observable Compose state.
- `remember` preserves it across recompositions.
- `by` delegates reads and writes.

The authentication form keeps current text locally. Submission progress, success, and errors live in `AuthViewModel` because they represent an asynchronous workflow and must survive recomposition.

Rule of thumb:

- local visual/form interaction: Compose state;
- workflow, data, loading, or shared state: ViewModel.

## Layout

Common layout composables:

- `Column`: vertical children;
- `Row`: horizontal children;
- `Box`: overlapping/positioned children;
- `LazyColumn`: scrolling list that composes visible items;
- `LazyVerticalGrid`: scrolling grid;
- `Scaffold`: top bar, bottom bar, and content structure.

A `Modifier` configures layout, interaction, semantics, and appearance:

```kotlin
Modifier.fillMaxWidth().padding(16.dp)
```

Modifier order can matter. Each call wraps or transforms the prior result.

## Adaptive Gallery

```kotlin
columns = GridCells.Adaptive(164.dp)
```

The grid creates as many columns as fit while targeting a minimum cell width of 164 density-independent pixels. `dp` is an Android layout unit that scales across screen densities.

Artwork images calculate an aspect ratio from dimensions, then constrain height to avoid extreme cards.

## Lists and Stable Keys

```kotlin
items(state.images, key = { it.id }) { image ->
    MuseumImageCard(image, onImage)
}
```

A stable key tells Compose which visual item corresponds to which artwork when a list changes. This improves state preservation and update correctness.

## Effects

Some operations are not pure rendering:

- navigate after success;
- redirect a protected route;
- monitor scroll position;
- copy loaded image fields into edit-form state.

`LaunchedEffect(key)` runs a coroutine tied to the composable and restarts when the key changes.

Effects should not be used for ordinary display logic. Otherwise recomposition can accidentally repeat work.

## CompositionLocal and Localization

A `CompositionLocal` makes a value available to a composable subtree without passing it through every function parameter.

`ArtMuseumApp` chooses `AppStrings` and provides it:

```kotlin
CompositionLocalProvider(LocalAppStrings provides strings) {
    // screens
}
```

Screens read `LocalAppStrings.current`. The typed `AppStrings` class ensures English and Chinese definitions provide the same fields.

Alternative: Android string resources are conventional and offer mature locale tooling. This project uses a typed Compose provider to keep language override behavior explicit and immediate.

## Navigation

Navigation Compose maps route strings to screens:

```kotlin
composable("detail/{id}") {
    DetailScreen(viewModel = hiltViewModel())
}
```

`{id}` is a route argument. Navigating to `detail/abc123` creates a destination whose `SavedStateHandle` contains `"id" -> "abc123"`.

Top-level routes:

- `gallery`;
- `upload`;
- `mine`;
- `settings`.

Nested/workflow routes:

- `detail/{id}`;
- `edit/{id}`;
- `login/{destination}`;
- `register/{destination}`.

## Retaining Intended Destination

When a signed-out user selects Upload, the app navigates to `login/upload`. After login, the destination argument sends them to Upload.

This is a small but important product behavior: authentication interrupts the workflow without discarding the user’s intent.

## Protected Redirects

Protection exists in two places:

- bottom-bar click chooses login for protected destinations;
- route content checks user again and invokes `ProtectedRedirect`.

The second check covers direct or restored navigation. UI guarding improves experience; server authorization remains the security boundary.

## Accessibility

`AsyncImage` uses `altText` when available and title as fallback for `contentDescription`. Navigation icons have localized descriptions. Compose semantics and `testTag` also support testing and accessibility tooling.

An extension should preserve meaningful content descriptions and avoid using test tags as visible behavior.

## Material 3 and Theme

`ArtMuseumTheme` selects a light or dark Material color scheme based on system theme. Screens use `MaterialTheme.colorScheme` and typography instead of hard-coded local styles.

## Next

- [Networking and Serialization](networking-and-serialization.md)
- [App Startup and Navigation](../05-walkthroughs/app-startup-and-navigation.md)
