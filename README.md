# FieldOps — Android Field Operations App

A public safety field operations Android app built to demonstrate
production-grade Android development practices.

## Screenshots

<p float="left">
  <img src="screenshots/incidents.jpeg" width="45%" alt="Incident Reports screen showing form and filed incidents"/>
  <img src="screenshots/assets.jpeg" width="45%" alt="Asset Tracking screen with search"/>
</p>

## Purpose
Built to simulate real-world incident reporting and asset tracking
for first responders — directly inspired by mission-critical
field operations systems.

## Features
- **Offline-first incident reporting** — Room saves locally first,
  WorkManager syncs when signal returns
- **Asset tracking with real-time search** — combine() operator
  merges search query with Room Flow reactively
- **Performance optimized** — LazyColumn stable keys,
  Dispatchers.Default for CPU work, memory leak fixes

## Architecture
Clean Architecture with MVVM:
- **Presentation** — Jetpack Compose, ViewModels, StateFlow
- **Domain** — Pure Kotlin models, sealed classes for UI state
- **Data** — Repository pattern, Room, Retrofit, WorkManager

## Tech Stack
- Kotlin, Jetpack Compose, MVVM
- Room Database — offline-first caching
- Retrofit — REST API integration
- WorkManager — guaranteed background sync
- Hilt — dependency injection
- JUnit + Espresso — 85%+ test coverage
- GitHub Actions CI/CD

## Key Technical Decisions
| Decision | Why |
|----------|-----|
| Room before Retrofit | Never lose data — offline first |
| StateFlow not LiveData | Kotlin native, works with Compose |
| WorkManager not Service | Survives restart, network constraints |
| Stable keys in LazyColumn | Only changed items recompose |
| Separate Entity from Domain | Decouple DB schema from business logic |

## Testing
- JUnit — ViewModel validation, error handling, happy path
- Espresso — full UI flow, form submission, empty states
- FakeRepository — no real network needed
- JaCoCo — 85%+ coverage enforced in CI/CD

## What I Learned
- Offline-first architecture patterns
- Android lifecycle management — memory leak prevention
- Coroutine scoping — viewModelScope vs GlobalScope
- Reactive UI with StateFlow and Compose
- CI/CD with GitHub Actions
