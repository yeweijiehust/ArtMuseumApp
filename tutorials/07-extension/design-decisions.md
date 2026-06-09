# Design Decisions and Alternatives

## Prerequisites

- [Architecture and Data Flow](../03-architecture/architecture-and-data-flow.md)
- [Dependencies and Build System](../reference/dependencies-and-build.md)

Architecture is a set of trade-offs, not a collection of universally correct patterns. This document explains why the project’s choices fit its current size and where alternatives may become better.

## Single Module, Layered Packages

**Current choice:** one `app` Gradle module with domain/data/di/presentation packages.

**Why it fits:** small project, fast navigation, simple build, clear enough package boundaries.

**Alternative:** separate Gradle modules for domain, data, feature UI, and core libraries.

**When to reconsider:** larger team, stronger compile-time boundary enforcement, reusable domain, or slower builds.

## Repository Interfaces Without Explicit Use Cases

**Current choice:** ViewModels call focused repository interfaces directly.

**Why it fits:** workflows are mostly one repository action and orchestration is small.

**Alternative:** one use-case/interactor class per action.

**When to reconsider:** reusable policy, cross-repository orchestration, complex authorization, or growing ViewModels.

## Room as Gallery List Source of Truth

**Current choice:** remote refresh writes Room; UI observes Room.

**Why it fits:** seamless cached content and one stream for online/offline lists.

**Alternative:** UI state directly owns network results and reads cache only on failure.

**Trade-off:** direct ownership is simpler initially but duplicates state paths and makes stale/cache merging harder.

## Two Nullable Position Columns

**Current choice:** `publicPosition` and `minePosition` on each image row.

**Why it fits:** exactly two lists and simple queries.

**Alternative:** normalized collection-membership table.

**When to reconsider:** many lists, filters, favorites, categories, independent pagination, or richer ordering.

## Runtime-Switchable Endpoint

**Current choice:** absolute Retrofit URLs sourced from DataStore.

**Why it fits:** compatible deployments can be selected without rebuilding.

**Alternative:** fixed build-time endpoint with relative Retrofit paths.

**Trade-off:** fixed endpoints simplify URLs and cookie/server assumptions. Runtime switching requires validation and strict session/cache clearing.

## Cookie Authentication

**Current choice:** persistent `am_session` cookie through custom OkHttp `CookieJar`.

**Why it fits:** matches backend contract and automatically attaches requests.

**Alternatives:** bearer token header, platform account manager, OAuth/OpenID Connect.

**When to reconsider:** backend auth model changes, multi-account support, external identity providers, or stronger credential storage requirements.

## Typed In-App Localization

**Current choice:** `AppStrings` and `CompositionLocal`.

**Why it fits:** explicit instant device/English/Chinese override inside Compose.

**Alternative:** Android locale resources and per-app locale APIs.

**Trade-off:** Android resources provide stronger tooling, plurals, and translator workflows. Typed objects give compile-time field parity but become unwieldy as languages and strings grow.

## Full ByteArray Upload

**Current choice:** read selected image fully into memory, capped at 10 MiB.

**Why it fits:** simple multipart construction and bounded payload.

**Alternative:** stream RequestBody from ContentResolver.

**When to reconsider:** larger files, lower-memory devices, progress reporting, resumable upload.

## Read-Only Offline Support

**Current choice:** cached browsing only; mutations require network.

**Why it fits:** predictable behavior and no synchronization conflicts.

**Alternative:** offline mutation queue.

**Cost of alternative:** durable queue, retries, idempotency, conflict rules, pending/error UI, and server support.

## Error Categories Instead of Raw Messages

**Current choice:** map technical failures to typed categories and localized prompts.

**Why it fits:** consistent, actionable, bilingual UX and no exposure of arbitrary server text.

**Alternative:** display server message directly.

**Trade-off:** raw messages require less mapping but may be inconsistent, insecure, unlocalized, or unactionable.

## Hilt

**Current choice:** generated dependency injection.

**Why it fits:** many shared framework objects and interface bindings; integrates with Android and ViewModels.

**Alternative:** manual composition root.

**When to reconsider:** a much smaller project or desire to reduce code generation/tooling.

## Current Technical Pressure Points

These are not necessarily bugs, but they deserve attention as the app grows:

- all ViewModels and UI errors share one large source file;
- repository implementations share one large source file;
- cookie persistence bridges synchronous OkHttp and asynchronous DataStore with `runBlocking`;
- upload success is coupled to two subsequent refresh successes;
- Room schema version 1 has no migration path;
- cache rows with both positions null are not automatically pruned;
- startup ignores non-unauthorized restoration errors;
- current user exists only in memory and must be restored from server.

Use these as design prompts, not automatic refactoring instructions. Change architecture only when a concrete requirement makes the trade-off worthwhile.
