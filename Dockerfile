# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Download dependencies first (better cache)
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Copy source and build shaded JAR
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the main jar built by Maven and rename to app.jar
COPY --from=build /workspace/target/app-1.0-SNAPSHOT.jar /app/app.jar

# DB settings (overridable at runtime)
ENV DB_URL="jdbc:mysql://host.docker.internal:3306/todo_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
    DB_USER="todo_user" \
    DB_PASS="todo_pass_123"

ENV JAVA_TOOL_OPTIONS="-Dhibernate.connection.url=${DB_URL} -Dhibernate.connection.username=${DB_USER} -Dhibernate.connection.password=${DB_PASS}"

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
