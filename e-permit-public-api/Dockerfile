FROM eclipse-temurin:20-jdk-alpine
ADD target/e-permit-public-api.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dsecurerandom.source=file:/dev/urandom", "/app.jar"]