# Multi-stage Docker build for Java 21 Blockchain Node
# Stage 1: Build stage with Maven and OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Compile and package executable standalone JAR
RUN mvn clean package -DskipTests

# Stage 2: Lightweight runtime stage with JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy pre-built standalone JAR from builder stage
COPY --from=builder /app/target/blockchain-evoting-node.jar app.jar

# Render or Cloud hosts assign the PORT dynamically via environment variable
ENV PORT=8000
EXPOSE 8000

# Start Web Visual Simulator & Blockchain Node
ENTRYPOINT ["java", "-jar", "app.jar"]
