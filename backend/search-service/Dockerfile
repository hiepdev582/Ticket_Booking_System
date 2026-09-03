# Stage 1: Build
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app

COPY . .

RUN mvn clean package -pl search-service -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/search-service/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
