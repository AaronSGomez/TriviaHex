# Guía de Generación Frontend (Flutter) para el Backend Trivia

## Objetivo
Quiero que actúes como un desarrollador experto en Flutter. Debes crear una aplicación móvil completa que consuma un backend REST API existente de un juego de "Trivia". El flujo de la aplicación debe ser moderno, atractivo (estilo dark mode y degradados) y centrado en la experiencia de usuario.

## Información del Backend
El backend está construido con Spring Boot (Java 21) usando Arquitectura Hexagonal.
*   **Base URL:** `https://tuapi.duckdns.org` (o la IP local si se prueba en la misma red, pero esta es la pública).
*   **Documentación API (Swagger):** Toda la especificación de los endpoints en formato OpenAPI se puede encontrar interactuando con `https://tuapi.duckdns.org/swagger-ui/index.html` o descargando el JSON desde `https://tuapi.duckdns.org/v3/api-docs`.

La API maneja principalmente 3 entidades clave:
1.  **Player (Jugador):** Creación, lectura de ranking, eliminación.
2.  **Question (Pregunta):** Creación de nuevas preguntas con múltiples opciones, borrado.
3.  **GameSession (Sesión de Juego):** Inicializar partida especificando parámetros, pedir preguntas de forma secuencial sin repetirse, enviar respuestas, y obtener al finalizar un puntaje de ranking y una nota real de examen (sobre 10).

## Requisitos y Pantallas a Generar

### 1. Pantalla de Bienvenida y Registro (Login/Signup)
*   **UI:** Logo del juego, título atractivo ("LevelUp42 Trivia").
*   **Funcionalidad:**
    *   Formulario simple pidiendo el nombre de usuario (`name`) y un correo electrónico (`mail`).
    *   Al darle a "Comenzar/Registrar", haz un `POST` al endpoint correspondiente de Players.
    *   Si el jugador ya existe en la DB local (ej. Shared Preferences o Hive), salta esta pantalla y llámalo por su nombre, mostrándole directamente un botón para "Jugar de Nuevo". Guarda el ID del jugador devuelto para usarlo en el resto de la app.

### 2. Panel Principal (Dashboard)
Aquí el usuario aterrizará después de registrarse. Muestra una barra de navegación (BottomNavigationBar) o Drawer para cambiar entre 3 funcionalidades principales:

#### A. Iniciar Nuevo Juego (Sección Play)
*   **UI:** Un botón central grande y llamativo ("Empezar Partida").
*   **Funcionalidad:**
    *   Llamada a la API para Iniciar la Sesión de Juego (`POST` `/api/v1/session`) enviando el ID del jugador, la categoría elegida (`subject`) y la cantidad de preguntas (`totalQuestions`).
    *   Navega a la pantalla del flujo del juego y empieza a pedir preguntas.

#### B. Clasificación (Leaderboard)
*   **UI:** Una lista que muestra los mejores jugadores, ordenados por puntuación, o mostrando los perfiles generales.
*   **Funcionalidad:**
    *   Solicita la lista completa de jugadores o puntuaciones de las sesiones finalizadas.
    *   Destaca la posición actual del jugador usando un color diferente en la lista o un banner superior con "Tu puntuación actual: X".

#### C. Aportar Preguntas (Moderador/Admin)
*   **UI:** Un formulario limpio.
*   **Funcionalidad:** Un formulario para expandir la base de datos de preguntas. Debe incluir:
    *   Campo para el Texto de la Pregunta (ej. "¿Cuál es la capital de...?").
    *   Campo para el Tema o Categoría (`subject`).
    *   Campos para 4 respuestas posibles (Option A, Option B, Option C, Option D).
    *   Un selector (Dropdown o RadioButtons) para elegir cuál de las opciones es la correcta (opción 1, 2, 3 o 4).
    *   Al guardar, lanza un `POST` al endpoint de creación de Preguntas. Refleja confirmaciones visuales usando un Snackbar.

### 3. Flujo del Juego (Pantalla de Preguntas)
*   **UI:** Pantalla dinámica e inmersiva. Muestra el número de pregunta (ej. 1/10).
*   **Funcionalidad:**
    *   Pide la siguiente pregunta al backend (`GET` `/api/v1/session/{sessionId}/next-question`) y la muestra.
    *   Muestra las 4 opciones de respuesta como botones grandes.
    *   Al seleccionar una respuesta, haz un `POST` al endpoint para comprobar la respuesta (`/api/v1/session/{sessionId}/answer`).
    *   Muestra feedback inmediato: Rojo y vibración si falló, verde si acertó. Opcionalmente muestra la explicación devuelta.
    *   Transición fluida a la siguiente pregunta repitiendo el ciclo.
    *   Al finalizar las preguntas (o si `next-question` devuelve un 404), llama explícitamente a (`POST` `/api/v1/session/{sessionId}/finish`) y muestra una pantalla de "Resultados" destacando: el puntaje de ranking, la **nota académica (0-10)** (`grade`) y un gran letrero de **Aprobado o Suspenso** (`isPassed`). Incluye un botón para volver al Dashboard.

## Requisitos Técnicos de Flutter

1.  **Gestor de Estados:** Usa Riverpod o Provider (tu elección, pero mantén un solo estándar) para gestionar toda la lógica de estado global (el estado del jugador activo, las llamadas en progreso a la API...).
2.  **Red (Network):** Usa la librería `dio` o `http` para todas las llamadas a red. Construye clases Repository separadas que se comuniquen con la API, de manera que la UI no maneje el parseo JSON directamente.
3.  **UI/UX:** Define un archivo de `theme.dart` central con la paleta de colores. Utiliza bordes redondeados, sombras suaves y transiciones hero entre el menú iniciar y la pantalla de juego.
4.  **Modelo de Datos:** Crea archivos de datos robustos (usando `freezed` o `json_serializable` preferiblemente) equivalentes a los objetos del Backend (Player, Question, GameSession).

Por favor, genera el código fuente estructurado por carpetas (features, core, presentation, domain) basándote en un esquema Clean Architecture o Feature-First.
