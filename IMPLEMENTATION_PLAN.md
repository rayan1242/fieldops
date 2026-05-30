# FieldOps — Implementation Plan

**Package:** `com.fieldops.app` | **Language:** Kotlin | **Min SDK:** API 24 (Android 7.0)

**Scope (4 features only):**
1. Incident Reporting
2. Asset Tracking
3. Performance Optimization (WorkManager offline sync)
4. Testing & CI/CD

---

## Phase 1 — Project Setup

### Step 1: New Android Studio Project
- Project name: `FieldOps`
- Package: `com.fieldops.app`
- Language: Kotlin
- Min SDK: API 24
- Build system: Gradle

### Step 2: Add Dependencies (`build.gradle`)
| Category | Library |
|---|---|
| UI | Jetpack Compose, Material3, Activity-Compose, Material Icons Extended |
| ViewModel | lifecycle-viewmodel-compose, lifecycle-runtime-ktx |
| Navigation | navigation-compose |
| Database | Room (runtime, ktx, kapt compiler) |
| Network | Retrofit2, Gson converter, OkHttp, Logging Interceptor |
| Coroutines | kotlinx-coroutines-android |
| DI | Hilt (android + kapt compiler) |
| Background | WorkManager (work-runtime-ktx) |
| Testing | JUnit, Mockito, coroutines-test, Espresso, Compose UI test, Hilt testing |

### Step 3: AndroidManifest.xml
- Permissions: `INTERNET`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- Application class: `FieldOpsApplication`
- Main activity: `MainActivity`

---

## Phase 2 — Project Structure

```
com.fieldops.app
├── data/
│   ├── local/
│   │   ├── entity/         ← IncidentEntity.kt, AssetEntity.kt
│   │   ├── dao/            ← IncidentDao.kt, AssetDao.kt
│   │   └── FieldOpsDatabase.kt
│   ├── remote/
│   │   ├── api/            ← FieldOpsApiService.kt
│   │   └── dto/            ← IncidentDto.kt, AssetDto.kt
│   └── repository/         ← IncidentRepository.kt, AssetRepository.kt
├── domain/
│   └── model/              ← Incident.kt, Asset.kt, IncidentState.kt, AssetState.kt
├── ui/
│   ├── incident/           ← IncidentViewModel.kt, IncidentScreen.kt
│   ├── asset/              ← AssetViewModel.kt, AssetScreen.kt
│   └── navigation/         ← FieldOpsNavigation.kt
├── worker/                 ← SyncIncidentsWorker.kt
└── FieldOpsApplication.kt
```

---

## Phase 3 — Domain Layer

### Step 4: Domain Models (`domain/model/`)

**Incident.kt**
- Fields: `id` (UUID), `type` (IncidentType), `location`, `severity`, `description`, `reportedBy`, `timestamp`, `isSynced`
- Enums: `IncidentType` {FIRE, CRIME, ACCIDENT, MEDICAL, OTHER}
- Enums: `Severity` {LOW, MEDIUM, HIGH, CRITICAL}

**Asset.kt**
- Fields: `id` (UUID), `name`, `status` (AssetStatus), `assignedTo`, `location`, `lastUpdated`
- Enum: `AssetStatus` {ACTIVE, IN_USE, NEEDS_RESTOCK, FAULTY}

**State classes**
- `IncidentState`: sealed class — `Idle | Loading | Success | Error(message)`
- `AssetState`: sealed class — `Idle | Loading | Success | Error(message)`

---

## Phase 4 — Data Layer

### Step 5: Room Entities (`data/local/entity/`)

**IncidentEntity** — table: `incidents`
- All fields stored as primitives; enums stored as `String`
- `isSynced: Boolean = false` — tracks offline-first state
- Mapper functions: `toDomain()` / `toEntity()`

**AssetEntity** — table: `assets`
- All fields stored as primitives; enums stored as `String`
- Mapper functions: `toDomain()` / `toEntity()`

### Step 6: DAOs (`data/local/dao/`)

**IncidentDao**
| Method | Type | Purpose |
|---|---|---|
| `getAllIncidents()` | `Flow<List<IncidentEntity>>` | Reactive list for UI |
| `getUnsyncedIncidents()` | `suspend` | WorkManager sync job |
| `insertIncident()` | `suspend` | Offline-first insert |
| `markAsSynced(id)` | `suspend` | After backend upload |
| `deleteIncident()` | `suspend` | Delete |
| `getIncidentById(id)` | `suspend` | Single fetch |

**AssetDao**
| Method | Type | Purpose |
|---|---|---|
| `getAllAssets()` | `Flow<List<AssetEntity>>` | Reactive list for UI |
| `getAssetById(id)` | `suspend` | Single fetch |
| `insertAll(list)` | `suspend` | Bulk insert from backend |
| `insertAsset()` | `suspend` | Single insert |
| `updateStatus(id, status, timestamp)` | `suspend` | Status update |
| `deleteAsset()` | `suspend` | Delete |

### Step 7: Room Database (`data/local/FieldOpsDatabase.kt`)
- `@Database` with both entities, version 1
- Singleton via `@Volatile` + `synchronized` block
- `fallbackToDestructiveMigration()` for dev convenience

### Step 8: Retrofit DTOs & API Service (`data/remote/`)

**IncidentDto / AssetDto**
- Mirror domain models with `@SerializedName` for JSON mapping
- `isSynced = true` on DTO→Domain mapper (data from backend is already synced)

**FieldOpsApiService**
```
GET  /api/incidents          → List<IncidentDto>
POST /api/incidents          → IncidentDto
PUT  /api/incidents/{id}     → IncidentDto
DELETE /api/incidents/{id}

GET  /api/assets             → List<AssetDto>
PUT  /api/assets/{id}/status → AssetDto
```

**RetrofitInstance** — lazy singleton, 30-second timeouts, logging interceptor

### Step 9: Repositories (`data/repository/`)

**IncidentRepository** — offline-first pattern:
1. `fileIncident()`: save to Room first → try backend → catch IOException silently (WorkManager will retry)
2. `syncOfflineIncidents()`: called by WorkManager — uploads unsynced incidents, marks them synced
3. `deleteIncident()`: delete from Room → try backend → fail silently
4. Exposes `incidents: Flow<List<Incident>>` mapped from Room

**AssetRepository**:
1. `syncAssets()`: fetch from backend → cache in Room → fail silently on IOException
2. `updateAssetStatus()`: update Room first → try backend → fail silently
3. Exposes `assets: Flow<List<Asset>>` mapped from Room

---

## Phase 5 — ViewModel Layer

### Step 10: ViewModels (`ui/incident/`, `ui/asset/`)

**IncidentViewModel** (`@HiltViewModel`)
- `incidents: StateFlow<List<Incident>>` — from Room via `stateIn(WhileSubscribed(5000))`
- `uiState: StateFlow<IncidentState>` — tracks operation state
- `fileIncident()` — validates location + description before calling repository
- `deleteIncident()`, `resetState()`

**AssetViewModel** (`@HiltViewModel`)
- `assets: StateFlow<List<Asset>>` — from Room
- `filteredAssets: StateFlow<List<Asset>>` — `combine(assets, searchQuery)` reactive filter
- `searchQuery: StateFlow<String>`
- `syncAssets()` — called in `init {}`
- `updateAssetStatus()`, `onSearchQueryChange()`, `resetState()`

---

## Phase 6 — UI Layer (Jetpack Compose)

### Step 11: Screens

**IncidentScreen** (`ui/incident/IncidentScreen.kt`)
- `TopAppBar` with Add/Close toggle button
- Collapsible `IncidentForm` (state hoisted):
  - `FilterChip` row for IncidentType selection
  - `OutlinedTextField` for Location (required, shows error)
  - `FilterChip` row for Severity selection
  - `OutlinedTextField` for Description (required, shows error, minLines=3)
  - `Button` with `CircularProgressIndicator` while loading
- `LazyColumn` of `IncidentCard` items (stable `key = { it.id }`)
- Each card shows: type, severity (color-coded), location, description (2-line ellipsis), pending sync badge
- Empty state: "No incidents reported"
- `LaunchedEffect(uiState)` clears form on Success

**AssetScreen** (`ui/asset/AssetScreen.kt`)
- `TopAppBar` with Refresh/Sync button
- `OutlinedTextField` search bar (filters by name, location, id)
- `LinearProgressIndicator` while loading
- `LazyColumn` of `AssetCard` items
- Each card shows: name, location, color-coded status badge, `DropdownMenu` to change status
- Empty state handles both "no assets" and "no search results" cases

### Step 12: Navigation (`ui/navigation/FieldOpsNavigation.kt`)
- `NavHost` with routes: `"incidents"`, `"incident/{incidentId}"`, `"assets"`
- `FieldOpsBottomNavigation` — 2 tabs: Incidents (Warning icon) + Assets (List icon)
- `MainActivity` (`@AndroidEntryPoint`) hosts `Scaffold` + `NavController`
- Bottom nav uses `launchSingleTop = true` + `popUpTo(startDestination)`

---

## Phase 7 — Background Sync

### Step 13: WorkManager (`worker/SyncIncidentsWorker.kt`)
- Extends `CoroutineWorker` — native suspend function support
- `doWork()`: calls `repository.syncOfflineIncidents()`
- Retry logic: `Result.retry()` if `runAttemptCount < 3`, else `Result.failure()`

**FieldOpsApplication** (`@HiltAndroidApp`)
- Schedules `PeriodicWorkRequest` every 15 minutes on `NetworkType.CONNECTED`
- Uses `ExistingPeriodicWorkPolicy.KEEP` — won't replace if already scheduled

**Offline sync flow:**
```
No signal → fileIncident() saves to Room (isSynced=false)
Signal returns → WorkManager fires every 15 min
→ getUnsyncedIncidents() → POST to backend → markAsSynced()
→ Pending sync badge disappears in UI
```

---

## Phase 8 — Testing

### Step 14: JUnit Unit Tests (12 tests)

**IncidentViewModelTest** (7 tests):
| Test | What it covers |
|---|---|
| Filing without location → error | Validation |
| Filing without description → error | Validation |
| Valid incident → success state | Happy path |
| Valid incident → repository called | Repository interaction |
| Network failure → error state | Error handling |
| `resetState()` clears error | State management |
| Delete → repository called | Delete feature |

**AssetViewModelTest** (5 tests):
| Test | What it covers |
|---|---|
| Search by name filters list | Search feature |
| Search by location filters list | Search feature |
| Empty search shows all assets | Edge case |
| Update status → repository called | Asset tracking |
| Sync failure → error state | Error handling |

**Fake implementations needed:**
- `FakeIncidentRepository` — overrides `incidents` Flow, tracks `filedIncidents`, `deletedIncidents`
- `FakeAssetRepository` — overrides `assets` Flow, tracks `updatedAssets`
- `FakeIncidentDao` — in-memory `MutableStateFlow` list
- `FakeAssetDao` — in-memory `MutableStateFlow` list
- `FakeFieldOpsApiService` — `shouldThrowError` flag for error path testing

**Test setup pattern:**
```kotlin
@Before fun setup() {
    Dispatchers.setMain(StandardTestDispatcher())
    viewModel = IncidentViewModel(fakeRepository)
}
@After fun tearDown() { Dispatchers.resetMain() }
```

### Step 15: Espresso UI Tests (13 tests)

**IncidentScreenTest** (7 tests) — `@HiltAndroidTest` + `createAndroidComposeRule<MainActivity>`:
- Screen shows correctly
- Add button reveals form
- Submit without location → error shown
- Submit without description → error shown
- Valid submit → form closes
- Filed incident appears in list
- Empty state shows when no incidents

**AssetScreenTest** (6 tests):
- Screen shows correctly
- Search bar displayed
- Searching filters the list
- Clearing search shows all assets
- Sync button displayed
- Empty state shows when no assets

---

## Phase 9 — CI/CD

### Step 16: GitHub Actions (`.github/workflows/android_ci.yml`)

**Trigger:** push to `main`/`develop`, PR to `main`

**Pipeline steps:**
1. Checkout code
2. Set up JDK 17 (Temurin)
3. Cache Gradle dependencies
4. Run JUnit tests (`./gradlew test`)
5. Generate JaCoCo coverage report (`./gradlew jacocoTestReport`)
6. Enforce 85% minimum coverage (`./gradlew jacocoTestCoverageVerification`)
7. Run Espresso tests on emulator API 29 (`reactivecircus/android-emulator-runner@v2`)
8. Upload coverage report as artifact

**JaCoCo config in `build.gradle`:**
- Enable unit + Android test coverage in debug build type
- `jacocoTestCoverageVerification` task: minimum = 0.85 (85%)

---

## Build Order Summary

| Phase | Steps | Output |
|---|---|---|
| 1 — Setup | 1–3 | Project compiles, manifest ready |
| 2 — Structure | — | Folders created |
| 3 — Domain | 4 | Models + state classes |
| 4 — Data | 5–9 | Room + Retrofit + Repositories |
| 5 — ViewModels | 10 | Business logic layer |
| 6 — UI | 11–12 | Screens + navigation working |
| 7 — Sync | 13 | Offline-first sync via WorkManager |
| 8 — Tests | 14–15 | JUnit + Espresso coverage |
| 9 — CI/CD | 16 | Automated pipeline + 85% gate |

---

## Key Architecture Decisions

| Decision | Why |
|---|---|
| Room first in `fileIncident()` | Offline-first — never lose data |
| `isSynced` flag on Incident | WorkManager knows what to upload |
| `Flow` from DAOs | Reactive — UI updates automatically |
| `sealed class` for UI state | Compiler-safe state transitions |
| State hoisting in Compose | Composables are reusable and testable |
| `combine()` for filtered assets | Reactive search — no manual re-query |
| Fake DAOs + API in tests | Fast, no I/O, deterministic |
| 85% JaCoCo gate in CI | Enforced — can't merge without coverage |
