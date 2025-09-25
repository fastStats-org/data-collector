FROM gradle:jdk21-alpine AS build

WORKDIR /home/gradle

COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts /home/gradle/
COPY --chown=gradle:gradle src /home/gradle/src/

RUN gradle shadowJar
FROM openjdk:25-slim

RUN mkdir /app
WORKDIR /app

COPY --from=build /home/gradle/build/libs/*.jar /app/data-collector.jar
ENTRYPOINT ["java", "-Xms256M", "-Xmx512M", "-jar", "/app/data-collector.jar"]