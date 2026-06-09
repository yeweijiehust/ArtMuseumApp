# Kotlin From Zero

## Prerequisites

Read [Programming, Web, and Android Foundations](programming-web-android.md) first.

Kotlin is the programming language used by every production and test source file in this repository. This lesson explains the specific language features you will encounter.

## Values, Variables, and Types

```kotlin
val title: String = "Morning Light"
var refreshing: Boolean = true
```

- `val` declares a read-only reference. It cannot be reassigned.
- `var` declares a reference that can be reassigned.
- `title` and `refreshing` are names.
- `String` and `Boolean` are types.
- `=` assigns the initial value.

Kotlin often infers the type:

```kotlin
val title = "Morning Light"
```

The repository prefers `val` because limiting mutation makes behavior easier to reason about.

## Functions

```kotlin
fun title(value: String) = value.trim().length in 1..120
```

- `fun` begins a function declaration.
- `title` is the function name.
- `value: String` is a parameter.
- `=` introduces a one-expression function body.
- The result is a `Boolean`, inferred by Kotlin.

`value.trim()` removes leading and trailing whitespace. `.length` reads a property. `in 1..120` tests whether the length is between 1 and 120, inclusive.

## Classes and Objects

A class is a blueprint for objects:

```kotlin
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: String
)
```

`data class` asks Kotlin to generate useful value behavior such as equality, readable text output, and `copy`. The properties in parentheses form the primary constructor.

`User` models a business concept. DTO and database classes are also data classes because their purpose is mainly to carry values.

An `object` creates exactly one instance:

```kotlin
object Validators
```

This is appropriate because validators hold stateless functions and constants.

## Constructors and Dependency Injection Syntax

```kotlin
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()
```

Read it from left to right:

- declare class `AuthViewModel`;
- annotate its constructor with `@Inject`, so Hilt may call it;
- accept an `AuthRepository`;
- store it as a private read-only property;
- inherit from `ViewModel`.

The colon means inheritance or interface implementation. Learn why the constructor receives an interface in [Dependency Injection with Hilt](../03-architecture/dependency-injection.md).

## Interfaces

An interface describes a contract without choosing an implementation:

```kotlin
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String)
}
```

Any class implementing `AuthRepository` must provide these members. Production uses `AuthRepositoryImpl`; tests can use a fake. This supports dependency inversion and testability.

`override` marks an implementation of a contract:

```kotlin
override suspend fun login(email: String, password: String) { ... }
```

## Nullability

Kotlin distinguishes a guaranteed value from an optional one:

```kotlin
val title: String
val description: String?
```

`String?` may contain a string or `null`. The compiler requires safe handling:

```kotlin
image.description ?: strings.noDescription
```

The Elvis operator `?:` uses the right side when the left side is `null`.

Other null syntax used here:

- `value?.method()`: call only when non-null;
- `value?.let { ... }`: run a block only when non-null;
- `value!!`: assert non-null or crash; use cautiously;
- `checkNotNull(value)`: fail clearly if a required value is missing;
- `orEmpty()`: turn nullable text into an empty string.

## Collections and Lambdas

`List<MuseumImage>` means an ordered list of artwork values.

A lambda is an unnamed function:

```kotlin
items.map { it.toDomain() }
```

`map` transforms every item. Inside a one-parameter lambda, `it` is the default parameter name. This converts a list of DTOs or entities into domain models.

An explicit lambda can name parameters:

```kotlin
items.forEachIndexed { index, image ->
    dao.upsert(image.toEntity(publicPosition = index))
}
```

The arrow separates parameters from the body.

## Control Flow

```kotlin
when {
    state.loading -> LoadingState()
    state.error != null -> ErrorState(state.error!!)
    state.image != null -> ShowImage(state.image!!)
}
```

`when` selects the first matching branch. This is how screens choose loading, error, empty, or content UI.

A value-based `when` is also common:

```kotlin
when (error) {
    UiError.Offline -> strings.offline
    UiError.Timeout -> strings.timeout
    else -> strings.genericError
}
```

`if` is an expression in Kotlin and can produce a value:

```kotlin
val strings = if (deviceChinese) Chinese else English
```

## Enums, Sealed Classes, and Exhaustiveness

An enum represents one choice from a fixed set:

```kotlin
enum class AppLanguage { Device, English, Chinese }
```

A sealed class represents a closed family whose variants may carry different data:

```kotlin
sealed class AppFailure(message: String) : Exception(message) {
    data object Offline : Network("OFFLINE")
    data class Api(val code: String, val detail: String) : AppFailure(detail)
}
```

`Offline` needs no extra values, so it is a `data object`. `Api` carries a server code and detail, so it is a `data class`. The compiler can verify that a `when` covers all variants.

## Extension Functions

An extension function looks like a method on an existing type:

```kotlin
fun ImageDto.toDomain() = MuseumImage(...)
```

It does not modify `ImageDto`. It defines a conversion function whose receiver is an `ImageDto`, allowing `dto.toDomain()`.

This repository uses extensions to keep representation conversion near the relevant data type.

## Annotations

Annotations attach metadata for tools or frameworks:

```kotlin
@Serializable
data class HealthDto(...)
```

`@Serializable` lets the serialization compiler plugin generate JSON conversion code.

Other examples include `@Composable`, `@HiltViewModel`, `@Entity`, `@Dao`, `@GET`, and `@Test`. Their meanings are explained in the relevant framework lessons.

## Generics

Generics let one abstraction work with different types:

```kotlin
suspend fun <T : Any> apiCall(
    json: Json,
    block: suspend () -> Response<T>
): T
```

`T` stands for a type chosen at the call site. `T : Any` requires a non-null type. The function can handle `HealthDto`, `ImageDto`, or another response body while preserving its exact type.

## Exceptions and `runCatching`

An exception represents a failed operation. `throw` stops normal execution and propagates failure:

```kotlin
throw AppFailure.InvalidResponse
```

`try` / `catch` handles selected exceptions. `runCatching { ... }` wraps success or failure in a `Result`:

```kotlin
runCatching { repository.refreshPublic() }
    .onSuccess { ... }
    .onFailure { ... }
```

ViewModels use this pattern to turn repository failures into visible UI state.

## Next

Read [Asynchronous and Reactive Programming](async-and-reactive.md). It explains `suspend`, coroutines, and `Flow`, which are essential for understanding repositories and ViewModels.
