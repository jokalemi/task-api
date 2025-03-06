# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiar el JAR compilado
COPY --from=builder /app/target/task-api-0.0.1-SNAPSHOT.jar task-api.jar

# Exponer el puerto de la API
EXPOSE 8080

# Definir variables de entorno
ENV JWT_SECRET=""
ENV MONGO_URI=""
ENV ALLOWED_ORIGINS=""

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/task-api.jar"]
