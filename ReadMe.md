# KTOR API Dokumentation:

## Authentifizierungsbereich

### Login (`/login`)

Die Login-Route ermöglicht es Benutzern, sich mit ihren Anmeldedaten einzuloggen. Nach erfolgreicher Authentifizierung erhalten sie einen JWT-Token, der für weitere autorisierte Anfragen verwendet werden kann.

- **HTTP-Route:** POST `/login`
- **Anfrage:** `UserCredentials`
    - **Felder:**
        - `username`: Der Benutzername des Benutzers.
        - `password`: Das Passwort des Benutzers.
    - **Beispielanfrage:**
      ```json
      {
        "username": "demoUser",
        "password": "pass123"
      }
      ```
- **Antwort:** `Token`
    - **Felder:**
        - `token`: Ein JWT-Token, der bei erfolgreicher Authentifizierung zurückgegeben wird.
    - **Beispielantwort:**
      ```json
      {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http POST [Server-Adresse]/login username=demoUser password=pass123
  ```

### Logout (`/logout`)

Die Logout-Route ermöglicht es einem bereits authentifizierten Benutzer, die Sitzung zu beenden. Dafür muss der Benutzer einen gültigen JWT-Token im Authentifizierung-Header senden.

- **HTTP-Route:** POST `/logout`
- **Authentifizierung:** Erforderlich (JWT-Token)
- **Anfrage:** Keine Körperdaten erforderlich, nur der Authentifizierung-Header.
- **Antwort:** Eine Bestätigungsnachricht, die anzeigt, dass der Benutzer erfolgreich ausgeloggt wurde.
    - **Beispielantwort:**
      ```json
      {
        "message": "Logged out successfully."
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http POST [Server-Adresse]/logout "Authorization:Bearer [Token]"
  ```

---

## Kategorienbereich

### Alle Kategorien Abrufen (`/categories`)

Diese Route bietet eine Übersicht aller vorhandenen Kategorien in der Anwendung. Sie liefert eine Liste aller Kategorien ohne die dazugehörigen Rezepte.

- **HTTP-Route:** GET `/categories`
- **Antwort:** Liste von `Category`
    - **Felder:**
        - `categoryId`: Eindeutige ID der Kategorie.
        - `name`: Name der Kategorie.
    - **Beispielantwort:**
      ```json
      [
        {
          "categoryId": 1,
          "name": "Vegetarisch"
        },
        {
          "categoryId": 2,
          "name": "Vegan"
        }
        // Weitere Kategorien...
      ]
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/categories
  ```

### Kategorie und Rezepte Abrufen (`/category/{categoryId}`)

Diese Route ermöglicht es, eine spezifische Kategorie anhand ihrer ID abzurufen. Zusätzlich werden alle Rezepte, die zu dieser Kategorie gehören, in der Antwort geliefert.

- **HTTP-Route:** GET `/category/{categoryId}`
- **Antwort:**
    - **Kategorie:** `Category`
        - **Felder:**
            - `categoryId`: Eindeutige ID der Kategorie.
            - `name`: Name der Kategorie.
    - **Rezepte:** Liste von `Recipe`
        - Jedes `Recipe`-Objekt enthält relevante Rezeptinformationen.
    - **Beispielantwort:**
      ```json
      {
        "category": {
          "categoryId": 1,
          "name": "Vegetarisch"
        },
        "recipes": [
          {
            "recipeId": 3,
            "name": "Gemüseauflauf",
            // Weitere Rezeptdaten...
          }
          // Weitere Rezepte...
        ]
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/category/1
  ```

### Kategorie Erstellen, Aktualisieren und Löschen

Neben dem Abrufen von Kategorien ermöglichen weitere Routen das Erstellen, Aktualisieren und Löschen von Kategorien. Diese Aktionen sind in der Regel rollenbasiert und erfordern entsprechende Berechtigungen (z. B. Admin-Rechte).

- **Erstellen (POST `/category`):** Erstellt eine neue Kategorie.
- **Aktualisieren (PUT `/category/{categoryId}`):** Aktualisiert eine vorhandene Kategorie.
- **Löschen (DELETE `/category/{categoryId}`):** Löscht eine vorhandene Kategorie.

---

## Zutatenbereich

### Alle Zutaten Abrufen (`/ingredients`)

Diese Route liefert eine Liste aller in der Anwendung verfügbaren Zutaten. Jede Zutat wird mit ihrer ID, ihrem Namen und weiteren spezifischen Informationen präsentiert.

- **HTTP-Route:** GET `/ingredients`
- **Antwort:** Liste von `Ingredient`
    - **Felder:**
        - `ingredientId`: Eindeutige ID der Zutat.
        - `name`: Name der Zutat.
        - `count`: Anzahl der Zutat.
        - `unit`: Maßeinheit der Zutat.
    - **Beispielantwort:**
      ```json
      [
        {
          "ingredientId": 1,
          "name": "Tomaten",
          "count": 3,
          "unit": "Stück"
        },
        // Weitere Zutaten...
      ]
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/ingredients
  ```

### Zutat und Zugehörige Rezepte Abrufen (`/ingredient/{ingredientId}`)

Diese Route ermöglicht es, eine spezifische Zutat und alle Rezepte, die diese Zutat enthalten, anhand der Zutaten-ID abzurufen.

- **HTTP-Route:** GET `/ingredient/{ingredientId}`
- **Antwort:**
    - **Zutat:** `Ingredient`
        - **Felder:**
            - `ingredientId`: Eindeutige ID der Zutat.
            - `name`: Name der Zutat.
            - `count`: Anzahl der Zutat.
            - `unit`: Maßeinheit der Zutat.
    - **Rezepte:** Liste von `Recipe`
        - Jedes `Recipe`-Objekt enthält relevante Rezeptinformationen.
    - **Beispielantwort:**
      ```json
      {
        "ingredient": {
          "ingredientId": 1,
          "name": "Tomaten",
          ...
        },
        "recipes": [
          {
            "recipeId": 5,
            "name": "Tomatensauce",
            ...
          }
          // Weitere Rezepte...
        ]
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/ingredient/1
  ```

### Zutat Erstellen, Aktualisieren und Löschen

Für autorisierte Benutzer (typischerweise Administratoren oder Autoren) bietet die Anwendung Routen zum Erstellen, Aktualisieren und Löschen von Zutaten.

- **Erstellen (POST `/ingredient`):** Ermöglicht das Hinzufügen einer neuen Zutat zur Datenbank.
- **Aktualisieren (PUT `/ingredient/{ingredientId}`):** Ermöglicht das Aktualisieren einer bestehenden Zutat.
- **Löschen (DELETE `/ingredient/{ingredientId}`):** Ermöglicht das Entfernen einer Zutat aus der Datenbank.

Diese Aktionen erfordern eine Authentifizierung und sind durch die Nutzerrollen eingeschränkt.

---

## Rezeptbereich

### Alle Rezepte Abrufen (`/recipes`)

Diese Route listet alle Rezepte in der Anwendung auf, inklusive ihrer Details.

- **HTTP-Route:** GET `/recipes`
- **Antwort:** Liste von `Recipe`
    - **Felder:**
        - `recipeId`: Eindeutige ID des Rezepts.
        - `name`: Name des Rezepts.
        - Weitere spezifische Rezeptdaten.
    - **Beispielantwort:**
      ```json
      [
        {
          "recipeId": 101,
          "name": "Spaghetti Bolognese",
          ...
        },
        ...
      ]
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/recipes
  ```

### Rezept Abrufen (`/recipe/{recipeId}`)

Ruft die Details eines spezifischen Rezepts anhand seiner ID ab, einschließlich der zugehörigen Tags und Kategorien.

- **HTTP-Route:** GET `/recipe/{recipeId}`
- **Antwort:** `RecipeOutput`
    - **Felder:**
        - `recipeId`: Eindeutige ID des Rezepts.
        - `name`: Name des Rezepts.
        - `tags`: Liste von `Tag`-Objekten.
        - `categories`: Liste von `Category`-Objekten.
        - Weitere spezifische Rezeptdaten.
    - **Beispielantwort:**
      ```json
      {
        "recipeId": 102,
        "name": "Vegane Pizza",
        "tags": [...],
        "categories": [...],
        ...
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/recipe/102
  ```

### Rezept Erstellen, Aktualisieren und Löschen

Diese Routen ermöglichen es autorisierten Benutzern, Rezepte zu erstellen, zu aktualisieren und zu löschen.

- **Erstellen (POST `/recipe`):**
    - Erstellt ein neues Rezept basierend auf den bereitgestellten Daten.
    - **Anfrage:** `RecipeCreationRequest`
- **Aktualisieren (PUT `/recipe/{recipeId}`):**
    - Aktualisiert ein bestehendes Rezept.
    - **Anfrage:** `RecipeUpdateRequest`
- **Löschen (DELETE `/recipe/{recipeId}`):**
    - Entfernt ein Rezept aus der Datenbank.

Diese Aktionen erfordern eine Authentifizierung und sind durch Nutzerrollen eingeschränkt.

---

## Tag-Bereich

### Alle Tags Abrufen (`/tags`)

Diese Route listet alle Tags in der Anwendung auf. Jeder Tag wird mit seiner ID und seinem Namen dargestellt.

- **HTTP-Route:** GET `/tags`
- **Antwort:** Liste von `Tag`
    - **Felder:**
        - `tagId`: Eindeutige ID des Tags.
        - `name`: Name des Tags.
    - **Beispielantwort:**
      ```json
      [
        {
          "tagId": 5,
          "name": "Italienisch"
        },
        // Weitere Tags...
      ]
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/tags
  ```

### Tag und Zugehörige Rezepte Abrufen (`/tag/{tagId}`)

Ermöglicht das Abrufen eines spezifischen Tags und aller Rezepte, die diesem Tag zugeordnet sind, anhand der Tag-ID.

- **HTTP-Route:** GET `/tag/{tagId}`
- **Antwort:**
    - **Tag:** `Tag`
        - **Felder:**
            - `tagId`: Eindeutige ID des Tags.
            - `name`: Name des Tags.
    - **Rezepte:** Liste von `Recipe`
        - Jedes `Recipe`-Objekt enthält relevante Rezeptinformationen.
    - **Beispielantwort:**
      ```json
      {
        "tag": {
          "tagId": 5,
          "name": "Italienisch"
        },
        "recipes": [
          // Liste der Rezepte, die dem Tag zugeordnet sind
        ]
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/tag/5
  ```

### Tag Erstellen, Aktualisieren und Löschen

Diese Routen ermöglichen autorisierten Benutzern (typischerweise Administratoren oder Autoren) das Erstellen, Aktualisieren und Löschen von Tags.

- **Erstellen (POST `/tag`):** Fügt einen neuen Tag zur Datenbank hinzu.
- **Aktualisieren (PUT `/tag/{tagId}`):** Aktualisiert einen bestehenden Tag.
- **Löschen (DELETE `/tag/{tagId}`):** Entfernt einen Tag aus der Datenbank.

Diese Aktionen erfordern eine Authentifizierung und sind durch die Nutzerrollen eingeschränkt.

---

## Benutzerbereich

### Alle Benutzer Abrufen (`/users`)

Diese Route ermöglicht das Abrufen einer Liste aller Benutzer in der Anwendung.

- **HTTP-Route:** GET `/users`
- **Authentifizierung:** Erforderlich (Admin-Berechtigungen)
- **Antwort:** Liste von `User`
    - **Felder:**
        - `userId`: Eindeutige ID des Benutzers.
        - `username`: Benutzername.
        - `role`: Rolle des Benutzers.
        - `createdAt`: Erstellungsdatum des Benutzerkontos.
        - `updatedAt`: Datum der letzten Aktualisierung des Benutzerkontos.
    - **Beispielantwort:**
      ```json
      [
        {
          "userId": 1,
          "username": "user1",
          "role": "Admin",
          "createdAt": "2023-01-01",
          "updatedAt": "2023-01-10"
        },
        // Weitere Benutzer...
      ]
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/users "Authorization:Bearer [Token]"
  ```

### Spezifischen Benutzer Abrufen (`/user/{userId}`)

Ruft die Details eines bestimmten Benutzers anhand seiner ID ab.

- **HTTP-Route:** GET `/user/{userId}`
- **Authentifizierung:** Erforderlich (Admin-Berechtigungen)
- **Antwort:** `User`
    - **Felder:**
        - `userId`, `username`, `role`, `createdAt`, `updatedAt`
    - **Beispielantwort:**
      ```json
      {
        "userId": 2,
        "username": "user2",
        "role": "User",
        ...
      }
      ```
- **HTTPie-Befehl:**
  ```bash
  http GET [Server-Adresse]/user/2 "Authorization:Bearer [Token]"
  ```

### Benutzer Erstellen, Aktualisieren und Löschen

Diese Routen ermöglichen das Erstellen, Aktualisieren und Löschen von Benutzerkonten. Sie sind in der Regel auf Benutzer mit Admin-Berechtigungen beschränkt.

- **Erstellen (POST `/user`):** Ermöglicht das Erstellen eines neuen Benutzerkontos.
- **Aktualisieren (PUT `/user/{userId}`):** Ermöglicht das Aktualisieren eines bestehenden Benutzerkontos.
- **Löschen (DELETE `/user/{userId}`):** Ermöglicht das Löschen eines Benutzerkontos.

---
Dokumentation mit ChatGPT auf Basis des Quelltextes erstellt.