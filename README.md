# 🧠 Trivia Quiz Backend - Full Architecture

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Nginx](https://img.shields.io/badge/Nginx-Load_Balancer-009639?logo=nginx)

Proyecto backend robusto para un sistema de **Trivia Quiz**. Diseñado para la preparación de exámenes y competición multijugador, optimizado para despliegue en **Raspberry Pi 5** mediante contenedores Docker con balanceo de carga.

---

## 📋 Tabla de Contenidos

1. [Arquitectura General](#-arquitectura-general)
2. [Modelo de Dominio](#-modelo-de-dominio)
3. [API REST](#-api-rest)
4. [Implementación (Código Java)](#-implementación-código-java)
5. [Despliegue (Docker & Nginx)](#-despliegue-docker--nginx)

---

## 🏗️ Arquitectura General

El sistema se compone de un cluster de aplicaciones Spring Boot stateless, gobernadas por un balanceador de carga Nginx y respaldadas por una base de datos PostgreSQL.

### Componentes
* **Backend:** Spring Boot (Stateless API).
* **Base de Datos:** PostgreSQL.
* **Balanceador:** Nginx (Round Robin).
* **Escalado:** 3 réplicas activas del backend.

### Diagrama de Infraestructura

```mermaid
graph TD
    User((Internet / Dominio)) -->|HTTP:80| Nginx[Nginx Load Balancer]
    
    subgraph Docker Network
        Nginx --> B1[Backend #1]
        Nginx --> B2[Backend #2]
        Nginx --> B3[Backend #3]
        
        B1 --> DB[(PostgreSQL)]
        B2 --> DB
        B3 --> DB
    end
```

## 🌳 Árbol de Clases y Paquetes Principales

```text
levelup42.trivia/
├── TriviaApplication.java
├── domain/
│   ├── model/
│   │   ├── GameSession.java
│   │   ├── Player.java
│   │   ├── Question.java
│   │   └── SessionStatus.java
│   ├── port/
│   │   ├── in/
│   │   │   ├── gamesession/
│   │   │   ├── player/
│   │   │   └── question/
│   │   └── out/
│   │       ├── GameSessionRepositoryPort.java
│   │       ├── PlayerRepositoryPort.java
│   │       └── QuestionRepositoryPort.java
│   └── exception/
│
├── application/
│   └── service/
│       ├── gamesession/
│       ├── player/
│       └── question/
│
└── infraestructure/
    ├── adapter/
    │   ├── in/rest/
    │   │   ├── GameSessionController.java
    │   │   ├── PlayerController.java
    │   │   ├── QuestionController.java
    │   │   └── dto/
    │   │
    │   └── out/persistence/
    │       ├── GameSessionJpaAdapter.java
    │       ├── PlayerJpaAdapter.java
    │       ├── QuestionJpaAdapter.java
    │       ├── entity/
    │       └── repository/
    │
    ├── config/
    │   ├── exception/
    │   ├── CorsConfig.java
    │   ├── DebugExceptionHandler.java
    │   └── OpenApiConfig.java
    │
    └── mapper/
        ├── GameSessionMapper.java
        ├── PlayerMapper.java
        └── QuestionMapper.java
```

---

## 📊 Modelo de Dominio

Estructura de datos central para gestionar preguntas, jugadores y el estado de cada sesión de juego.

```mermaid
classDiagram
    class Question {
        +Long id
        +String statement
        +String options (A-D)
        +String correctOption
        +String explanation
        +String subject
        +String topic
        +String difficulty
        +boolean active
    }
    class Player {
        +UUID id
        +String name
        +String mail
        +Instant createdAt
    }
    class GameSession {
        +UUID id
        +UUID playerId
        +String subjet
        +int totalQuestions
        +int answeredQuestions
        +int correctAnswers
        +int score
        +Instant startedAt
        +Instant finishedAt
        +SessionStatus status
        +getGrade() double
        +isPassed() boolean
    }
    class SessionStatus {
        <<enumeration>>
        IN_PROGRESS
        FINISHED
    }
    
    Player "1" --> "*" GameSession : plays
    GameSession "1" --> "1" SessionStatus : has
```

---

## 🌐 API REST

Path base: `/api/v1`

### 📝 Preguntas (Questions)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/question` | Listar todas las preguntas |
| `POST` | `/question` | Crear nueva pregunta |
| `PUT` | `/question/{id}` | Actualizar pregunta |
| `DELETE` | `/question/{id}` | Eliminar pregunta |

### 👤 Jugadores (Players)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/player` | Registrar jugador |
| `GET` | `/player/{id}` | Obtener perfil de un jugador |
| `PUT` | `/player/{id}` | Actualizar perfil de un jugador |
| `DELETE` | `/player/{id}` | Eliminar un jugador |

### 🎮 Sesiones de Juego (Game Flow)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/session` | Iniciar nueva sesión de juego |
| `GET` | `/session/{sessionId}` | Obtener detalles y estado (incluye nota) |
| `GET` | `/session/{sessionId}/next-question` | Obtener siguiente pregunta (oculta respuesta correcta) |
| `POST` | `/session/{sessionId}/answer` | Enviar una respuesta y evaluar acierto |
| `POST` | `/session/{sessionId}/finish` | Finalizar explícitamente una sesión en curso |
| `GET` | `/session/player/{playerId}` | Historial de sesiones jugadas por un jugador |
| `GET` | `/session/leaderboard` | Clasificación global de sesiones finalizadas |

---

## 💻 Implementación de Arquitectura Hexagonal (Código Java)

### 4.1. Main Application
```java
package levelup42.trivia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TriviaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriviaApplication.class, args);
    }
}
```

### 4.2. Dominio y Casos de Uso (Núcleo)

<details>
<summary><b>Ver código de Dominio</b></summary>

**GameSession.java** (Sin dependencias externas, pura lógica de negocio)
```java
package levelup42.trivia.domain.model;

public class GameSession {
    private final UUID id;
    private final UUID playerId;
    private final String subjet;
    private int totalQuestions, answeredQuestions, correctAnswers, score;
    private SessionStatus status;

    public void registerCorrectAnswer(int points) {
        this.correctAnswers++;
        this.score += points;
        this.answeredQuestions++;
    }

    public void registerIncorrectAnswer() {
        this.answeredQuestions++;
    }

    public double getGrade() {
        if (totalQuestions == 0) return 0.0;
        double questionValue = 10.0 / totalQuestions;
        double penaltyValue = questionValue / 3.0; // Resta 1/3 por fallo
        int incorrectAnswers = answeredQuestions - correctAnswers;
        double rawGrade = (correctAnswers * questionValue) - (incorrectAnswers * penaltyValue);
        return Math.max(0.0, Math.min(10.0, rawGrade));
    }
    
    public boolean isPassed() {
        return getGrade() >= 5.0;
    }
    // ...
}
```

**GameSessionRepositoryPort.java** (Puerto de salida)
```java
package levelup42.trivia.domain.port.out;

import levelup42.trivia.domain.model.GameSession;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepositoryPort {
    GameSession save(GameSession gameSession);
    Optional<GameSession> findById(UUID id);
    // ...
}
```

**SubmitAnswerUseCase.java** (Puerto de entrada)
```java
package levelup42.trivia.domain.port.in.gamesession;

import java.util.UUID;

public interface SubmitAnswerUseCase {
    boolean execute(UUID sessionId, Long questionId, String selectedOption);
}
```
</details>

### 4.3. Servicios (Capa de Aplicación)

<details>
<summary><b>Ver SubmitAnswerService.java</b></summary>

```java
package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.port.in.gamesession.SubmitAnswerUseCase;
import org.springframework.stereotype.Service;

@Service
public class SubmitAnswerService implements SubmitAnswerUseCase {
    private final GameSessionRepositoryPort sessionRepository;
    private final QuestionRepositoryPort questionRepository;
    // ... dependencies via constructor
    
    @Override
    public boolean execute(UUID sessionId, Long questionId, String option) {
        GameSession session = sessionRepository.findById(sessionId).orElseThrow();
        Question question = questionRepository.findById(questionId).orElseThrow();
        
        boolean isCorrect = question.getCorrectOption().equals(option);
        if (isCorrect) session.registerCorrectAnswer(10);
        else session.registerIncorrectAnswer();
        
        sessionRepository.save(session);
        return isCorrect;
    }
}
```
</details>

### 4.4. Infraestructura y Configuración (`config/`)

El paquete `infraestructure/config/` centraliza la configuración del framework y la seguridad de la aplicación:

*   **`CorsConfig.java`**: Define las políticas de **CORS (Cross-Origin Resource Sharing)**, permitiendo especificar orígenes, métodos y cabeceras autorizados. Esta clase se implementó recientemente **para permitir que las Web Apps (ej. interfaz en Flutter Web) puedan comunicarse con el backend** sin ser bloqueadas por las restricciones de seguridad del navegador.
*   **`OpenApiConfig.java`**: Integra y configura **Swagger / OpenAPI 3**, generando y sirviendo de forma automática la documentación interactiva y los esquemas del API REST.
*   **`DebugExceptionHandler.java`** (y el paquete `exception/`): Gestiona las excepciones de manera global. Intercepta los errores lanzados por la aplicación y los convierte en respuestas HTTP estandarizadas para el cliente.

---

## 🐳 Despliegue (Docker & Nginx)

Configuración para orquestar la base de datos, 3 réplicas del backend y el balanceador de carga.

### docker-compose.yml

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: quizdb
      POSTGRES_USER: quizuser
      POSTGRES_PASSWORD: quizpass
    volumes:
      - quiz-data:/var/lib/postgresql/data
    networks:
      - quiz-net

  # Replicas del Backend
  quiz-backend-1:
    build: ./backend
    environment: &backend_env
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/quizdb
      SPRING_DATASOURCE_USERNAME: quizuser
      SPRING_DATASOURCE_PASSWORD: quizpass
    networks:
      - quiz-net

  quiz-backend-2:
    build: ./backend
    environment: *backend_env
    networks:
      - quiz-net

  quiz-backend-3:
    build: ./backend
    environment: *backend_env
    networks:
      - quiz-net

  nginx:
    build: ./nginx
    ports:
      - "80:80"
    networks:
      - quiz-net

networks:
  quiz-net:

volumes:
  quiz-data:
```

### Configuración Nginx (`nginx.conf`)

Define el grupo de servidores (upstream) para el balanceo de carga.

```nginx
events {}

http {
    upstream quiz_backend {
        server quiz-backend-1:8080;
        server quiz-backend-2:8080;
        server quiz-backend-3:8080;
    }

    server {
        listen 80;
        
        location / {
            proxy_pass http://quiz_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
}
```

### Estructura de Archivos

```text
quiz-trivia-backend/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/quiz/...
├── nginx/
│   ├── Dockerfile
│   └── nginx.conf
└── docker-compose.yml
```