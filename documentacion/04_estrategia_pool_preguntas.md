# Estrategia Dinámica de Ventana 96h por Tamaño de Pool

## Contexto
Diferentes asignaturas tienen distinto número de preguntas:
- **Asignatura A**: 274 preguntas
- **Asignatura B**: 207 preguntas  
- **Asignatura C**: 153 preguntas
- *Más preguntas se agregarán el fin de semana*

Una ventana fija de 96 horas causa problemas en pools pequeños: si 153 preguntas se hacen todas en 96h, el usuario se queda sin preguntas nuevas.

## Solución Implementada

### Algoritmo: Exclusión Dinámica
En `GetNextQuestionService`, al seleccionar pregunta para sesión NORMAL:

```
1. Obtener totalQuestions = countBySubject(subject)
2. Obtener recentAskedCount = countAskedByPlayerAndSubjectSince(playerId, subject, 96h)
3. Calcular exclusionRatio = recentAskedCount / totalQuestions
4. Si exclusionRatio >= 0.80:
      → No aplicar filtro de 96h (permitir cualquier pregunta)
   Si no:
      → Aplicar filtro: excluir todas las preguntas de últimas 96h
```

### Ejemplos de Comportamiento

| Pool Size | 80% Threshold | Preguntas en 96h | Acción |
|-----------|---------------|-----------------|--------|
| 274 | 219 | 150 | Aplicar filtro ✓ |
| 274 | 219 | 220 | **Desactivar filtro** |
| 207 | 165 | 100 | Aplicar filtro ✓ |
| 207 | 165 | 166 | **Desactivar filtro** |
| 153 | 122 | 80 | Aplicar filtro ✓ |
| 153 | 122 | 123 | **Desactivar filtro** |

### Beneficios
- ✅ **Maximiza rotación** cuando hay suficiente pool (< 80% excluidas)
- ✅ **Permite continuar jugando** cuando pool se agota (>= 80% excluidas)
- ✅ **Automático**: Sin necesidad de configuración por subject
- ✅ **Escalable**: Funciona con cualquier tamaño de pool, actual o futuro

### Nuevas Preguntas (Fin de Semana)
Cuando se agreguen nuevas preguntas:
1. Automáticamente quedan disponibles (aumenta `countBySubject`)
2. No sufren retraso de 96h (no están en `findAskedByPlayerAndSubjectSince`)
3. Inmediatamente elegibles para selección

## Implementación Técnica

### Nuevos Métodos
- `DataQuestionRepository.countBySubject(String subject): long`
- `DataGameSessionQuestionRepository.countAskedByPlayerAndSubjectSince(...): long`
- `QuestionRepositoryPort.countBySubject(String subject): long`
- `GameSessionRepositoryPort.countAskedByPlayerAndSubjectSince(...): long`

### Adaptadores Modificados
- `QuestionJpaAdapter`: Delega a `DataQuestionRepository.countBySubject()`
- `GameSessionJpaAdapter`: Delega a `DataGameSessionQuestionRepository.countAskedByPlayerAndSubjectSince()`

### Lógica en GetNextQuestionService
```java
long totalQuestionsInSubject = questionRepository.countBySubject(session.getSubject());
long recentAskedCount = sessionRepository.countAskedByPlayerAndSubjectSince(
    session.getPlayerId(), 
    session.getSubject(), 
    since
);
double exclusionRatio = (double) recentAskedCount / totalQuestionsInSubject;

if (exclusionRatio < 0.80) {
    // Aplicar filtro de 96h
    excludedIds.addAll(recentAsked);
}
// Si >= 0.80: sin filtro (permitir repetición)
```

## Test Coverage
- ✅ `GetNextQuestionService96hRotationTest`: 3 pruebas
  - Validación de exclusión de 96h
  - Rotación en pool de 274 preguntas
  - Elegibilidad después de 96h
- ✅ `GameSessionCycleTest`: 3 pruebas (ciclos por subject independientes)
- ✅ Todos los tests: 24/24 PASSING

## Notas Futuras
- Si el umbral (80%) necesita ajustarse, cambiar en `GetNextQuestionService` línea con `0.80`
- Monitorear con métricas: qué % de sesiones activaron el "desactivar filtro"
- Considerar alertas si un subject supera regularmente el 80% en 96h
