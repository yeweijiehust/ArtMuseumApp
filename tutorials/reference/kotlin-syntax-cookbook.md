# Kotlin Syntax Cookbook

## Prerequisites

Read [Kotlin From Zero](../01-foundations/kotlin-from-zero.md) first. This page is a quick lookup for syntax that appears in the repository.

## `val` and `var`

```kotlin
val endpoint = "https://example.com"
var attempted = false
```

`val` cannot be reassigned. `var` can.

## Nullable Types and Operators

```kotlin
val description: String? = null
val shown = description ?: "No description"
val length = description?.length
```

- `?` on a type allows null.
- `?:` supplies a fallback.
- `?.` safely accesses only when non-null.

## String Templates

```kotlin
val url = "$endpoint/api/images/$id"
val label = "${image.width} × ${image.height}"
```

`$name` inserts a value. `${expression}` inserts an expression.

## Ranges and Membership

```kotlin
value.length in 1..120
mimeType in supportedImages
```

`..` creates an inclusive range. `in` tests membership.

## Named and Default Arguments

```kotlin
image.toEntity(publicPosition = index, minePosition = mine)
```

Names clarify argument purpose. A parameter declaration such as `publicPosition: Int? = null` makes the argument optional.

## Lambdas

```kotlin
items.map { it.toDomain() }

items.forEachIndexed { index, image ->
    dao.upsert(image.toEntity(publicPosition = index))
}
```

Braces contain an unnamed function. `it` is the implicit single parameter. The arrow follows explicit parameters.

## Function References

```kotlin
Button(onClick = viewModel::refresh)
```

`::` creates a reference to a function without calling it.

## Scope Functions

```kotlin
state.image?.let {
    title = it.title
}
```

`let` runs when the nullable receiver is non-null and exposes it as `it`.

```kotlin
OkHttpClient.Builder().apply {
    addInterceptor(...)
}.build()
```

`apply` runs configuration using the receiver as `this`, then returns that receiver.

```kotlin
resolver.openInputStream(uri)?.use { it.readBytes() }
```

`use` closes the resource after the block.

## `when`

```kotlin
val prompt = when (error) {
    UiError.Offline -> strings.offline
    else -> strings.genericError
}
```

`when` selects a branch and can return a value.

## `runCatching`

```kotlin
runCatching { repository.refreshPublic() }
    .onSuccess { page -> ... }
    .onFailure { error -> ... }
```

Wraps thrown failure or returned success in `Result`.

## Data-Class `copy`

```kotlin
state.value = state.value.copy(loading = false, error = failure.toUiError())
```

Creates a new value with selected fields changed.

## Extension Functions

```kotlin
fun ImageDto.toDomain() = MuseumImage(...)
```

Defines a callable conversion as if it were a method on `ImageDto`.

## Generics

```kotlin
suspend fun <T : Any> apiCall(block: suspend () -> Response<T>): T
```

`T` is a type parameter. This function works with many non-null response-body types.

## Property Delegation

```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()
var email by remember { mutableStateOf("") }
```

`by` delegates property reads/writes to another object, letting Compose state feel like an ordinary value.

## Callable Types

```kotlin
onRetry: (() -> Unit)?
onImage: (String) -> Unit
```

- `() -> Unit`: function with no input and no meaningful return.
- `(String) -> Unit`: function accepting a string.
- trailing `?`: optional callback.

## Inheritance and Interface Implementation

```kotlin
class MainActivity : ComponentActivity()
class AuthRepositoryImpl : AuthRepository
```

The colon indicates a parent class or implemented interface.

## Annotations

```kotlin
@Composable
@Serializable
@Test
@GET
```

Annotations provide metadata interpreted by Compose, serialization, tests, Retrofit, and other tools.

## Object Expressions

```kotlin
object : Statement() {
    override fun evaluate() { ... }
}
```

Creates an anonymous object extending or implementing a type. `MainDispatcherRule` uses one to wrap test execution.

## Companion Objects

```kotlin
companion object {
    const val DEFAULT_ENDPOINT = "..."
}
```

A companion stores members associated with the class rather than each instance.

## Visibility

- `private`: only the containing declaration/file can use it.
- `internal`: visible within the module.
- public is the default.

## Further Reading

Return to [Kotlin From Zero](../01-foundations/kotlin-from-zero.md) for explanations and [Asynchronous and Reactive Programming](../01-foundations/async-and-reactive.md) for coroutine/Flow syntax.
