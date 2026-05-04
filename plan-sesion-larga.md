# Plan para ampliar la duración de la sesión

## Objetivo
Que la sesión de juego dure más que un solo test, mantenga el progreso del usuario y evite repetir preguntas dentro del mismo recorrido.

Paso 1: Preparación del Modelo de Datos

    "Actúa como un experto en Java Spring Boot y JPA. Necesito modificar mi entidad de Sesión de Test para implementar una lógica de ciclos de 5 días.

        Localiza la entidad que gestiona las sesiones y añade un campo entero llamado testCycleIndex que por defecto sea 1.

        Añade un campo enumerado SessionType con valores NORMAL y REVIEW.

        Asegúrate de que la entidad que registra las respuestas de los usuarios tenga una relación clara con la Pregunta y un campo booleano correct.

        Genera la migración necesaria para PostgreSQL (o deja que Hibernate actualice el esquema) asegurando que no se pierdan datos existentes."

Paso 2: Lógica de Selección de Preguntas (El Corazón)

    "Modifica el servicio encargado de generar los tests. Debes implementar la siguiente lógica:

        Al solicitar un nuevo test, comprueba el testCycleIndex del usuario en la última sesión finalizada.

        Si el índice es menor a 5, crea una sesión NORMAL incrementando el índice. Selecciona preguntas aleatorias de la asignatura que el usuario NO haya respondido correctamente en las últimas 96 horas.

        Si el índice es 5, crea una sesión de REVIEW. La lógica de selección debe ser:

            Obtener todas las preguntas falladas por el usuario en la asignatura (priorizando las más recientes).

            Si hay menos preguntas falladas que el tamaño del test, rellena el resto con preguntas nuevas.

            Tras crear esta sesión, reinicia el testCycleIndex a 1.
            IMPORTANTE: No cambies la firma de los métodos que exponen los endpoints hacia el frontend para evitar errores de conexión."

Paso 3: Limpieza y Consistencia (Post-Test)

    "Revisa el método que procesa la finalización de un test.

        Asegúrate de que cada respuesta se guarde correctamente en la base de datos con su estado (acierto/error).

        Si el test era de tipo REVIEW, marca las preguntas que ahora se han acertado de forma que el sistema sepa que han sido 'recuperadas', pero mantén el histórico para estadísticas.

        Verifica que si el usuario completa la batería total de la asignatura, el sistema sea capaz de reiniciar el pool de preguntas disponibles sin lanzar excepciones de puntero nulo o listas vacías."

¿Por qué esto no romperá tu Frontend?

    Contrato de API Intacto: El frontend seguirá pidiendo GET /api/test/new. El JSON que recibe tendrá la misma estructura de preguntas. El cambio es puramente interno en cómo el backend llena esa lista.

    Transparencia: Para el frontend, el "Test de Repaso" del día 5 parecerá un test normal, a menos que quieras enviar un campo extra en el JSON llamado isReview: true para mostrar un aviso tipo "¡Modo Caña Activado!".

    Persistencia en Postgres: Al usar la base de datos para el contador, si el usuario cambia del portátil al móvil (MiniPC a Laptop), la lógica se mantiene porque el estado reside en el servidor.

Consejo de "pro": Antes de aplicar el Paso 2, pide a la IA que cree un Test Unitario (JUnit) que simule 5 creaciones de test seguidas y verifique que la quinta efectivamente contiene preguntas que fueron falladas en las anteriores. ¡Es la mejor forma de dormir tranquilo!