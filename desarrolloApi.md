# Guía de Desarrollo de la API - Backend Trivia

Este documento detalla la estructura principal del proyecto, construido siguiendo los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)**. Esta guía está diseñada para que cualquier desarrollador o IA entienda cómo replicar, mantener o ampliar este proyecto.

## Árbol de Clases y Paquetes Principales

```text
src/main/java/levelup42/trivia/
├── TriviaApplication.java                  # Punto de entrada principal de Spring Boot
├── domain/                                 # Cero dependencias de frameworks (Núcleo)
│   ├── model/                              # Entidades del Dominio (POJOs puros)
│   │   ├── GameSession.java
│   │   ├── Player.java
│   │   ├── Question.java
│   │   └── SessionStatus.java
│   ├── port/                               # Puertos (Interfaces)
│   │   ├── in/                             # Casos de Uso (Entrada al dominio)
│   │   │   ├── gamesession/
│   │   │   ├── player/
│   │   │   └── question/
│   │   └── out/                            # Repositorios (Salida del dominio)
│   │       ├── GameSessionRepositoryPort.java
│   │       ├── PlayerRepositoryPort.java
│   │       └── QuestionRepositoryPort.java
│   └── exception/                          # Excepciones de negocio
│
├── application/                            # Orquestación de lógica
│   └── service/                            # Implementación de los puertos de entrada (Casos de uso)
│       ├── gamesession/
│       ├── player/
│       └── question/
│
└── infraestructure/                        # Detalles técnicos, frameworks y bases de datos
    ├── adapter/                            # Implementación de los puertos
    │   ├── in/rest/                        # Controladores HTTP (Spring Web)
    │   │   ├── AuthController.java         # Acceso libre (Registro y Login JWT)
    │   │   ├── GameSessionController.java  # Requiere JWT
    │   │   ├── PlayerController.java       # Requiere JWT
    │   │   ├── QuestionController.java     # Protegido por Roles (ADMIN)
    │   │   └── dto/                        # Objetos de transferencia de red (Request/Response)
    │   │
    │   └── out/persistence/                # Acceso a datos (Spring Data JPA)
    │       ├── GameSessionJpaAdapter.java  # Implementa GameSessionRepositoryPort
    │       ├── PlayerJpaAdapter.java       # Implementa PlayerRepositoryPort
    │       ├── QuestionJpaAdapter.java     # Implementa QuestionRepositoryPort
    │       ├── entity/                     # Entidades de Base de Datos (Hibernate/JPA)
    │       └── repository/                 # Interfaces de Spring Data JpaRepository
    │
    ├── config/                             # Configuraciones de Spring (Cors, Swagger, Excepciones)
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java # Manejo global de errores (401, 403, 500)
    │   ├── CorsConfig.java                 # Configuración de orígenes permitidos
    │   └── OpenApiConfig.java              # Configuración de la UI de Swagger con Token JWT
    │
    ├── security/                           # Implementación de Spring Security
    │   ├── jwt/
    │   │   ├── JwtAuthenticationFilter.java# Intercepta y valida tokens en cada petición
    │   │   └── JwtService.java             # Lógica de firmado y generación de tokens
    │   ├── CustomUserDetails.java          # Adaptador de Security a nuestra entidad Player
    │   └── SecurityConfig.java             # Filtros de Stateless HTTP y reglas `@PreAuthorize`
    │
    └── mapper/                             # Conversores entre Dominio <-> Infraestructura
        ├── GameSessionMapper.java
        ├── PlayerMapper.java
        └── QuestionMapper.java
```

## Endpoints de la API

La API expone los siguientes endpoints principales bajo `/api/v1`:

### GameSession (`/api/v1/session`)
- `POST /` - Iniciar una nueva sesión de juego (`GameSessionRequest`). Se requiere `playerId`, `subject`, y `totalQuestions`. Devuelve la sesión inicializada.
- `GET /{sessionId}` - Obtener los detalles y estado actual de una sesión específica. Incluye una nota calculada sobre 10 (`grade`) y si está aprobada (`isPassed`).
- `GET /{sessionId}/next-question` - Obtener la siguiente pregunta aleatoria para la sesión de una categoría específica, garantizando que no se repitan preguntas ya respondidas (`CurrentQuestionResponse` oculta la respuesta correcta por seguridad).
- `POST /{sessionId}/answer` - Enviar una respuesta a una pregunta (`SubmitAnswerRequest`). Evalúa la respuesta, actualiza la puntuación general y responde indicando el acierto.
- `POST /{sessionId}/finish` - Finalizar explícitamente una sesión en curso. Devuelve la sesión con la nota final (`grade`) sobre 10, restando 1/3 de pregunta por cada fallo.
- `GET /player/{playerId}` - Obtener el historial de sesiones jugadas por un jugador, incluyendo su calificación en cada una.
- `GET /leaderboard` - Obtener la clasificación global de las sesiones finalizadas, ordenadas por mayor puntuación.

### Player (`/api/v1/player` & `/api/auth`)
- **Autenticación (`/api/auth`) [PÚBLICO]**:
  - `POST /register` - Inscribir nuevo jugador. Retorna Token JWT.
  - `POST /login` - Iniciar sesión. Retorna Token JWT.
- **Consultas (`/api/v1/players`) [Requiere Token]**:
  - `GET /` - Listar todos los jugadores (público o protegido, según convenga).
  - `GET /{id}` - Obtener perfil de un jugador específico.
  - `PUT /{id}` - Actualizar perfil de un jugador.
  - `DELETE /{id}` - Eliminar un jugador.

### Question (`/api/v1/question`)
- `POST /` - Crear una nueva pregunta para el banco de preguntas.
- `GET /` - Listar todas las preguntas.
- `PUT /{id}` - Actualizar detalles de una pregunta.
- `DELETE /{id}` - Eliminar una pregunta.

## Detalle de Capas y Clases

### 1. Capa de Dominio (`domain`)
Es el corazón de la aplicación. **No debe tener ninguna dependencia de Spring Boot, base de datos o red.** Todo se define con Java puro.
*   **`domain.model`**: Clases como `Player`, `Question`, `GameSession`. Representan el negocio puro y sus reglas intrínsecas (ej. `GameSession` calcula su propia nota de examen sobre 10 y controla sus estadísticas).
*   **`domain.port.in`**: Interfaces que definen qué operaciones puede realizar el usuario (los "Casos de Uso"). Ej: `CreatePlayerUseCase`, `SubmitAnswerUseCase`.
*   **`domain.port.out`**: Interfaces que definen qué necesita el dominio del mundo exterior (como guardar o buscar algo en base de datos). Ej: `GameSessionRepositoryPort` (permite rastrear IDs de preguntas ya hechas). Al no usar JPA aquí, el dominio es agnóstico del motor final.

### 2. Capa de Aplicación (`application`)
*   **`application.service`**: Son las clases que **implementan** los puertos de entrada (`domain.port.in`). Estas clases, como `CreatePlayerService`, toman la interfaz `PlayerRepositoryPort` (inyección de dependencias) y orquestan el proceso comercial. 
*   **Anotación Importante:** Llevan `@Service` para que Spring las registre, siendo el único acoplamiento ligero al framework.

### 3. Capa de Infraestructura (`infraestructure`)
Aquí reside todo lo relacionado con tecnología, bases de datos, APIs y frameworks.
*   **Adaptadores de Entrada (`adapter/in/rest`)**: 
    *   **Controllers:** Exponen los servicios mediante HTTP. Tienen anotaciones como `@RestController`, `@PostMapping`, etc. Llaman a los Casos de Uso (`domain.port.in`).
    *   **DTO (Data Transfer Objects):** Clases como `PlayerRequest`. Sirven como barrera para que la red no envíe la entidad `Player` directamente y para validar las entradas de la petición web.
*   **Adaptadores de Salida (`adapter/out/persistence`)**:
    *   **JpaAdapters:** Clases como `PlayerJpaAdapter`. Implementan los puertos de salida (`domain.port.out.PlayerRepositoryPort`). Cogen los métodos puros del dominio y los traducen a operaciones de repositorio usando `DataPlayerRepository` y mapeando los objetos.
    *   **Entities:** Clases como `PlayerEntity`. Modelos acoplados a JPA (`@Entity`, `@Table`) que definen cómo se mapean los datos a la base de datos SQL.
    *   **Data Repositories:** Interfaces que extienden `JpaRepository` de Spring Data para hacer magia con SQL sin escribir queries manualmente.
*   **Mappers (`mapper`)**:
    *   Clases usando `MapStruct` (o escribiendo métodos genéricos manualmente) para traducir instancias, por ejemplo:
        *   `PlayerEntity` <-> `Player`
        *   `PlayerRequest` -> `Player`
        *   `Player` -> `PlayerResponse`

## Cómo Replicar o Añadir un Nuevo Flujo (Ej: "Categoría")

Si en el futuro deseas añadir una tabla "Categoría" para las preguntas, sigue estos pasos desde adentro hacia afuera:

1.  **Dominio**: Crea el modelo `Category` puro.
2.  **Puertos Out**: Define `CategoryRepositoryPort` con métodos como `save()`, `findById()`.
3.  **Puertos In**: Define los casos de uso, ej. `CreateCategoryUseCase`.
4.  **Servicios**: Crea `CreateCategoryService` implementando el UseCase, marcándolo con `@Service`.
5.  **Entidad BD**: Crea `CategoryEntity` en infraestructura con `@Entity`.
6.  **JpaRepository**: Crea `DataCategoryRepository extends JpaRepository`.
7.  **Adapter BD**: Crea `CategoryJpaAdapter implements CategoryRepositoryPort` con `@Component`, traduciendo el modelo de dominio a `CategoryEntity` y guardándolo en el repositorio.
8.  **REST**: Crea los DTOs `CategoryRequest/Response` y finalmente el `CategoryController` (`@RestController`).
