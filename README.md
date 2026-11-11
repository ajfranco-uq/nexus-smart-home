# NEXUS Smart Home — v3

Current iteration: v3  
Java 21 · Gradle 8.13 · JavaFX 21 · FXML · CSS

## Run

Use JDK 21. From the project root:

- macOS/Linux: `./gradlew clean run`
- Windows: `gradlew.bat clean run`

The Gradle wrapper downloads Gradle 8.13 on first use. An internet connection may be required the first time dependencies are resolved.

## Demo PINs

- Andres (Administrator): `1234` — preferred temperature 21 °C
- Erika (Resident): `2222` — preferred temperature 25 °C
- Duke (Resident): `3333` — preferred temperature 23 °C

These are demonstration credentials only. PINs are hashed with SHA-256 in memory, but this is not a production authentication design. A production system requires a salted password/PIN hashing scheme, secure secret handling, rate limiting and persistent identity management.

## What's new in v3

- A persistent bottom Pattern Activity Console, visible while navigating the application.
- Each trace entry shows the user action, pattern (or NONE), explanation, Java class, method and execution status.
- Navigation actions are also traced and explicitly marked as not directly invoking a design pattern.
- The trace component is backed by a JavaFX-independent `PatternExecutionTracer` and retains the latest 150 entries.
- Demo residents renamed to Andres (Administrator), Erika (Resident) and Duke (Resident).

## What's new in v2

- A shared application context and constructor-based controller dependencies.
- Resident presence with simulated arrival/departure actions.
- House Mode State pattern: `AWAY` ↔ `HOME`.
- Observer pattern through `DomainEventPublisher` and `ActivityLogService`.
- Mediator pattern through `HomeCoordinator`, which coordinates climate recalculation after presence events.
- Strategy pattern for climate targets: average of residents at home, or Eco target when nobody is home.
- Automatic security policy simulation: disarm on first arrival, arm when the last resident leaves.
- Live Residents, Climate, Overview and Activity screens.
- JUnit 5 unit tests for the main presence and climate rules.
- JavaFX CSS font weights normalized to supported `normal`/`bold` values.

## Architecture in this iteration

- `domain`: residents, presence, house mode states, climate strategies and domain events.
- `application`: application service, event activity log and home coordinator.
- `presentation`: JavaFX controllers and FXML views.
- `NexusContext`: composition root that creates shared application services and injects them into controllers.

## Demo flow

1. Sign in with Andres (`1234`). The first resident arrival changes the house to `HOME`, disarms security and selects Andres' preferred temperature (21 °C).
2. Open Residents and simulate Erika arriving. The climate target becomes the average of Andres and Erika: 23 °C.
3. Simulate Andres leaving. Erika remains home, so the house stays in `HOME` and climate recalculates to 25 °C.
4. Simulate Erika leaving. The house changes to `AWAY`, security arms and the Eco strategy selects 20 °C.
5. Follow the Pattern Activity Console at the bottom to inspect actions, patterns, classes and methods in real time.

## Pattern Activity Console

The console is always visible at the bottom of the main application. It records navigation and business actions. For each entry it displays the action, the pattern involved (or `NONE` when no pattern is directly involved), a short explanation, the implementation class, the method and the status. The console is an educational trace, not a bytecode profiler: entries are explicitly emitted by the application at meaningful pattern execution points. `NONE` means the action does not directly use a design pattern.

## Scope

This is an in-memory teaching/demo application, not a real home security product. Vehicle, shopping, device integrations, real sensors, external APIs and the remaining scenes/modules are not connected yet. The next iteration can add security and lock state machines, device abstractions and command-driven scenes.
