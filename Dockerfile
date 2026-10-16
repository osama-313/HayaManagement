# syntax=docker/dockerfile:1

# ---- build stage -------------------------------------------------------------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Resolve dependencies first so this layer is cached until the POM changes.
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---- runtime stage -----------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --uid 1001 appuser
COPY --from=build /workspace/target/team-management-api.jar app.jar
USER appuser

ENV PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
