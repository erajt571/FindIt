FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
RUN addgroup -S findit && adduser -S findit -G findit
COPY --from=build /workspace/target/findit-api-0.1.0-SNAPSHOT.jar /app/findit-api.jar
USER findit
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/findit-api.jar"]
