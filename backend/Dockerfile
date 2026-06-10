FROM gradle:jdk21-corretto AS build
WORKDIR /app
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon
COPY . .
RUN gradle myBuild -x functionalTest --no-daemon

FROM eclipse-temurin:21-jdk
COPY --from=build /app/build/libs/app.jar app.jar
RUN mkdir -p /jmxRemoteConfig
COPY jmxRemoteConfig/jmxremote.password /jmxRemoteConfig/jmxremote.password
COPY jmxRemoteConfig/jmxremote.access /jmxRemoteConfig/jmxremote.access
RUN chmod 400 /jmxRemoteConfig/jmxremote.password /jmxRemoteConfig/jmxremote.access

EXPOSE 8080 9010 9011
ENTRYPOINT ["java", "-jar", "app.jar"]