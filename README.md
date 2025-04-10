# **e-permit-java**

A Java-based implementation of the e-permit system.
<details>
   <summary>⚠️ Production Environment Guidelines</summary>

### Database
- Must be stable (do not use Docker Compose for the database).
- Manual changes should be restricted.
- Regular backups are required.
- The production database should be separate from the test database.

### Passwords
- Use strong, unique passwords for the production environment.
- Production passwords should differ from those used in test environments.
- Passwords should not be shared with anyone.
- The keystore password must be backed up securely.

### APIs
- Use container orchestration solutions, such as Kubernetes, whenever possible.
- The internal API should be hosted on the intranet, not the internet.
- The public API should be accessible over the internet and use HTTPS to ensure secure communication.

</details>

## **Quickstart**

Follow these steps to set up and run the e-permit system:

### 1. Create an `epermit.env` File

Create a file named `epermit.env` in your working directory and add the following properties:

```properties
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=<db_url>
SPRING_DATASOURCE_USERNAME=<db_user>
SPRING_DATASOURCE_PASSWORD=<db_pwd>
SPRING_DATASOURCE_DRIVER=<db_driver>
SPRING_DATASOURCE_DIALECT=<dialect>
EPERMIT_ISSUER_CODE=<country_code>
EPERMIT_ISSUER_NAME=<country_name>

# Optional if graylog exists
EPERMIT_GRAYLOG_HOST=<host>
EPERMIT_GRAYLOG_PORT=<port>
```

> **💡 Tip**:  
> Create a `certs` folder in your working directory and place `*.crt` files in it if you need to add custom certificates to the truststore.

### 2. Run the Internal API with Docker Compose

Create a `docker-compose.yml` file in the working directory with the following content:

```yaml
services:
  internal-api:
    container_name: internal-api
    image: ghcr.io/e-permit/internalapi:latest
    environment:
      - EPERMIT_ADMIN_PASSWORD=<secret>
      - EPERMIT_KEYSTORE_PASSWORD=<secret>
      - FLYWAY_ENABLED=true
      - FLYWAY_SCHEMAS=public
    env_file:
      - epermit.env
    ports:
      - "8080:8080"
    # Optional if extra certificates are needed
    volumes:
      - ./certs:/opt/certs
```

### 3. Run the Public API with Docker Compose

Create a `docker-compose.yml` file in the working directory with the following content:

```yaml
services:
  public-api:
    image: ghcr.io/e-permit/publicapi:latest
    env_file: 
      - epermit.env
    ports:
      - "8080:8080"
```

### **API Endpoints**

After starting the services, you can use the following endpoints with the Internal API:

#### Handshake
`POST /authorities`

```json
{
    "code": "<country_code>",
    "name": "<country_name>",
    "public_api_uri": "https://<public_api_uri>"
}
```

#### Create Quota
`POST /authorities/<country_code>/quotas`

```json
{
    "permit_type": 1,
    "permit_year": 2024,
    "quantity": 100
}
```

#### Create Permit
`POST /permits`

```json
{
    "issued_for": "<country_code>",
    "permit_year": 2024,
    "permit_type": 1,
    "company_name": "TEST",
    "company_id": "123",
    "plate_number": "TEST",
    "departure_country": "<country_code>",
    "arrival_country": "<country_code>"
}
```

#### Health Check
`GET /healthcheck`

For a complete demonstration, refer to the [e-permit demo](https://github.com/e-permit/e-permit-java/tree/main/examples/demo).

## **Build**

To build the project, first clone the repository and navigate to the project directory. Then, run the following command:

```bash
mvn clean package
```

### **Prerequisites**

Ensure you have the following dependencies installed:

- Maven: Version 3.9.9 or higher
- Java: Version 21 or higher