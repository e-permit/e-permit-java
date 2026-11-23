FROM eclipse-temurin:25-jdk-jammy
RUN addgroup -S epermit && adduser -S epermit -G epermit
USER epermit
ADD target/e-permit-public-api.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dsecurerandom.source=file:/dev/urandom", "/app.jar"]
