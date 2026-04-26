FROM gradle:jdk21-corretto AS build
WORKDIR /app
COPY . .
RUN gradle build

FROM eclipse-temurin:21-jdk
COPY --from=build /app/build/libs/lab4.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]