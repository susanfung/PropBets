# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and package files for dependency resolution
COPY pom.xml ./
COPY package.json package-lock.json* ./

# Copy source code and frontend
COPY src ./src
COPY frontend ./frontend
COPY tsconfig.json vite.config.ts types.d.ts ./

# Build the application using system Maven (not wrapper to avoid config issues)
RUN mvn clean package -Pproduction -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/propbets-1.0-SNAPSHOT.jar app.jar

# Expose port (Render will override with $PORT)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]

