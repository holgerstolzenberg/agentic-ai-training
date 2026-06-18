---
Ticket-ID: CLVN-016
Type: Story
Epic: CLVN-015
Status: Done
---
# Raumauswahl bestätigen

## User Story

Als INNOQ-Mitarbeiter möchte ich meine Raumauswahl bestätigen, damit ich den für mich passenden Konferenzraum reservieren kann.

## Beschreibung

Nachdem ein INNOQ-Mitarbeiter einen verfügbaren Konferenzraum für sein Meeting oder seinen Workshop gefunden hat, möchte er diesen Raum verbindlich auswählen. Die Bestätigung der Raumauswahl ist der erste Schritt im Buchungsprozess und führt den Mitarbeiter zur weiteren Eingabe von Buchungsdetails wie Meetingtitel und optionaler Buchungsnotiz.

Diese User Story ist zentral für das Epic "Raum buchen", da sie den Übergang von der Raumsuche zur eigentlichen Buchung darstellt. Der Mitarbeiter erhält durch die Bestätigung eine visuelle Rückmeldung, dass sein gewünschter Raum für den Buchungsvorgang ausgewählt wurde.

## Akzeptanzkriterien

- [ ] Ein verfügbarer Konferenzraum kann durch Klick/Tap ausgewählt werden
- [ ] Der ausgewählte Konferenzraum wird visuell hervorgehoben
- [ ] Die Raumdetails (Name, Standort, Ausstattung, Kapazität) werden bei der Auswahl angezeigt
- [ ] Der gewählte Zeitraum wird bei der Auswahl angezeigt
- [ ] Eine Bestätigungsschaltfläche ermöglicht das Fortfahren zum nächsten Buchungsschritt
- [ ] Die Auswahl kann vor der Bestätigung geändert werden
- [ ] Nach Bestätigung wird der Mitarbeiter zur Eingabe weiterer Buchungsdetails weitergeleitet

## Betroffene Persona

[INNOQ-Mitarbeiter](/docs/produkt/personas/innoq-mitarbeiter.md)

## Zugehöriges Epic

[CLVN-015 - Raum buchen](/docs/produkt/backlog/CLVN-015-EPIC-raum-buchen.md)

## Subtasks

### Prerequisite: Backend – Resource & Booking API

- [x] **SUB-1** Add Spring Data JPA + H2 to `pom.xml`; configure H2 datasource in `application.yml`
- [x] **SUB-2** Create JPA entities: `LocationEntity`, `EquipmentEntity`, `RoomEntity` (with `@ElementCollection equipmentIds`), `EmployeeEntity`, `BookingEntity` (with `@Version` + unique constraint on roomId/date/startTime)
- [x] **SUB-3** Create `JpaRepository` interfaces for all five entities
- [x] **SUB-4** Implement `DataSeeder` (`ApplicationRunner`) replicating `buildRooms()` and `seedBookings()` from frontend mock data
- [x] **SUB-5** Implement shared error types (`ResourceNotFoundException`, `DoubleBookingException`) and `GlobalExceptionHandler` returning `ProblemDetail` (RFC 7807 / `application/problem+json`)
- [x] **SUB-6** Implement `location` feature package: `GET /api/v1/locations`, `GET /api/v1/locations/{id}` (with `roomCount` in response)
- [x] **SUB-7** Implement `equipment` feature package: `GET /api/v1/equipment`, `GET /api/v1/equipment/{id}`
- [x] **SUB-8** Implement `room` feature package: `GET /api/v1/rooms`, `GET /api/v1/rooms?location-id=`, `GET /api/v1/rooms/{id}`, `GET /api/v1/availability?room-id=&date=&start-time=&end-time=`
- [x] **SUB-9** Implement `employee` feature package: `GET /api/v1/employees/current` (hardcoded Alex Berger)
- [x] **SUB-10** Implement `booking` feature package: `GET /api/v1/bookings`, `GET /api/v1/bookings/{id}`, `POST /api/v1/bookings` (→ 201 / 409), `DELETE /api/v1/bookings/{id}` (→ 204); `BookingResponse` includes `roomName` and `locationName`
- [x] **SUB-11** Add `WebConfig` CORS mapping for all origins/methods

### Prerequisite: Frontend – HTTP Wiring

- [x] **SUB-12** Rewrite `CatalogService` to use `HttpClient`; all methods return `Observable<T>` with `shareReplay(1)`; `currentEmployee$` replaces the sync property
- [x] **SUB-13** Rewrite `BookingService` to use `HttpClient`; keep `_bookings` Signal for optimistic updates; `bookRoom()`, `cancel()`, `checkAvailability()`, `bookingsForRoom()` all HTTP-backed
- [x] **SUB-14** Update `find-rooms.ts` / `home.ts` / `locations.ts` / `my-bookings.ts` / `room-detail.ts` / `app.ts` to use `toSignal()` bridges; fix all N+1 patterns (bulk availability fetch for find-rooms; `roomName`/`locationName` from `BookingResponse`)
- [x] **SUB-15** Remove mock-data imports from all services; deprecate `mock-data.ts`

### CLVN-016: Confirm Room Selection UI

- [x] **SUB-16** Add `selectedRoom = signal<ConferenceRoom | null>(null)`, `selectRoom()`, and `confirmSelection()` to `find-rooms.ts`; remove direct `[routerLink]` from room cards
- [x] **SUB-17** Update `find-rooms.html`: room cards use `(click)="selectRoom(r)"` + `[class.room--selected]` + keyboard a11y (`role="button"`, `keydown.enter/space`); add confirmation panel below room grid
- [x] **SUB-18** Add `room--selected` highlight styles and `.selection-panel` CSS block to `find-rooms.scss`

### Documentation

- [x] **SUB-19** Update `ADR-002` status to "Superseded"; update `technical-debt.md` (TS-2, TS-5 resolved)
