# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY package.json package-lock.json* ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B || true

# Copy the rest of the project files
COPY src ./src
COPY frontend ./frontend
COPY tsconfig.json vite.config.ts types.d.ts ./

# Build the application
RUN ./mvnw clean package -Pproduction -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/propbets-1.0-SNAPSHOT.jar app.jar

# Expose port (Render will override with $PORT)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]

