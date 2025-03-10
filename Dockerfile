# Build stage - Uses a Maven 3.9.9 image with JDK 21 pre-installed
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Sets the working directory inside the container
WORKDIR /app

# Copies the project files into the container
COPY . .

# Compiles the project and generates the final JAR
RUN mvn clean package -DskipTests

# Runtime stage - Uses a lighter image with just JDK 21
FROM amazoncorretto:21.0.6-alpine3.21

# Sets the working directory where the application will run inside the container
WORKDIR /app

# Copies the JAR generated in the build stage to the final container
COPY --from=builder /app/target/picbank-auth-service-*.jar picbank-auth-service.jar

# Specifies the port exposed by the container
EXPOSE 8080

# Command to run the microservice
ENTRYPOINT ["java", "-jar", "picbank-auth-service.jar"]
