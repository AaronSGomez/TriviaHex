# Guía Paso a Paso: Arquitectura Hexagonal con el Jugador (Player)

¡Tienes toda la razón! Ver el código aparecer de repente puede parecer magia y no ayuda a interiorizar los conceptos. La Arquitectura Hexagonal (o de "Puertos y Adaptadores") cambia nuestra forma habitual de pensar, porque **invertimos las dependencias**.

En una aplicación tradicional (Controlador -> Servicio -> Repositorio), el Servicio depende de la Base de Datos. En la Arquitectura Hexagonal, el Dominio (y el Servicio) son los "reyes", y es la Base de Datos la que debe adaptarse a ellos.

Aquí te explico **exactamente qué hicimos, archivo por archivo, y POR QUÉ lo hicimos**.

---

## 🏗️ 1. El Núcleo: El Dominio (Lo que ya tenías)
**Archivo:** `domain/model/Player.java`

Este es el corazón de tu aplicación. Representa un Jugador con sus reglas de negocio puras.
**¿Por qué está así?** Fíjate que en este archivo **no hay dependencias de Spring Boot ni de JPA** (no hay `@Entity`, no hay `@Column`). Si mañana decides cambiar Spring Boot por otro framework, tu entidad `Player` no cambia en absoluto.

---

## 🚪 2. Las Puertas de Comunicación: Los Puertos (Ports)
Para que nuestro `Player` sea útil, tiene que comunicarse con el mundo exterior (recibir llamadas de la web y guardar datos en la base de datos). Pero como nuestro dominio es "puro" y no quiere depender de nadie, crea **Interfaces** (contratos). Es como decir: *"Yo no sé cómo funciona una base de datos, pero si quieres trabajar conmigo, debes cumplir este contrato"*.

### A. Puerto de Entrada (Inbound Port) - Lo que el dominio ofrece
**Archivo:** `domain/port/in/CreatePlayerUseCase.java`
- **¿Qué hace?** Define qué acciones (Casos de Uso) permite hacer el dominio desde el exterior. En este caso, crear un jugador.
- **¿Por qué?** Porque cuando el controlador web quiera crear un jugador, no llamará directamente al código que lo crea, llamará a esta interfaz. Así, la web no sabe cómo se hace por debajo.

### B. Puerto de Salida (Outbound Port) - Lo que el dominio necesita
**Archivo:** `domain/port/out/PlayerRepositoryPort.java`
- **¿Qué hace?** Le dice al exterior qué necesita de él. Tiene métodos como `save()` y `findById()`.
- **¿Por qué?** El dominio necesita guardar jugadores, pero **se niega a usar PostgreSQL directamente**. Así que pide que alguien de afuera implemente esta interfaz y haga el trabajo sucio de base de datos.

---

## ⚙️ 3. El Orquestador: Aplicación (Service)
**Archivo:** `application/service/CreatePlayerService.java`

- **¿Qué hace?** Es la implementación de nuestro puerto de entrada (`CreatePlayerUseCase`). Contiene la lógica orquestada: recibe los datos, quizás valida si el mail ya existe (lógica de negocio), y luego le pide al Puerto de Salida (`PlayerRepositoryPort`) que guarde el jugador.
- **¿Por qué?** Fíjate que el servicio recibe la interfaz `PlayerRepositoryPort` en su constructor, no recibe un repositorio de JPA. Esto se llama **Inyección de Dependencias**. El servicio no sabe si está guardando en PostgreSQL, en un archivo de texto o en la nube; él solo llama a `port.save()`.

---

## 🔌 4. El Trabajo Sucio (PostgreSQL): Adaptadores de Salida (Outbound Adapters)
Aquí es donde entra Spring Boot y la tecnología real. Tenemos que "adaptar" la Base de Datos para que cumpla el contrato (`PlayerRepositoryPort`) que el Dominio exigió.

### A. La Tabla de la Base de Datos
**Archivo:** `infrastructure/adapter/out/persistence/entity/PlayerEntity.java`
- **¿Qué hace?** Esta sí tiene las anotaciones `@Entity` y `@Table`. Es la representación técnica de la tabla `players`.
- **¿Por qué?** Separamos el modelo de la DB (`PlayerEntity`) del modelo de negocio (`Player`). Así, si añades una columna técnica en la DB (ej. un contador de accesos), no manchas tu `Player` puro.

### B. La Magia de Spring Data
**Archivo:** `infrastructure/adapter/out/persistence/repository/SpringDataPlayerRepository.java`
- **¿Qué hace?** Es la interfaz clásica de Spring que hereda de `JpaRepository` para hacer los `INSERT` y `SELECT`.

### C. El Traductor (Mapper)
**Archivo:** `infrastructure/adapter/out/persistence/mapper/PlayerMapper.java`
- **¿Qué hace?** Convierte el `Player` del Dominio en un `PlayerEntity` para guardarlo, y cuando lee de la DB, convierte el `PlayerEntity` de vuelta a un `Player` puro del Dominio.
- **¿Por qué?** Porque el repositorio de Spring solo entiende de `Entities`, y nuestro Servicio solo habla en términos de `Player` puros. Necesitamos a alguien que traduzca.

### D. El Adaptador Principal (¡La pieza clave!)
**Archivo:** `infrastructure/adapter/out/persistence/PlayerJpaAdapter.java`
- **¿Qué hace?** Esta es la clase que dice *"Yo firmo el contrato"*. **Implementa el `PlayerRepositoryPort`**.
- **¿Cómo funciona?** Cuando el servicio llama a `save()`, esta clase atrapa la llamada, usa el Translator (Mapper) para pasar de `Player` a `PlayerEntity`, lo guarda con el `SpringDataPlayerRepository`, y devuelve el resultado de nuevo como `Player`.

---

## 🌐 5. La Interfaz con el Usuario (REST): Adaptadores de Entrada (Inbound Adapters)
Alguien tiene que iniciar todo este flujo. Necesitamos a alguien que atienda las peticiones de Internet (Postman, Frontend).

### A. El Objeto de Petición (DTO)
**Archivo:** `infrastructure/adapter/in/rest/dto/PlayerRequest.java`
- **¿Qué hace?** Modela exactamente el JSON que el frontend enviará (solo `name` y `mail`).
- **¿Por qué?** Porque la web no necesita enviar el `id` o la fecha de creación `createdAt`, eso lo generamos nosotros.

### B. El Controlador Web
**Archivo:** `infrastructure/adapter/in/rest/PlayerController.java`
- **¿Qué hace?** Escucha en la ruta `@PostMapping("/api/v1/players")`. Cuando llega un JSON, toma los datos, instancia (construye) un `Player` de dominio básico y llama a nuestro Puerto de Entrada (`CreatePlayerUseCase.createPlayer()`).
- **¿Por qué no llama a la base de datos?** Porque la Arquitectura Hexagonal prohíbe que el Controlador hable con la Base de datos. Su única forma de entrar "al centro del hexágono" es a través del Puerto.

---

## 🗺️ Resumen de una Petición (El Viaje del Dato)

1. El Controlador (`PlayerController`) recibe la petición Postman (Adaptador Entrada).
2. El Controlador pasa el dato por el Puerto de Entrada (`CreatePlayerUseCase`).
3. El Servicio (`CreatePlayerService`) hace su cálculo o lógica.
4. El Servicio pide guardar el dato a través del Puerto de Salida (`PlayerRepositoryPort`).
5. El Adaptador JPA (`PlayerJpaAdapter`) escucha esa petición, traduce el dato y lo guarda en PostgreSQL usando Spring Data.

**Todo este lío de archivos tiene un solo propósito fundamental:** Si mañana decides cambiar PostgreSQL por MongoDB, **solo** borras y rehaces los archivos de la carpeta `adapter/out/persistence`. Todo tu Dominio, tu Servicio y tus Controladores seguirán intactos sin que tengas que cambiarles ni una sola línea de código. Eso es la verdadera magia de la arquitectura hexagonal.
