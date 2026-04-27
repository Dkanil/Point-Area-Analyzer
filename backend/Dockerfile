FROM gradle:jdk21-corretto AS build
WORKDIR /app
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon
COPY . .
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jdk
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]