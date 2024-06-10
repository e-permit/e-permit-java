**e-permit-java**
================

A Java-based implementation of the e-permit system.

**Quickstart**
-------------

To get started, follow these steps:

### 1. Create an `epermit.env` file

Create a file named `epermit.env` in the working directory with the following properties:

```properties
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=<db url>
SPRING_DATASOURCE_USERNAME=<db user>
SPRING_DATASOURCE_PASSWORD=<db pwd>
SPRING_DATASOURCE_DRIVER=<db driver>
SPRING_DATASOURCE_DIALECT=<dialect>
EPERMIT_ISSUER_CODE=<Country code>
EPERMIT_ISSUER_NAME=<Country name>
# Optional
EPERMIT_GRAYLOG_HOST=<host>
EPERMIT_GRAYLOG_PORT=<port>
```

### 2. Run the Public API using Docker Compose

Create a `docker-compose.yml` file in the working directory with the following content:

```yaml
version: '3.8'
services:
  public-api:
    image: ghcr.io/e-permit/publicapi:latest
    env_file: 
      - epermit.env
    ports:
      - "8080:8080"
```

### 3. Run the Internal API using Docker Compose

Create a `docker-compose.yml` file in the working directory with the following content:

```yaml
version: '3.8'
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
```

### API Endpoints

Once the services are up and running, you can execute the following scripts using the Internal API endpoints:

#### Handshake:

`POST /authorities`

```json
{
    "code": "<country code>",
    "name": "<country name>",
    "public_api_uri": "https://..."
}
```

#### Create Quota:

`POST /authorities/<country code>/quotas`

```json
{
    "permit_type": "1",
    "permit_year": 2024,
    "quantity": 100
}
```

#### Create Permit:

`POST /permits`

```json
{
    "issued_for": "<country code>",
    "permit_year": 2024,
    "permit_type": "1",
    "company_name": "TEST",
    "company_id": "123",
    "plate_number": "TEST",
    "arrival_country": "<country code>"
}
```

Full demo can be found at: https://github.com/e-permit/e-permit-java/tree/x-road/examples/tr-uz-demo

## Build

To build the project, run the following command:

```
mvn package
```
