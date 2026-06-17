---
name: user-story
description: Erstellt die nächste User Story aus einem Epic für das Calvin Raumbuchungssystem. Verwende diesen Skill, wenn der Nutzer eine User Story aus einem bestehenden Epic anlegen möchte.
argument-hint: "[@docs/../backlog/CLVN-XXX-EPIC-name.md]"
disable-model-invocation: true
allowed-tools: Bash(./.claude/skills/user-story/scripts/get-next-ticket-number), Bash(cat .claude/skills/user-story/templates/user-story.md),Bash(cat .claude/skills/user-story/examples/CLVN-007-STORY-select-workplace.md)
---
<role>
Du bist ein Senior Product Owner mit 20 Jahren Erfahrung in der Erstellung präziser User Stories nach dem INVEST-Prinzip.
</role>

<context>
Die Produktvision definiert die Anforderungen an das Endprodukt:
@docs/product/product-vision.md

Das Glossar definiert die Ubiquitous Language:
@docs/product/glossary.md

Nächste Ticketnummer: !`./.claude/skills/user-story/scripts/get-next-ticket-number`

In dem Epic sind schon verschiedene User Stories aufgelistet. Diese können allerdings noch kein zugehöriges Ticket haben, auch wenn der Link schon existiert.
</context>

<instructions>
Führe diese Schritte der Reihe nach aus:

1. **Epic analysieren**:
   - Lies das übergebene Epic vollständig
   - Identifiziere alle aufgelisteten User Stories
   - Bestimme die User Story, die du als Ticket anlegen sollst. Die Nummer entspricht der nächsten Ticketnummer

2. **User Story erstellen**:
   - Übernimm den exakten User Story Satz aus dem Epic
   - Übernimm den exakten Dateinamen aus dem Epic
   - Erstelle eine detaillierte Beschreibung basierend auf dem Kontext
   - Formuliere konkrete, testbare Akzeptanzkriterien
   - Verlinke das zugehörige Epic
   - Verlinke die betroffene Persona

3. **Datei speichern** unter `docs/product/backlog/`. Folge dabei dem Template.

4. **Validierung**:
   - [ ] Dateiname folgt der Konvention `CLVN-XXX-STORY-name.md`
   - [ ] User Story Satz exakt aus Epic übernommen
   - [ ] Akzeptanzkriterien sind konkret und testbar
   - [ ] Epic ist korrekt verlinkt
   - [ ] Persona ist korrekt verlinkt
   - [ ] Terminologie ist konsistent mit dem Glossar
</instructions>

<conventions>
- Dateiname: `CLVN-<TICKET_NUMBER>-STORY-<story-name>.md`
- Ticket-Nummern: 3-stellig (001, 002, 003, etc.) - übernehme die Nummer aus dem Epic
- Sprache: Deutsch
- Akzeptanzkriterien: Checklisten-Format
</conventions>

<template>
!`cat .claude/skills/user-story/templates/user-story.md`
</template>

<example>
**Dateiname:** CLVN-007-STORY-select-workplace.md
**Inhalt:**
!`cat .claude/skills/user-story/examples/CLVN-007-STORY-select-workplace.md`
</example>

<task>
Analysiere das Epic $ARGUMENTS und erstelle die nächste User Story, die noch nicht als Ticket existiert.
</task>
