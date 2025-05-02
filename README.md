# e-permit-java

**e-permit-java** is a Java-based implementation of the e-Permit system for electronic permit exchange between authorities. It provides two main RESTful services (Public API and Internal API) and uses a PostgreSQL database for storing data. This documentation serves as an implementation guide for developers and operators to set up, configure, and understand the e-permit system.

> **💡 Tip:** If yo are new to e-permit concept read the e-permit specifications and broader context, refer to the [e-permit documentation site](https://e-permit.github.io).

## Architecture

The e-permit system consists of two services and a database, as shown in the architecture diagram below:

- **Public API:** A service exposed to other authorities for secure data exchange.
- **Internal API:** A service used by the local authority's internal systems to manage permits, quotas, and usage.
- **Database (PostgreSQL):** Distributed database storing all persistent data (permits, quotas, keys, etc.). The authorities share the same database schema but store own data separately.

![Architecture Diagram of the e-permit System](https://e-permit.github.io/img/e-permit-architecture.png)

Each service is provided as a Docker image for easy deployment. Typically, the Internal API runs on an intranet or private network, while the Public API is accessible on the internet over HTTPS. Both services connect to the PostgreSQL database (which can be hosted separately).

## Quickstart

Follow these steps to set up and run the e-permit system quickly using Docker:

1. **Create an `epermit.env` Environment File**  
   Create a file named **`epermit.env`** in your working directory and add the following configuration properties (with values specific to your environment):

   ```properties
   # Spring profile
   SPRING_PROFILES_ACTIVE=dev

   # Database connection settings
   SPRING_DATASOURCE_URL=<jdbc_url_to_your_db>
   SPRING_DATASOURCE_USERNAME=<db_username>
   SPRING_DATASOURCE_PASSWORD=<db_password>
   SPRING_DATASOURCE_DRIVER=<db_driver_class>
   SPRING_DATASOURCE_DIALECT=<hibernate_dialect>

   # Issuing authority identification
   EPERMIT_ISSUER_CODE=<your_country_code>
   EPERMIT_ISSUER_NAME=<your_country_name>

   # (Optional) Graylog logging integration
   EPERMIT_GRAYLOG_HOST=<graylog_host>
   EPERMIT_GRAYLOG_PORT=<graylog_port>
   ```

   In this file:
   - `EPERMIT_ISSUER_CODE` and `EPERMIT_ISSUER_NAME` identify your authority (country code and name) in the e-permit system.
   - If you have a Graylog server for centralized logging, you can specify its host and port; otherwise, these can be omitted.

   > **💡 Tip:** If you need to trust custom Certificate Authority certificates (for example, to call other countries' APIs), create a folder named **`certs`** in your working directory and place the requisite `.crt` files there. These will be added to the Java truststore of the containers (see the volume mapping in the compose file below).

2. **Create a `docker-compose.yml` File**  
   Create a **`docker-compose.yml`** file in the same directory with the following content. This will set up the Internal API, Public API, and (for development/testing) a PostgreSQL database service:

   ```yaml
   version: '3.8'
   services:
     internal-api:
       container_name: internal-api
       image: ghcr.io/e-permit/internalapi:latest
       environment:
         # Administrative password for Internal API requests (set your own secret)
         - EPERMIT_ADMIN_PASSWORD=<admin_password>
         # Password to encrypt the signing key in the database (back this up securely)
         - EPERMIT_KEYSTORE_PASSWORD=<keystore_password>
         - FLYWAY_ENABLED=true
         - FLYWAY_SCHEMAS=public
       env_file:
         - epermit.env
       ports:
         - "8081:8080"   # Internal API will be available on localhost:8081
       # Optional: include custom certificates if needed for truststore
       volumes:
         - ./certs:/opt/certs

     public-api:
       container_name: public-api
       image: ghcr.io/e-permit/publicapi:latest
       env_file: 
         - epermit.env
       ports:
         - "8080:8080"   # Public API will be available on localhost:8080

     # Database service (for local development; use a managed DB in production)
     db:
       container_name: epermit-db
       image: postgres:16.2-alpine
       environment:
         - POSTGRES_USER=compose-postgres
         - POSTGRES_PASSWORD=compose-postgres
         - POSTGRES_DB=devdb
       ports:
         - "5432:5432"  
   ```

   In this compose file:
   - The **Internal API** service uses environment variables for an admin password (`EPERMIT_ADMIN_PASSWORD`) and a keystore password (`EPERMIT_KEYSTORE_PASSWORD`). **Make sure to set strong, secret values for these in production.** The keystore password is used to encrypt your authority's private signing key in the database – keep this password safe and backed up.
   - The **Public API** service shares the common configuration from `epermit.env` and exposes port 8080.
   - The **PostgreSQL** service is included for convenience in development. On production, you should use a standalone, stable PostgreSQL instance rather than running it via Docker Compose.

3. **Start the Services**  
   Run the Docker Compose setup to start all services:

   ```bash
   docker-compose up -d
   ```

   This will launch the Internal API, Public API, and the database container. The Internal API will be listening on port **8081** (mapped to container's 8080), and the Public API on port **8080**.

Once the services are up and running, you can use the following endpoints with the Internal API:

#### Handshake

The authority should first handshake with the other authorities. So that they can send message to each other. Let's say you have an authority named `A` and another authority named `B`. `A` should call the internal API with the public API URI of `B`:

`POST /authorities`

```json
{
    "code": "<authority_code>", // it should be B
    "name": "<authority_name>", // it should be B
    "public_api_uri": "https://<public_api_uri>" // it should be the public API URI of B
}
```

After handshake is from A to B and B to A. Then you can use the following endpoints with the Internal API:

#### Create Quota

To create a quota, use the following endpoint:

`POST /authorities/<authority_code>/quotas`

```json
{
    "permit_type": 1,
    "permit_year": 2025,
    "quantity": 100
}
```

#### Create Permit

To create a permit, use the following endpoint:

`POST /permits`

```json
{
    "issued_for": "<authority_code>",
    "permit_year": 2025,
    "permit_type": 1,
    "company_name": "TEST",
    "company_id": "123",
    "plate_number": "TEST",
    "departure_country": "<country_code>",
    "arrival_country": "<country_code>"
}
```

For the full example [e-permit demo](https://github.com/e-permit/e-permit-java/tree/main/examples/demo).

## Public API

The **Public API** is the service that allows external authorities (other countries) to communicate with your e-permit system. In other words, this API is exposed publicly for inter-country data exchange. Each country must deploy a Public API service that conforms to the e-permit specifications and is accessible over the internet via HTTPS.

Key points about the Public API:

- **Purpose:** It enables secure exchange of permit data between countries. All communication is encrypted (TLS) and messages are digitally signed.
- **Public Key Exchange:** Every authority needs to generate its own private/public key pair (for digital signatures). The Public API is responsible for sharing the authority’s **public key** with other countries. (The implementation will automatically generate an initial key pair during the first startup and store the keys in the database. The private key is stored encrypted using the provided keystore password.)
- **Usage:** Other countries' systems will call your Public API to submit requests (e.g. permit applications, quota requests) and to verify permits. Likewise, your system will call their Public APIs for the same purposes.

The main functions provided by the Public API include:

- **Distributing Public Keys:** Offering your authority’s public key to other countries. Outgoing messages from your country are signed (sealed) with your private key, and the receiving country uses the public key (retrieved via this API) to authenticate the message’s signature.
- **Permit Verification:** Verifying an electronic permit via a QR code. The Public API exposes an endpoint (e.g. `GET /verify/{qrCode}`) that allows a permit’s QR code to be validated by the issuing country’s system. A foreign authority can scan the permit’s QR code and call this endpoint to confirm the permit’s authenticity and details.
- **Handling Foreign Requests:** Accepting incoming permit or quota requests from other countries. For example, when another country’s Internal API submits a request for a permit or quota allocation, it will be received and processed by your Public API.

> **Note:** Ensure the Public API endpoint is secured with TLS (HTTPS) and accessible on the internet, as it will be contacted by external parties. Keep your private signing key secure and only share the public certificate via this API.

## Internal API

The **Internal API** is the service used within an authority’s own systems for managing e-permit data such as quotas, permits, and usage records. It is intended for **internal use only** (for example, by a back-office application or admin interface) and not for direct use by other countries.

Key points about the Internal API:

- **Purpose:** It provides endpoints for your authority to create and manage quotas, issue permits to domestic transport companies, record usage of permits, and initiate requests to other countries (via their Public APIs).
- **Access Control:** The Internal API is secured and assumes only a single administrator (or internal system) will directly consume it. An admin password (configured via the `EPERMIT_ADMIN_PASSWORD` environment variable) is required to authenticate requests. This implementation does **not** provide a full user management system; if multiple user accounts or a GUI are needed, you should implement an additional layer (e.g., an internal application or gateway) that in turn uses this API.
- **Integration:** The Internal API will call other authorities' Public APIs when necessary (for example, to send a permit creation event to another authority). It uses the private key (stored in the database) to sign outgoing messages, and it manages incoming data from foreign Public APIs.

For convenience during development or testing, the Internal API includes a Swagger UI and OpenAPI documentation:

- **Swagger UI:** You can explore and test the Internal API via a web interface at [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) (if running locally with default ports).
- **OpenAPI Docs:** The raw OpenAPI specification (JSON) is available at [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs), which can be used for generating client code or further documentation.

> **Note:** In production, **do not expose the Internal API to the public internet.** It should be deployed on an internal network. Use strong, unique credentials for the admin password, and rotate or update them as needed to maintain security.

## Logging

This implementation uses **structured logging** in JSON format, making it easy to aggregate and analyze logs.

- By default, logs are written to the console (stdout) in JSON. If you run the services in Docker, you can collect these logs using a logging driver or an aggregator (for example, **Fluentd** or Elastic Beats).
- The system can optionally send logs to a **Graylog** server. If Graylog host and port are provided (via `EPERMIT_GRAYLOG_HOST` and `EPERMIT_GRAYLOG_PORT` in the environment), the services will emit logs to Graylog in addition to the console. This allows for centralized log management across multiple instances.

Log entries are structured (key-value pairs in JSON) to include context about requests, making it easier to filter and search in log management systems.

## Error Handling

All errors in the system are handled by a global exception handler, ensuring consistent responses. Each error is associated with an HTTP status code and an application-specific error code. The common HTTP response codes used are:

- `200` – **Success:** The request was processed successfully (for endpoints that return a result).
- `400` – **Bad Request:** The request was invalid (e.g., missing required fields or parameters).
- `401` – **Unauthorized:** Authentication failed (e.g., missing or incorrect admin password for Internal API).
- `404` – **Not Found:** The requested resource was not found.
- `422` – **Unprocessable Entity:** Validation failed (e.g., provided data does not meet requirements).
- `500` – **Internal Server Error:** An unexpected server-side error occurred.

If a **422 Unprocessable Entity** error is returned due to validation issues, the response body will include details about what went wrong. The validation error response typically contains an error code, a descriptive message, and possibly field-specific error details to aid in debugging the client’s request.

All defined error codes and their meanings can be found in the source code (see the [`ErrorCodes.java` file](https://github.com/e-permit/e-permit-java/blob/main/e-permit/src/main/java/epermit/commons/ErrorCodes.java) for a complete list of error identifiers used by the application).

## Health Checks

Each service provides a health check endpoint to verify that it is running correctly. This mechanism can be used both for internal monitoring and for other authorities to check the availability of your API service:

- The **Internal API** exposes a health check at an endpoint such as `GET /healthcheck` (on port 8081 by default). Hitting this URL will return a simple status (e.g., HTTP 200 OK with a status message) if the service is healthy.
- The **Public API** similarly can have a health/status endpoint (depending on the implementation) to allow monitoring its availability.

These health checks can be used in load balancers or uptime monitoring tools to automatically verify that the e-permit services are up and responsive. In the context of inter-country operations, an authority’s system might periodically call the other country’s health endpoint to ensure their Public API is accessible.

## Database

The e-permit system uses a **PostgreSQL** database to store all data, including permit information, quotas, usage records, and cryptographic keys/certificates. The database schema is managed via automated migrations:

- **Schema Migrations:** The Internal API service employs Flyway for database migrations. On startup (when `FLYWAY_ENABLED=true`), it will automatically apply any new migrations to the database. You can find the migration scripts in the source repository under the [`e-permit-internal-api/src/main/resources/db/migration`](https://github.com/e-permit/e-permit-java/tree/main/e-permit-internal-api/src/main/resources/db/migration) directory. These scripts define the necessary tables and alterations for the e-permit schema.
- **Data Persistence:** All critical data, such as issued permits and cryptographic keys, are stored in the database. **Ensure you back up this database regularly**, especially the data related to permit issuance and the stored private keys (which, as mentioned, are encrypted with a keystore password).

> **💡 Production Tip:** For production deployments, follow these database best practices:  
> - **Use a Stable Database Service:** Run PostgreSQL on a dedicated server or managed service. *Avoid using an ephemeral containerized database for production*, as it may not guarantee data durability or performance.  
> - **Restrict Manual Changes:** Do not manually alter the e-permit database schema or data outside of the application or migration scripts. This prevents inconsistencies.  
> - **Regular Backups:** Set up automated backups of the database to prevent data loss. Verify backup integrity periodically.  
> - **Separate Environments:** Use a separate database instance for testing/development. The production database should be isolated from non-production usage to ensure stability and security.

## **Build**

To build the project, first clone the repository and navigate to the project directory. Then, run the following command:

```bash
mvn clean package
```

### **Prerequisites**

Ensure you have the following dependencies installed:

- Maven: Version 3.9.9 or higher
- Java: Version 21 or higher

By adhering to these guidelines, you will maintain a reliable and secure database for your e-permit system.

---

With the e-permit Java implementation set up and running, you can now integrate it with your processes. The Internal API can be used by your internal applications to manage permits and quotas, and the Public API will handle secure communication with other authorities' systems. Be sure to refer to the e-permit specification for details on the data formats and protocols, and use the provided Swagger UI and documentation to explore available endpoints. 

