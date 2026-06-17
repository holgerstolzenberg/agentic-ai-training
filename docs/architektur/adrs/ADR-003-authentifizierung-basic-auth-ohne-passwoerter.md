# ADR-003: Authentifizierung im Prototyp – Basic-Auth ohne Passwörter statt Okta

**Status:** Akzeptiert
**Datum:** 2026-06-17
**Bezug:** Ergänzt/ändert [ADR-001: Technologie-Stack](./ADR-001-technologie-stack-fuer-booking-service.md) (Abschnitt Okta-Integration)

## Kontext und Problemstellung

[ADR-001](./ADR-001-technologie-stack-fuer-booking-service.md) hat als
Authentifizierung perspektivisch eine **Okta-Integration** (OIDC/OAuth2) vorgesehen.
Für den Prototyp bringt Okta jedoch Nachteile:

- **Abhängigkeit zu einem Drittsystem** (Okta-Tenant, Konfiguration, Netzzugang).
- **Langsames Testen:** Mit echtem OIDC-Login lässt sich nicht schnell zwischen
  verschiedenen Test-Nutzern wechseln – genau das brauchen wir aber, um Szenarien
  wie QA-4 (eigene vs. fremde Buchungen) auszuprobieren.

## Entscheidung

- Wir **verzichten im Prototyp auf die Okta-Integration**. Sie wird **nachgeliefert,
  wenn das System in Produktion geht**.
- Stattdessen setzen wir auf **Basic-Auth ohne Passwörter**: Der per HTTP-Basic
  übermittelte **Benutzername bestimmt die Identität** des Nutzers; das Passwort
  wird **nicht geprüft** (beliebig/leer).
- Der Booking-Service nutzt diese Identität für die Autorisierung (z. B. „eigene vs.
  fremde Buchung" gemäß QA-4).

## Begründung

- **Schnelles Testen mit verschiedenen Nutzern:** Nutzerwechsel durch simples
  Ändern des Basic-Auth-Benutzernamens, ohne Login-Flow.
- **Keine Drittsystem-Abhängigkeit:** Der Prototyp läuft autark, ohne Okta-Tenant.
- **Geringer Aufwand:** In Spring Security mit wenigen Zeilen umsetzbar; der für
  ADR-001 gewählte Stack (Spring Security) bleibt unverändert und ist später ohne
  Architekturbruch auf einen OAuth2 Resource Server (Okta) umstellbar.

## Konsequenzen

**Positiv**

- Sofort lauffähig, reibungsloses Multi-User-Testing.
- Migrationspfad zu Okta bleibt offen (gleicher Security-Stack).

**Negativ (technische Schuld, siehe [Technische Schulden](../technische-schulden.md))**

- **Keine echte Authentifizierung:** Da kein Passwort geprüft wird, kann sich jede
  Person als **beliebiger Nutzer** ausgeben. Die Zugriffskontrolle (QA-4) ist damit
  nur „kooperativ" und **nicht sicher** durchsetzbar.
- Nur für eine **nicht-öffentliche Prototyp-/Testumgebung** vertretbar; ein
  Produktivbetrieb ist ohne die nachzuliefernde Okta-Integration **nicht zulässig**.

## Referenzen

- [ADR-001: Technologie-Stack für den Booking Service](./ADR-001-technologie-stack-fuer-booking-service.md)
- [QA-4: Anonyme Sichtbarkeit fremder Buchungen](../qualitätsanforderungen/QA-4.md)
- [Technische Schulden](../technische-schulden.md)
