FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src/main ./src/main
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

COPY --from=build --chown=10001:10001 /build/target/spotify-clone-be-*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=UTC"

USER 10001:10001
EXPOSE 10000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
