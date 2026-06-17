# QA-5 · Integration eines neuen Standorts

**Qualitätsmerkmal:** Änderbarkeit / Erweiterbarkeit
**Priorität:** 5 von 5

**Szenario (Prosa):**
> Im Rahmen einer geplanten INNOQ-Expansion fügt ein Entwickler einen neuen Standort
> samt zugehöriger Konferenzräume und Ausstattung zu Calvin hinzu, indem er die
> **Ressourcen-Mock-Daten in der SPA** ergänzt. Die Änderung ist **innerhalb eines
> Release-Zyklus (2 Wochen) implementiert, getestet und deployt, ohne Anpassung der
> Kern-Buchungslogik und ohne bestehende Funktionalität zu beeinträchtigen
> (keine Regression).**

| Baustein | Ausprägung |
|----------|------------|
| **Environment** | Geplante Expansion, regulärer Entwicklungszyklus |
| **Source** | Entwickler / Entwicklungsteam |
| **Event** | Ergänzt einen neuen Standort inkl. Räume und Ausstattung in den Ressourcen-Mock-Daten der SPA |
| **Artifact** | Ressourcen-Mock-Daten der SPA (`frontend/src/app/core/mock-data.ts`); der Booking-Service bleibt unverändert (kennt nur IDs) |
| **Response** | Standort ist integriert und buchbar; Booking-Service und bestehende Standorte/Funktionen bleiben unverändert lauffähig |
| **Measure** | Implementiert, getestet und deployt in ≤ 1 Release-Zyklus (2 Wochen); **keine Backend-Änderung nötig** (Ressourcendaten liegen in der SPA); bestehende automatisierte Tests bleiben grün |

**Motivation:** Die Multi-Standort-Architektur muss neue Standorte mit vertretbarem
Aufwand aufnehmen, damit Calvin mit INNOQ wachsen kann.

---

[← Übersicht Qualitätsanforderungen](./README.md)
