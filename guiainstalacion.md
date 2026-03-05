# Guía de Instalación y Despliegue con Docker y PostgreSQL (Raspberry Pi 5)

Esta guía detalla los pasos necesarios para empaquetar, exportar y desplegar el backend del juego Trivia en un servidor externo (específicamente una Raspberry Pi 5 con 8GB de RAM y almacenamiento NVMe), utilizando contenedores Docker y PostgreSQL 16.

> **Nota de versión**: Este despliegue incluye de base las **reglas de juego en el servidor** (evaluación tipo examen base 10 con penalización por fallo de 1/3) y un **algoritmo anti-repetición** para garantizar que nunca salgan preguntas duplicadas en una misma sesión.

## Requisitos Previos (En la Raspberry Pi 5 SERVER)

Antes de comenzar, asegúrate de conectarte por SSH a tu Raspberry Pi 5 y de tener instalados los siguientes componentes deportivos:

1.  **Docker**: Motor de contenedores. Para instalarlo en Raspberry Pi OS / Debian/ Ubuntu:
    ```bash
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    # ¡Cierra sesión y vuelve a entrar o ejecuta `newgrp docker` para aplicar los permisos de tu usuario!
    ```
2.  **Docker Compose**: Herramienta de orquestación (suele venir con Docker CLI ahora, comprobable usando `docker compose version`).

---

## 1. Empaquetar la Aplicación (En tu PC Local)

Dado que la Raspberry Pi tiene arquitectura ARM64, empaquetaremos el código en tu ordenador Windows (PC de desarrollo) generando un archivo `.zip` con el ejecutable JAR y los archivos de configuración de Docker, listos para transferir.

### Paso 1.1: Generar el archivo `.jar` ejecutable
En tu PC Windows, abre una terminal en la raíz de tu proyecto (carpeta `Trivia`) y asegúrate de tener todo limpio y precompilado:
```powershell
mvn clean package -DskipTests
```
*Si fue exitoso, tendrás un archivo llamado `trivia-0.0.1-SNAPSHOT.jar` dentro de la carpeta `/target/`.*

### Paso 1.2: Crear el `Dockerfile`
En la raíz de tu proyecto, asegúrate de tener el archivo `Dockerfile`. Usaremos una imagen base compatible con ARM64 (la Raspberry Pi):
```dockerfile
# Utilizamos una imagen ligera de Java 21 JRE 
# (Eclipse Temurin soporta automáticamente arquitectura ARM64 de la Raspberry Pi)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiamos el ejecutarble compilado (.jar) a la imagen
COPY trivia-app.jar app.jar

# Exponemos el puerto 8080 en el que corre la aplicación internamente
EXPOSE 8080

# Comando para ejecutar tu aplicación de Trivia
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Paso 1.3: Crear el `docker-compose.yml`
En la misma carpeta raíz, crea/modifica el archivo `docker-compose.yml`. Configurado para levantar **PostgreSQL 16**.
*(El almacenamiento NVMe de la Raspberry Pi 5 procesará de sobra I/O, el servicio volará 🎉).*

```yaml
services:
  postgres:
    image: postgres:16
    container_name: trivia_postgres
    restart: always
    environment:
      POSTGRES_DB: trivia
      POSTGRES_USER: trivia_user
      POSTGRES_PASSWORD: supersecret
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    container_name: trivia_backend
    restart: always
    ports:
      - "127.0.0.1:8081:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/trivia
      - SPRING_DATASOURCE_USERNAME=trivia_user
      - SPRING_DATASOURCE_PASSWORD=supersecret
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      # Variables de seguridad JWT (Imprescindibles)
      - APPLICATION_SECURITY_JWT_SECRET_KEY=UnaClaveSecretaLargaEnHexadecimalOBase64ParaJWTDeAlMenos256Bits
      - APPLICATION_SECURITY_JWT_EXPIRATION=86400000 
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Paso 1.4: Crear el paquete para exportar (Archivar)
Ahora, copia todo lo necesario para pasarlo a la Raspberry. Crea una carpeta temporal (ej: `trivia_deploy`), y mete dentro:
1. Tu archivo recopilado `target/trivia-0.0.1-SNAPSHOT.jar` **(IMPORTANTE: Renómbralo a `trivia-app.jar`** para que coincida con el Dockerfile).
2. El `Dockerfile`.
3. El `docker-compose.yml`.

Comprime esa carpeta `trivia_deploy` en un archivo `deploy_trivia.zip` para trasladarla a tu Servidor.

---

## 2. Despliegue en la Raspberry Pi 5

### Paso 2.1: Transferir los archivos
Manda el `.zip` a la Raspberry Pi. Si tu Raspberry tiene una dirección IP `192.168.1.100` y el usuario es `pi`, puedes usar `scp` (Secure Copy) desde Windows o herramientas como FileZilla/WinSCP:

Desde la consola de Windows (Powershell):
```powershell
scp deploy_trivia.zip pi@192.168.1.100:/home/pi/
```

### Paso 2.2: Descomprimir e Inicializar el entorno
Por SSH en tu Raspberry Pi (Servidor), ve a la ruta y extrae los archivos:

```bash
cd /home/pi/
unzip deploy_trivia.zip
cd trivia_deploy
```

### Paso 2.3: Lanzar los contenedores en Docker
Dentro de la carpeta descargada, arranca todo el ecosistema (Aplicación + Base de Datos):

```bash
docker compose up -d --build
```

### Detalles del comando en Producción:
*   `up`: Crear y empezar servicios.
*   `-d`: Detached mode (Liberar la terminal, dejándolo de fondo).
*   `--build`: Construye la imágen en el momento con tu `.jar` empaquetado y un Java adaptado a la arquitectura de la consola ARM64 de la Raspberry.

---

## 3. Comprobaciones de Salud de la DB de la Raspberry Pi

Dado que tu Raspberry Pi cuenta con 8Gb y un NMVE veloz, la inyección de tablas de PostgreSQL ocurrirá en cuestión de segundos.
Para asegurarte, chequea que ambas máquinas estén vivas:

```bash
docker compose ps
```
Revisa los logs del Backend para confirmar que se conectó a PostgreSQL 16 y expuso el puerto HTTP 8080 correctamente:
```bash
docker compose logs -f trivia-app
```

Desde cualquier equipo conectado en tu misma red Wi-Fi de casa/oficina interroga la API de esta manera desde tu navegador:
`http://192.168.1.100:8080/swagger-ui/index.html` (Cambia la IP local 192.168 por la IP de tu Raspberry Pi).

## 4. Subir una nueva Versión

Siempre que re-programes el código en tu Windows y quieras subir una actualización a la Raspberry Pi, simplemente:
1.  En Windows: Ejecuta de nuevo `mvn clean package -DskipTests`
2.  Copia el nuevo `.jar` y renómbralo a `trivia-app.jar`.
3.  Usa `scp` para meterlo en la Raspberry sobre escribiendo al viejo `/home/pi/trivia_deploy/trivia-app.jar`.
4.  En tu Raspberry, reinicia usando build:
    ```bash
    docker compose up -d --build
    ```
