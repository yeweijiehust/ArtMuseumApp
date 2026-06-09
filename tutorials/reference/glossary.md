# Glossary

Use this page whenever a lesson introduces unfamiliar vocabulary. Links point to the main teaching document for each idea.

## A

**Activity**
An Android component that hosts a top-level user interface. `MainActivity` hosts Compose. See [App Startup and Navigation](../05-walkthroughs/app-startup-and-navigation.md).

**Annotation**
Metadata beginning with `@` that a compiler, framework, or test runner interprets, such as `@Composable`, `@Serializable`, or `@Test`. See [Kotlin From Zero](../01-foundations/kotlin-from-zero.md#annotations).

**API**
Application Programming Interface. A defined way for programs to communicate. Here it usually means the ArtMuseum HTTP routes. See [API, JSON, and Authentication](../02-domain/api-json-auth.md).

**APK**
Android application package installed on a device.

**Authentication**
Establishing who the user is. Login and session restoration authenticate. See [Authentication Walkthrough](../05-walkthroughs/authentication.md).

**Authorization**
Deciding what an authenticated user is allowed to do, such as editing only owned artwork.

## B

**Backend**
The remote server-side ArtMuseum service.

**Build type**
A configured variation such as debug or release. Debug permits limited local HTTP and logging.

## C

**Cache**
A local copy used for speed or resilience. Room caches artwork metadata; Coil caches image data. See [Persistence, Cache, and Images](../04-frameworks/persistence-cache-images.md).

**Client**
A program that requests data/actions from a server. This Android app is a client.

**Clean Architecture**
An architectural style that protects business rules from framework details through layers and dependency direction. See [Architecture and Data Flow](../03-architecture/architecture-and-data-flow.md).

**Coil**
The image-loading library used by `AsyncImage`.

**Composable**
A function marked `@Composable` that describes UI in Jetpack Compose. See [Compose, State, and Navigation](../04-frameworks/compose-state-navigation.md).

**CompositionLocal**
A Compose mechanism for providing values to a subtree without passing each parameter manually. Used for localized strings.

**Coroutine**
A lightweight asynchronous task that can suspend without blocking a thread. See [Asynchronous and Reactive Programming](../01-foundations/async-and-reactive.md).

**Cookie**
Small server-associated data sent with matching HTTP requests. `am_session` authenticates this app.

**Cursor pagination**
Fetching a list in pages using an opaque server-provided continuation token. See [Art Museum Domain](../02-domain/art-museum-domain.md#pagination-and-cursors).

## D

**DAO**
Data Access Object. A Room interface that declares database queries and updates.

**DataStore**
Android persistence library used for small key-value preferences.

**Dependency**
Something a class or build needs. It can mean another object or an external library.

**Dependency injection**
Giving objects their dependencies from outside rather than constructing them internally. See [Dependency Injection with Hilt](../03-architecture/dependency-injection.md).

**Deserialization**
Converting text such as JSON into typed objects.

**Domain**
The real-world problem concepts a program models. Here: artwork, galleries, ownership, and accounts.

**DTO**
Data Transfer Object. A type shaped for network data, such as `ImageDto`.

## E

**Entity**
A Room class mapped to a database table. `ImageEntity` maps to `images`.

**Endpoint**
A server base URL, or sometimes a specific API route. This app can replace its compatible base endpoint.

**Exception**
A value representing failed execution that can be thrown and caught.

## F

**Flow**
Kotlin asynchronous stream that can emit multiple values over time.

## G

**Gradle**
The build system used to compile, test, and package the app. See [Dependencies and Build System](dependencies-and-build.md).

## H

**Hilt**
Android-oriented dependency injection framework built on Dagger.

**HTTP**
Request-response protocol used between the app and server.

## I

**Immutable state**
State represented by values that are replaced rather than changed in place. UI state classes use `copy`.

**Interface**
A contract describing members an implementation must provide.

## J

**JSON**
Text format used for API request and response data.

**Jetpack Compose**
Android declarative UI toolkit used by this app.

## K

**Kotlin**
Programming language used by the project.

**KSP**
Kotlin Symbol Processing. A compile-time mechanism used by Hilt and Room code generation.

## L

**Lifecycle**
The creation, active, inactive, and destruction states of Android components and ViewModels.

## M

**Manifest**
Android XML document declaring app components, permissions, and configuration.

**Mapper**
Code that converts one representation to another, such as `ImageDto.toDomain`.

**MIME type**
A standardized content type such as `image/jpeg`.

**Module**
A buildable Gradle unit. This repository has one Android `app` module.

**Multipart**
HTTP body format that sends several named parts, used for file upload.

## N

**Navigation back stack**
Ordered history of destinations used for back navigation.

**Null**
Absence of a value. Kotlin marks optional types with `?`.

## O

**Observable state**
State whose changes can notify consumers. Flow, StateFlow, and Compose state are observable.

**Offline browsing**
Displaying cached content without a working network. Mutations remain online-only.

**OkHttp**
HTTP client used beneath Retrofit.

**OpenAPI**
Machine-readable description of an HTTP API contract.

## P

**Package**
A namespace and organizational grouping for Kotlin source.

**Photo Picker**
Android system UI for selecting media without broad storage access.

**Presentation layer**
UI and UI-state coordination code.

## R

**Reactive programming**
Programming with streams and observable values that propagate changes.

**Recomposition**
Compose re-running relevant composable functions when observed state changes.

**Repository**
An abstraction exposing data/business capabilities while hiding source details.

**Retrofit**
Library that implements annotated HTTP API interfaces.

**Room**
Android persistence library over SQLite.

**Route**
A Navigation Compose destination string, or an HTTP path depending on context.

## S

**Serialization**
Converting typed objects into transport/storage text such as JSON.

**Session**
Server-recognized authenticated interaction continuity, represented here by a cookie.

**Singleton**
One shared object instance within a dependency graph scope.

**Source set**
Files included for a build/testing purpose, such as `main`, `debug`, `test`, or `androidTest`.

**StateFlow**
A Flow that always has a current value, used for UI state.

**Suspend function**
A function that may pause and resume within Kotlin coroutines.

## T

**Transaction**
A group of database operations that succeeds or fails atomically.

**Typed failure**
A failure represented by a known class/variant instead of arbitrary text.

## U

**Unidirectional data flow**
State flows down to UI; user events flow up to state owners.

**URI**
Identifier for a resource. Photo Picker returns content URIs rather than raw file paths.

**Upsert**
Insert a record if absent or update it if present.

## V

**ViewModel**
Android lifecycle-aware owner of screen/workflow state.

## W

**Web service**
A server exposing functionality through network APIs.
