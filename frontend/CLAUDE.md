# Frontend – Calvin SPA

## Documentation

| Document | Path |
|----------|------|
| Architecture (arc42) | `docs/arc42/arc42.md` |
| ADR-001 Frontend + Booking Service | `docs/arc42/adrs/ADR-001-frontend-prototype-and-booking-service.md` |
| ADR-002 Resource Data as Mock Data | `docs/architecture/adrs/ADR-002-resource-data-as-mock-in-spa.md` |
| ADR-003 Basic Auth without Passwords | `docs/architecture/adrs/ADR-003-authentication-basic-auth-without-passwords.md` |
| Quality Requirements | `docs/architecture/quality-requirements/README.md` |
| Technical Debt | `docs/architecture/technical-debt.md` |
| Product Backlog | `docs/product/backlog/backlog.md` |
| Glossary (Ubiquitous Language) | `docs/product/glossary.md` |

Always follow the wording from the glossary.

## Technology

- **Angular 22** – standalone components, no NgModule
- **TypeScript 6** – strict configuration (`tsconfig.app.json`)
- **SCSS** – component-local styling, global design tokens in `src/styles.scss`
- **Angular Router** – client-side routing with lazy-loading per page
- **Angular Signals** – reactive state (no RxJS for UI state, only for HTTP)
- **Build:** `@angular/build:application` (esbuild for production, Vite dev server)
- **Tests:** Vitest + jsdom (no Karma)
- **Formatter:** Prettier

## Folder Structure

```
frontend/
├── angular.json                        # Build configuration (baseHref, proxyConfig …)
├── proxy.conf.json                     # Dev server: /api/v1 → localhost:8081
├── serve-static.mjs                    # Production static server + API proxy for Crucible
├── src/
│   ├── main.ts                         # Bootstrap
│   ├── styles.scss                     # Global CSS Custom Properties (design tokens)
│   └── app/
│       ├── app.ts                      # Root component (shell: header, router outlet, footer)
│       ├── app.html / app.scss         # Shell template and styles
│       ├── app.config.ts               # ApplicationConfig (Router, HttpClient)
│       ├── app.routes.ts               # Route definitions with lazy-loading
│       ├── core/
│       │   ├── models.ts               # Domain interfaces (Ubiquitous Language)
│       │   ├── mock-data.ts            # Master data and seed bookings as mock
│       │   ├── catalog.service.ts      # Master data service (locations, rooms, equipment)
│       │   ├── booking.service.ts      # Booking state (signals, availability, double-booking)
│       │   ├── health.service.ts       # Backend health via Spring Actuator
│       │   └── format.ts              # Date/time utility functions
│       └── pages/
│           ├── home/                   # Home page
│           ├── locations/              # Location overview
│           ├── find-rooms/             # Room search with filters
│           ├── room-detail/            # Room details + booking form
│           ├── my-bookings/            # User's booking overview
│           └── workplaces/             # Placeholder (not yet implemented)
└── dist/calvin/browser/               # Production build (gitignored)
```

## Architecture

### Standalone Components

All components are standalone – no `NgModule`. Dependencies are declared directly in
the `imports` array of the `@Component` decorators.

### State Management with Signals

No external state management. Reactive state via Angular Signals:

```typescript
// Create a signal
readonly backendUp = signal<boolean | null>(null);

// Computed signal
readonly anzahlMeineBuchungen = computed(() => this.meineBuchungen().length);

// Read signal (in template)
{{ backendUp() }}

// Write signal
this.backendUp.set(true);
this._bookings.update(list => [...list, newBooking]);
```

### Services

Services are `providedIn: 'root'` and injected via `inject()`:

```typescript
private readonly health = inject(HealthService);
```

- **`CatalogService`** – provides master data from `mock-data.ts` (read-only)
- **`BookingService`** – manages room bookings in signal state (in-memory, TS-5)
- **`HealthService`** – HTTP call to Spring Actuator for backend connectivity check

### HTTP Calls

The HTTP client is configured with `withFetch()`. **All URLs must be relative** –
no leading slash:

```typescript
// Correct:
this.http.get<HealthStatus>('api/v1/actuator/health')

// Wrong (breaks behind the Crucible proxy):
this.http.get<HealthStatus>('/api/v1/actuator/health')
```

Relative URLs work both behind the Crucible proxy and locally.

### Routing

Hash routing (`withHashLocation()`) – URLs have the format `/#/find-rooms`.
This eliminates the need for server-side route fallbacks and the app works
correctly behind path-prefix proxies (Crucible).

All routes load their page lazily:

```typescript
{ path: 'locations', loadComponent: () => import('./pages/locations/locations').then(m => m.Locations) }
```

### Template Syntax (Angular 17+)

Control flow with `@if` / `@for` / `@else` (not `*ngIf` / `*ngFor`):

```html
@if (backendUp() === true) {
  <span>Backend connected</span>
} @else if (backendUp() === false) {
  <span>Backend not reachable</span>
}

@for (location of locations; track standort.id) {
  <li>{{ standort.name }}</li>
}
```

### Mock Data and Domain Model

Master data (locations, rooms, equipment) live in `core/mock-data.ts` – no
resource service in the prototype (ADR-002, TS-2). The Booking Service works
exclusively with IDs of this mock data. Domain interfaces in `core/models.ts`
follow the Ubiquitous Language from the glossary.

## Important Files

| File | Purpose |
|-------|-------|
| `angular.json` | `baseHref: "./"` (critical for Crucible), `proxyConfig`, builder configuration |
| `proxy.conf.json` | Forwards `/api/v1/*` in the dev server to `localhost:8081` |
| `serve-static.mjs` | Production static server for Crucible: serves `dist/` and proxies `/api/v1/*` |
| `src/styles.scss` | CSS Custom Properties (design tokens): colors, typography, shadows, radii |
| `src/app/core/models.ts` | All domain interfaces – look here before creating new types |
| `src/app/core/mock-data.ts` | Single source of master and seed data |

## Important Bash Commands

```bash
# Dev server (local, port 4200, with API proxy via proxy.conf.json)
npm start

# Production build
npm run build

# Production build + static server (for Crucible / port 4200)
npm run serve:proxy

# Tests
npm test

# Watch build (no server)
npm run watch
```

## Crucible Proxy – Critical Notes

The Crucible training environment serves the app under a path prefix:
`https://crucible.ch.innoq.io/t/<token>/s/<session>/proxy/4200/`

**Therefore:**

1. **`ng serve` does not work behind Crucible.** Vite generates absolute asset paths
   (`/main.js`) that bypass the proxy prefix → 404. Always use
   `npm run serve:proxy` (production build + `serve-static.mjs`) instead.

2. **`baseHref: "./"` in `angular.json` must not be removed.** Only with it
   does esbuild generate relative `import('./chunk-X.js')` paths instead of absolute ones.

3. **Hash routing (`withHashLocation()`) must not be removed.** Without it a
   direct page load would return a 404 from the static server.

4. **HTTP URLs must be relative** (no leading `/`) so they inherit the proxy prefix.

## CSS Conventions

Use CSS Custom Properties from `styles.scss`, no literal values:

```scss
// Correct:
color: var(--clv-petrol);
border-radius: var(--clv-radius);

// Wrong:
color: #004153;
border-radius: 6px;
```

Component-local styles live in the respective `.scss` file. Classes follow
BEM-like naming conventions with a `clv-` prefix for shell elements.

## Code Smells – Don't Do

- **`NgModule`** – all components are standalone
- **`*ngIf` / `*ngFor`** – use `@if` / `@for` instead
- **Constructor injection** (`constructor(private svc: MyService)`) – use `inject()` instead
- **Absolute HTTP URLs** (`'/api/v1/...'`) – always use relative URLs (`'api/v1/...'`)
- **RxJS subjects for UI state** – use Signals instead
- **Define new master data types in components** – they belong in `core/models.ts`
- **Create bookings directly in the backend** – `BookingService` is still in-memory (TS-5); switch only after backend integration
- **Recommend `ng serve` for Crucible** – not functional behind the proxy
