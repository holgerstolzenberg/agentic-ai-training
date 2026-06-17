# QA-1 · Performance bei der Raumanzeige

**Qualitätsmerkmal:** Performance / Antwortzeit
**Priorität:** 2 von 5

**Szenario (Prosa):**
> Im Normalbetrieb mit bis zu 300 gleichzeitigen Nutzern ruft ein Consultant die
> verfügbaren Konferenzräume eines Standorts für einen ausgewählten Zeitraum in der
> Calvin-Web-App ab. Die Ergebnisliste ist **sichtbar und interaktiv (First
> Contentful Paint) in ≤ 500 ms für 95 % der Anfragen**.

| Baustein | Ausprägung |
|----------|------------|
| **Environment** | Normalbetrieb während der Kernarbeitszeit, bis zu ~300 gleichzeitige Nutzer |
| **Source** | INNOQ-Mitarbeiter (Consultant) |
| **Event** | Ruft die verfügbaren Räume eines Standorts für einen Zeitraum ab |
| **Artifact** | Calvin Web-App (SPA) inkl. Abfrage an den Booking Service |
| **Response** | Liste der verfügbaren Räume wird angezeigt und ist interaktiv (First Contentful Paint) |
| **Measure** | ≤ 500 ms für 95 % der Anfragen |

**Motivation:** Die schnelle Anzeige ist essenziell für die in der
[Produktvision](../../produkt/produktvision.md) versprochene unkomplizierte Buchung –
besonders für Mitarbeiter wie Alex Berger, die nur selten und dann effizient buchen.

---

[← Übersicht Qualitätsanforderungen](./README.md)
