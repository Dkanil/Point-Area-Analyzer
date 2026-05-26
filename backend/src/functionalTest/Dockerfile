FROM mcr.microsoft.com/playwright/java:v1.50.0-noble
WORKDIR /app
COPY . .

CMD ["./gradlew", "functionalTest", "-PskipDockerUp", "--no-daemon"]