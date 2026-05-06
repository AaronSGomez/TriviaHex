# Lógica de Tests de Repaso

Este documento describe la mecánica implementada en el backend de la aplicación para la generación y gestión de los **Tests de Repaso** (sesiones de tipo `REVIEW`).

## 1. El Ciclo de Tests
El sistema lleva la cuenta de los tests realizados por el usuario en cada asignatura a través de la propiedad `testCycleIndex`.
- Los tests **1, 2, 3 y 4** se generan de manera estándar (priorizando preguntas nuevas o basadas en la ventana de las últimas 96 horas).
- Al iniciar el **5º test**, el sistema automáticamente lo tipifica como `SessionType.REVIEW`. Una vez finalizado, el ciclo se reinicia a 1.

## 2. La "Bolsa" de Fallos (Consulta a Base de Datos)
Para generar un test de repaso, el sistema extrae las preguntas que el usuario debe volver a practicar. La regla principal para incluir una pregunta en la "bolsa de fallos" es:
**"El ÚLTIMO intento del usuario para esta pregunta debe ser un fallo."**

Esto se logra mediante la siguiente consulta JPQL en `DataGameSessionQuestionRepository`:
```java
@Query("SELECT gsq.questionId FROM GameSessionQuestionEntity gsq " +
       "JOIN GameSessionEntity gs ON gs.id = gsq.sessionId " +
       "WHERE gs.playerId = :playerId AND gs.subject = :subject AND gsq.correct = false " +
       "AND gsq.answeredAt = (SELECT MAX(gsq2.answeredAt) FROM GameSessionQuestionEntity gsq2 " +
       "JOIN GameSessionEntity gs2 ON gs2.id = gsq2.sessionId " +
       "WHERE gs2.playerId = :playerId AND gs2.subject = :subject AND gsq2.questionId = gsq.questionId) " +
       "ORDER BY gsq.answeredAt ASC")
```

### Características de la Extracción:
- **Exclusión automática de aciertos en repasos**: Si un usuario falló una pregunta en el pasado, pero ya la ha acertado en un test de repaso, el sistema actualiza el registro con `correct = true`. Como la fecha máxima de respuesta ahora coincide con un acierto, la pregunta **desaparece automáticamente** de la bolsa de fallos.
- **Estrategia FIFO (First In, First Out)**: La consulta utiliza `ORDER BY gsq.answeredAt ASC`. Esto asegura que los fallos más antiguos tengan prioridad. Así evitamos que errores crónicos queden enterrados bajo fallos recientes si se alcanza el límite de preguntas del test.

## 3. Generación del Test
Imaginemos que el jugador acumula 45 fallos históricos, pero el test de repaso tiene un máximo de 30 preguntas:
- El sistema extrae los **30 fallos más antiguos** y compone el test con ellos.
- Las **15 preguntas restantes** se mantienen en la bolsa. Dado que su "último intento" sigue siendo un fallo, volverán a salir en los siguientes repasos hasta que el usuario logre acertarlas.

## 4. Resolución del Repaso
Durante el transcurso del test de repaso:
- **Si el usuario acierta la pregunta**: Se genera un nuevo registro `GameSessionQuestionEntity` con `correct = true` y fecha actual. El fallo histórico queda invalidado por este nuevo acierto, y la pregunta se purga de la lista de pendientes.
- **Si el usuario vuelve a fallar**: Se registra un nuevo `correct = false` con fecha actual. La pregunta se mantiene en la bolsa de fallos, pero ahora su fecha de error es muy reciente, por lo que se moverá al final de la cola (por la estrategia FIFO) dando espacio a que errores más antiguos salgan en el próximo repaso.

Esta estrategia garantiza una limpieza progresiva y eficiente del historial de errores, forzando al estudiante a repasar exactamente lo que no sabe y vaciando el embudo de fallos de forma lógica y estructurada.
