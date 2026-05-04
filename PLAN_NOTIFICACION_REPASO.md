# Plan: Notificación de Preguntas de Repaso en Test REVIEW

**Objetivo**: Cuando el usuario comienza un test de tipo REVIEW (ciclo 5), mostrar en el frontend un mensaje indicando cuántas preguntas falladas se incluyen en ese test.

**Ejemplo de mensaje**:
```
"Modo Repaso Activado ✓
En este test repasarás las 8 preguntas que fallaste en los ciclos anteriores"
```

---

## 1. Estado Actual (Fin de Sprint Anterior)

### Cómo se crean sesiones REVIEW
```
StartGameSessionService.java:
- Si testCycleIndex anterior == 4
  → Crear sesión con testCycleIndex = 5, sessionType = REVIEW
  → Guardar en BD
  → Retornar al frontend
```

### Cómo se seleccionan preguntas en REVIEW
```
GetNextQuestionService.java:
- Si sessionType == REVIEW:
  1. Obtener lista de failed questions: findFailedQuestionIdsByPlayerAndSubject()
  2. Retornar las primeras (por orden de recencia)
  3. Si se agotan las falladas, rellenar con preguntas nuevas
```

### Lo que nos FALTA
- **No contamos** cuántas preguntas falladas hay ANTES de empezar la sesión
- **No enviamos esa información** en la respuesta GET /api/v1/session/new

---

## 2. Flujo Propuesto (Mañana)

### Backend: Contar Failed Questions en StartGameSessionService

**Cambio en `StartGameSessionService.startSession()`**:

```java
@Override
public GameSessionResponse startSession(StartGameSessionRequest request) {
    UUID playerId = getPlayerId();  // from auth
    String subject = request.getSubject();
    
    // ... existing logic to find last session and compute nextCycleIndex ...
    
    // NEW: Contar preguntas de repaso si es REVIEW
    int reviewQuestionCount = 0;
    if (nextCycleIndex == 5) {
        sessionType = SessionType.REVIEW;
        // Contar falladas ANTES de crear la sesión
        List<Long> failedIds = sessionRepository.findFailedQuestionIdsByPlayerAndSubject(
            playerId, 
            subject
        );
        reviewQuestionCount = failedIds.size();  // ← NUEVO DATO
    }
    
    // Crear y guardar sesión
    GameSession newSession = new GameSession(
        UUID.randomUUID(),
        playerId,
        subject,
        totalQuestions,
        nextCycleIndex,
        sessionType
    );
    
    GameSession savedSession = sessionRepository.save(newSession);
    
    // Retornar respuesta con reviewQuestionCount
    return new GameSessionResponse(
        savedSession.getId(),
        savedSession.getPlayerId(),
        savedSession.getSubject(),
        savedSession.getTotalQuestions(),
        savedSession.getTestCycleIndex(),
        savedSession.getSessionType().toString(),
        reviewQuestionCount  // ← NUEVO CAMPO
    );
}
```

### Frontend: DTO con Nuevo Campo

**`GameSessionResponse.java`** (agregar):

```java
public class GameSessionResponse {
    private UUID sessionId;
    private UUID playerId;
    private String subject;
    private int totalQuestions;
    private int testCycleIndex;
    private String sessionType;  // "NORMAL" o "REVIEW"
    private Integer reviewQuestionCount;  // ← NUEVO (nullable, solo para REVIEW)
    
    // getters y setters...
}
```

### Respuesta HTTP (Ejemplo)

**GET `/api/v1/session/new?subject=Desarrollo%20de%20Interfaces`**

```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "playerId": "123e4567-e89b-12d3-a456-426614174000",
  "subject": "Desarrollo de Interfaces",
  "totalQuestions": 20,
  "testCycleIndex": 5,
  "sessionType": "REVIEW",
  "reviewQuestionCount": 8
}
```

---

## 3. Implementación Paso a Paso (Mañana)

### Paso 1: Modificar DTO GameSessionResponse (5 min)
- Agregar campo: `private Integer reviewQuestionCount;`
- Agregar getter/setter

### Paso 2: Modificar StartGameSessionService (10 min)
- Agregar lógica de conteo de failed questions
- Incluir en la respuesta

### Paso 3: Verificar Tests (5 min)
- Ejecutar: `mvn test -Dtest=GameSessionCycleTest`
- Debe pasar sin cambios (DTO es backward compatible)

### Paso 4: Frontend - Mostrar Mensaje (Frontend Dev)
```javascript
// Cuando llega la respuesta:
if (response.sessionType === 'REVIEW' && response.reviewQuestionCount) {
  showModal(`
    ✓ Modo Repaso Activado
    Repasarás ${response.reviewQuestionCount} preguntas que fallaste
  `);
}
```

---

## 4. Consideraciones

### ¿Por qué no usar GET /api/v1/session/{sessionId}?
- Porque el frontend necesita el mensaje ANTES de solicitar la primera pregunta
- Si esperamos a GET la primera pregunta, es demasiado tarde (ya está dentro del test)
- Mejor enviarla en la respuesta de creación

### ¿Qué pasa si hay 0 failed questions?
```json
{
  "sessionType": "REVIEW",
  "reviewQuestionCount": 0  // Test de repaso vacío (rellenará con preguntas nuevas)
}
```
- Frontend puede mostrar: "¡Perfecto! No tienes preguntas falladas, repasarás temas nuevos"

### ¿Qué pasa con NORMAL?
```json
{
  "sessionType": "NORMAL",
  "reviewQuestionCount": null  // Ignorar en frontend
}
```

---

## 5. Cambios en Código (Resumen)

| Archivo | Cambios |
|---------|---------|
| `GameSessionResponse.java` | +1 campo `Integer reviewQuestionCount` |
| `StartGameSessionService.java` | +3 líneas: contar failed questions si REVIEW |
| Frontend (TBD) | +1 modal/mensaje condicional |
| Tests | Sin cambios (backward compatible) |

---

## 6. Endpoints Afectados

### GET /api/v1/session/new
**Antes**:
```json
{
  "sessionId": "...",
  "sessionType": "REVIEW"
}
```

**Después**:
```json
{
  "sessionId": "...",
  "sessionType": "REVIEW",
  "reviewQuestionCount": 8  // ← NUEVO
}
```

Nota: Campo es nullable/omitido si es NORMAL, por lo que clientes antiguos seguirán funcionando.

---

## 7. Base de Datos (Sin Cambios)

- Las preguntas falladas ya están registradas en `gamesession_question.correct = false`
- Query `findFailedQuestionIdsByPlayerAndSubject()` ya existe
- Solo estamos CONTÁNDOLAS, no modificando nada

---

## 8. Checklist para Mañana ✓

- [ ] Modificar `GameSessionResponse.java` - agregar campo
- [ ] Modificar `StartGameSessionService.java` - contar y enviar
- [ ] Ejecutar tests: `mvn test`
- [ ] Crear JAR: `mvn clean package -DskipTests`
- [ ] Validar en API docs (Swagger) que el campo aparezca
- [ ] Comunicar al Frontend Dev: "Campo listo, el field es `reviewQuestionCount`"
- [ ] Frontend implementa modal/mensaje
- [ ] Testing E2E: completar ciclo 4 → ciclo 5 debe mostrar el mensaje

---

## 9. Datos Útiles para Prueba Manual

### Crear 4 sesiones NORMAL con algunas respuestas fallidas
```bash
POST /api/v1/session
POST /api/v1/session/{sessionId}/answer  # fallar algunas
POST /api/v1/session/{sessionId}/finish

# Repetir 4 veces para llegar a ciclo 5
```

### Ver resultado en ciclo 5
```bash
GET /api/v1/session/new?subject=Desarrollo%20de%20Interfaces
# Verificar que "reviewQuestionCount": N está presente
```

---

## 10. Links a Código

- Backend service: [StartGameSessionService.java](src/main/java/levelup42/trivia/application/service/gamesession/StartGameSessionService.java)
- DTO response: [GameSessionResponse.java](src/main/java/levelup42/trivia/infraestructure/adapter/in/rest/dto/GameSessionResponse.java)
- Query: [DataGameSessionQuestionRepository.java](src/main/java/levelup42/trivia/infraestructure/adapter/out/persistence/repository/DataGameSessionQuestionRepository.java) - método `findFailedQuestionIdsByPlayerAndSubject()`

