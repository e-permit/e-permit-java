# Use Eclipse Temurin JDK 21 on Alpine
FROM eclipse-temurin:21-jdk-alpine

# Create a group and user for the application
RUN addgroup -S epermit && adduser -S epermit -G epermit

# Create a directory for custom certificates and set ownership
RUN mkdir -p /opt/certs \
    && chown epermit:epermit /opt/certs

# Copy the JAR file to the container and set ownership
ADD target/e-permit-internal-api.jar /app.jar
RUN chown epermit:epermit /app.jar

# Expose the application's port
EXPOSE 8080

# Add an entrypoint script, make it executable, and set ownership
ADD entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh && chown epermit:epermit /entrypoint.sh

# Switch to the non-root user
USER epermit

# Use the entrypoint script to handle custom certificates
ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "-Dsecurerandom.source=file:/dev/urandom", "/app.jar"]
