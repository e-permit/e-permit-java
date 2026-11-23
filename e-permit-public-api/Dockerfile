FROM eclipse-temurin:25-jdk-jammy
RUN groupadd --system epermit && useradd --system --gid epermit --no-create-home epermit
COPY --chown=epermit:epermit target/e-permit-public-api.jar /app.jar
USER epermit
EXPOSE 8080
CMD ["java", "-Dsecurerandom.source=file:/dev/urandom", "-jar", "/app.jar"]
