---
paths:
  - frontend/**/*
---
# Frontend SPA Scope

## No Backend (Prototype Phase)

The frontend prototype works **without a backend**. All data is mocked in the frontend.

- Centralize mock data in `frontend/src/app/core/mock-data.ts`
- Mock data should be realistic: real INNOQ location names, plausible room names, realistic time slots
- No HTTP requests to a backend — the backend is connected on a separate training day

## Angular SPA with Routing

- **Angular Router** with `withHashLocation()` for client-side routing
- URLs update when navigating (hash-based: `/#/find-rooms`)
- Routes defined in `frontend/src/app/app.routes.ts` with lazy-loaded page components
- All page components live in `frontend/src/app/pages/`
