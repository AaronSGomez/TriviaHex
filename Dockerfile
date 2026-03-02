# Usamos una imagen base ligera con Java 21, compatible con ARM (Raspberry Pi)
FROM eclipse-temurin:21-jre

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado al contenedor (nota: usa el jar normal, no el .original)
COPY trivia-app.jar app.jar

# Exponemos el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
