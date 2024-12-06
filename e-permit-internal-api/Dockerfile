# Use Eclipse Temurin JDK 21 on Alpine
FROM eclipse-temurin:21-jdk-alpine

# Create a directory for custom certificates
RUN mkdir -p /opt/certs

# Copy the JAR file to the container
ADD target/e-permit-internal-api.jar /app.jar

# Expose the application's port
EXPOSE 8080

# Add an entrypoint script to dynamically add custom certificates
ADD entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Use the entrypoint script to handle custom certificates
ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "-Dsecurerandom.source=file:/dev/urandom", "/app.jar"]
