# Stage 1: Build with Maven
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run with JDK
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /build/target/studybuddy-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8005

ENTRYPOINT ["java", "-jar", "app.jar"]

