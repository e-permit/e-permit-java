FROM openjdk:14-jdk
ADD target/e-permit-internal-api.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dsecurerandom.source=file:/dev/urandom", "/app.jar"]