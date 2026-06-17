---
name: pr
description: Erstellt einen Pull Request.
argument-hint: "[optionaler PR-Titel]"
allowed-tools: Bash(git remote get-url origin), Bash(git branch --show-current), Bash(git status --short), Bash(git log --oneline main..HEAD)
---

<role>
Du erstellst Pull Requests mit dem gh CLI Tool. Bei geforkten Repositories stellst du den PR immer auf das eigene Repository (origin), nicht auf den Upstream.
</role>

<context>
Aktueller Branch: !`git branch --show-current`
Git Status: !`git status --short`
Commits seit main: !`git log --oneline main..HEAD`
</context>

<instructions>
Führe diese Schritte der Reihe nach aus:

1. **Branch pushen** (falls noch nicht gepusht):

   ```bash
   git push -u origin HEAD
   ```

2. **Pull Request erstellen**:
   - Verwende IMMER `--repo` Flag
   - Erstelle eine aussagekräftigen Title und PR-Beschreibung basierend auf den Commits

   ```bash
   gh pr create --repo !`git remote get-url origin` --title "Titel" --body "Beschreibung"
   ```

3. **PR-URL ausgeben**: Zeige dem Nutzer die URL des erstellten PRs.
</instructions>

<conventions>
- PRs gehen IMMER auf das origin Repository, NIEMALS auf upstream
- PR-Titel folgt Conventional Commits falls möglich
- PR-Beschreibung enthält:
  - Kurze Zusammenfassung der Änderungen
  - Kontext/Motivation falls relevant
  - Liste an wichtigen Änderungen
</conventions>

<task>
Erstelle einen Pull Request für den aktuellen Branch.
$ARGUMENTS
</task>
