# Code Map

## Purpose

This is a compact “where is it?” reference. For explanations, follow the linked lessons and [Packages and Responsibilities](../03-architecture/package-map.md).

## Product Behavior to Code

| Behavior | Screen | ViewModel | Repository/data |
| --- | --- | --- | --- |
| App startup/session restore | `ArtMuseumApp.kt` | `AppViewModel` | `AuthRepositoryImpl.restoreSession` |
| Public gallery refresh | `GalleryScreens.kt` | `GalleryViewModel` | `GalleryRepositoryImpl.refreshPublic` |
| Cursor pagination | `GalleryScreens.kt` | `GalleryViewModel.loadMore` | `loadMorePublic` |
| Detail with offline fallback | `DetailScreen` | `DetailViewModel` | `getImage` |
| Login/register | `AuthScreens.kt` | `AuthViewModel` | `AuthRepositoryImpl` |
| Upload | `ManagementScreens.kt` | `UploadViewModel` | `GalleryRepositoryImpl.upload` |
| Personal museum | `MineScreen` | `MineViewModel` | `refreshMine` |
| Edit/delete | `EditScreen` | `EditViewModel` | `update` / `delete` |
| Change endpoint | `SettingsScreen` | `AppViewModel.saveEndpoint` | `EndpointRepositoryImpl` |
| Change language | `SettingsScreen` | `AppViewModel.setLanguage` | `PreferencesRepositoryImpl` |
| Logout | `SettingsScreen` | `AppViewModel.logout` | `AuthRepositoryImpl.logout` |
| Error prompts | `Common.kt` | `toUiError` | `ApiSupport.kt` |

## Types to Files

| Type or concept | File |
| --- | --- |
| Domain models/failures | `domain/model/Models.kt` |
| Repository contracts | `domain/repository/Repositories.kt` |
| Validation | `domain/validation/Validators.kt` |
| DTOs and DTO mapping | `data/remote/Dtos.kt` |
| Retrofit routes | `data/remote/ArtMuseumApi.kt` |
| API/network failure mapping | `data/remote/ApiSupport.kt` |
| Cookie persistence adapter | `data/remote/SessionCookieJar.kt` |
| Current-user memory state | `data/remote/SessionStore.kt` |
| Room entity/mapping | `data/local/ImageEntity.kt` |
| Room SQL operations | `data/local/ImageDao.kt` |
| Room database | `data/local/ArtMuseumDatabase.kt` |
| DataStore preferences | `data/preferences/AppPreferences.kt` |
| Repository implementations | `data/repository/RepositoryImplementations.kt` |
| Framework object providers | `di/AppModule.kt` |
| Interface bindings | `di/RepositoryModule.kt` |
| Top-level app/navigation | `presentation/ArtMuseumApp.kt` |
| All UI state/ViewModels | `presentation/viewmodel/ViewModels.kt` |
| Localization | `presentation/localization/AppStrings.kt` |
| Theme | `presentation/theme/Theme.kt` |

All paths above are relative to:

```text
app/src/main/java/com/yeweijiehust/artmuseum/
```

## API Route to Code

| Route | Retrofit method | Repository caller |
| --- | --- | --- |
| `GET /api/health` | `health` | `verifyAndSave` |
| `GET /api/auth/me` | `me` | `restoreSession` |
| `POST /api/auth/login` | `login` | `login` |
| `POST /api/auth/register` | `register` | `register` |
| `POST /api/auth/logout` | `logout` | `logout` |
| `GET /api/images` | `images` | `refreshPublic`, `loadMorePublic` |
| `GET /api/images/{id}` | `image` | `getImage` |
| `GET /api/images/mine` | `myImages` | `refreshMine` |
| `POST /api/images` | `upload` | `upload` |
| `PATCH /api/images/{id}` | `update` | `update` |
| `DELETE /api/images/{id}` | `delete` | `delete` |

## Test Map

| Concern | Test file |
| --- | --- |
| Live JSON/OpenAPI compatibility | `data/remote/LiveContractDeserializationTest.kt` |
| Failure mapping | `data/remote/ApiSupportTest.kt` |
| Validation boundaries | `domain/validation/ValidatorsTest.kt` |
| ViewModel behavior | `presentation/viewmodel/ViewModelsTest.kt` |
| Localized error prompts | `presentation/ui/ErrorPromptTest.kt` |
| Settings screenshot | `presentation/ui/SettingsScreenshotTest.kt` |
| Protected navigation | `androidTest/.../MainActivityTest.kt` |
| Application ID | `androidTest/.../AppContextTest.kt` |

## Configuration Map

| Concern | File |
| --- | --- |
| App identity, SDK, dependencies | `app/build.gradle.kts` |
| Dependency versions/aliases | `gradle/libs.versions.toml` |
| App permission/entry points | `app/src/main/AndroidManifest.xml` |
| Release network policy | `app/src/main/res/xml/network_security_config.xml` |
| Debug manifest override | `app/src/debug/AndroidManifest.xml` |
| Debug network policy | `app/src/debug/res/xml/network_security_config_debug.xml` |
| CI | `.github/workflows/android-ci.yml` |
| Live contract runner | `scripts/run-live-contract-tests.ps1` |
| Project rules | `constraints.md` |
| Ignored files | `.gitignore`, `app/.gitignore` |

## Suggested Traces

- [App Startup and Navigation](../05-walkthroughs/app-startup-and-navigation.md)
- [Gallery Refresh, Pagination, and Offline Detail](../05-walkthroughs/gallery-and-offline.md)
- [Authentication and Protected Routes](../05-walkthroughs/authentication.md)
- [Upload, Edit, and Delete](../05-walkthroughs/upload-edit-delete.md)
- [Endpoint Changes and Error Prompts](../05-walkthroughs/endpoint-and-errors.md)
