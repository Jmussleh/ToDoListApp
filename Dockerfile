FROM ubuntu:latest
LABEL authors="Jena"

ENTRYPOINT ["top", "-b"]

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
# copy the shaded jar (single fat jar)
COPY --from=build /workspace/target/*-shaded.jar /app/app.jar

# Optional: pass DB settings via env (overridable at runtime)
ENV DB_URL="jdbc:mysql://host.docker.internal:3306/todo_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
    DB_USER="todo_user" \
    DB_PASS="todo_pass_123"

# Hibernate will pick up these system properties if you use them in your cfg builder
# If you're using hibernate.cfg.xml only, you can ignore these ENV vars.
ENV JAVA_TOOL_OPTIONS="-Dhibernate.connection.url=${DB_URL} -Dhibernate.connection.username=${DB_USER} -Dhibernate.connection.password=${DB_PASS}"

# Run console app (interactive menu)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]