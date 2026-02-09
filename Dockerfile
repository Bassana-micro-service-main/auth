# ============================================
#                 PROD
# ============================================

# Stage build
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .

# Build du JAR
RUN gradle clean bootJar --no-daemon

# Stage final
FROM eclipse-temurin:21-jdk-alpine AS prod
WORKDIR /app

# Copier le jar généré
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
