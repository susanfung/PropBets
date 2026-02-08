# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY frontend ./frontend
COPY package.json package-lock.json* tsconfig.json vite.config.ts types.d.ts ./

# Make mvnw executable
RUN chmod +x mvnw

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

