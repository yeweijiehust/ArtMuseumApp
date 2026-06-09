# Persistence, Cache, and Images

## Prerequisites

- [Art Museum Domain](../02-domain/art-museum-domain.md)
- [Asynchronous and Reactive Programming](../01-foundations/async-and-reactive.md)
- [Architecture and Data Flow](../03-architecture/architecture-and-data-flow.md)

## Three Kinds of Local State

The app uses a different tool for each kind of local data:

| Tool | Stores | Why |
| --- | --- | --- |
| Room | Structured artwork metadata and list positions | Queryable, observable relational data |
| DataStore Preferences | Endpoint, language, session-cookie text | Small key-value preferences |
| Coil cache | Decoded/in-memory and downloaded image data | Specialized image loading and caching |

Choosing a tool based on data shape is more important than forcing all persistence into one system.

## Room Concepts

Room is an Android abstraction over SQLite.

- An **entity** maps a Kotlin class to a database table.
- A **DAO** defines database operations.
- A **database** connects entities and DAOs.
- A **query** is SQL that reads or changes records.

`ImageEntity` becomes table `images`. `@PrimaryKey val id` guarantees one row per artwork ID.

## Why List Positions Are Nullable

`publicPosition` and `minePosition` are nullable integers.

- non-null `publicPosition`: include in public list at that order;
- non-null `minePosition`: include in personal list at that order;
- both non-null: artwork appears in both;
- both null: row is no longer reachable from a list and may only be temporary until later cleanup.

This allows refreshing one list without erasing membership in the other.

## Observable Queries

```kotlin
@Query("SELECT * FROM images WHERE publicPosition IS NOT NULL ORDER BY publicPosition")
fun observePublic(): Flow<List<ImageEntity>>
```

Room re-runs the query and emits when relevant table data changes. The repository maps each entity to `MuseumImage`, so upper layers do not depend on Room.

## Upsert and Merge

**Upsert** means insert if absent, update if present.

When refreshing public images, the repository preserves each row’s personal position:

```kotlin
val mine = dao.getForMerge(image.id)?.minePosition
dao.upsert(image.toEntity(publicPosition = index, minePosition = mine))
```

When refreshing personal images, it preserves public position. This is a merge rule: refresh one list while keeping the other list’s membership.

## Transactions

`database.withTransaction` makes “clear positions then write refreshed list” atomic. Observers should not see a partial transition.

Public pagination does not clear positions. It computes the current list size and appends new positions.

## Offline Detail Fallback

`getImage` prefers fresh network data and updates the cache. It falls back to Room only for `AppFailure.Network`.

Why only network failures? A `NotFound` from the server means the cached work may be stale and should not masquerade as current server truth. Network failure means the server could not be consulted, so stale-but-useful data is appropriate.

## DataStore Preferences

DataStore exposes preference data as Flow and updates it transactionally through `edit`.

Keys:

- `endpoint`;
- `language`;
- `cookie`.

Endpoint and language are naturally observable. Cookie access is adapted to OkHttp’s synchronous `CookieJar` interface.

## Endpoint Isolation

When a replacement endpoint passes health verification and differs from the old endpoint:

1. clear cookie;
2. clear in-memory current user;
3. clear all Room tables;
4. persist new endpoint.

This prevents cross-server data leakage and misleading cached content.

## Coil Image Loading

Compose screens use:

```kotlin
AsyncImage(
    model = image.url,
    contentDescription = image.altText ?: image.title
)
```

Coil:

- accepts the URL or picked local URI;
- downloads or reads image bytes;
- decodes them;
- integrates with Compose;
- uses memory/disk caching.

The app stores image metadata in Room, not image files. Coil owns image-byte caching.

## Photo Picker and Content URIs

Android Photo Picker returns a `Uri`, not a normal filesystem path. A **content URI** is a permission-mediated identifier for data owned by another provider.

`ContentResolver` is used to:

- read MIME type;
- query display name and size;
- open an input stream;
- read bytes for upload.

The app validates metadata first when available, then validates actual bytes after reading. This handles providers that omit or misreport size.

## Alternatives

- Storing full images in Room would inflate the database and duplicate Coil’s specialization.
- Storing everything only in memory would lose offline browsing and settings after process death.
- Writing custom SQL directly would offer control but lose Room’s compile-time query checks and Flow integration.
- A normalized database with separate membership tables would scale better for many collections, but two nullable positions are simpler for exactly two lists.

## Related Walkthroughs

- [Gallery Refresh, Pagination, and Offline Detail](../05-walkthroughs/gallery-and-offline.md)
- [Upload, Edit, and Delete](../05-walkthroughs/upload-edit-delete.md)
- [Endpoint Changes and Error Prompts](../05-walkthroughs/endpoint-and-errors.md)
