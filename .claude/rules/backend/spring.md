---
paths:
  - backend/src/**/*
---

The backend uses Spring Boot as main technology.
The following guardrails need to be followed:

- use Spring Boot BOM instead of Spring Maven parent based approach
- use functional or feature based packaging approach instead of tiered/layered packaging
- user yaml style property files
