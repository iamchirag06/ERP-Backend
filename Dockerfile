# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project definition
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the JAR (skipping tests to avoid DB connection errors during build)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 6767

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]