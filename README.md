# e-permit-java

## Quickstart

Create ```epermit.env``` file in the working directory and fill below properties: 

#### epermit.env

```properties
SPRING_PROFILES_ACTIVE=dev
HIBERNATE_DDL_AUTO=none
FLYWAY_ENABLED=true
FLYWAY_SCHEMAS=public
SPRING_DATASOURCE_URL=<db url>
SPRING_DATASOURCE_USERNAME=<db user>
SPRING_DATASOURCE_PASSWORD=<db pwd>
SPRING_DATASOURCE_DRIVER=<db driver>
SPRING_DATASOURCE_DIALECT=<dialect>
EPERMIT_ISSUER_CODE=<Country code>
EPERMIT_ISSUER_NAME=<Country name>
EPERMIT_ADMIN_PASSWORD=<admin pwd>
EPERMIT_KEY_PASSWORD=<admin pwd for encrypting key>
# Optional(if you use docker compose you can mount host volume to container volume )
EPERMIT_LOG_BASEPATH=<log base path e.g /var/log/epermit> 
```

### To run public api

Create a docker-compose.yml file in working directory with following content:

```yaml
version: '3.7'
services:
  public-api:
    image: ghcr.io/e-permit/publicapi:latest
    env_file: 
      - epermit.env
    ports:
      - "8080:8080"
```

### To run internal api

Create a docker-compose.yml file in working directory with following content:

```yaml
version: '3.7'
services:
  public-api:
    image: ghcr.io/e-permit/internalapi:latest
    env_file: 
      - epermit.env
    ports:
      - "8080:8080"
```

## Development node

### Docker compose

```yaml
version: '3.7'
services:
  internal-api:
    image: ghcr.io/e-permit/internalapi:latest
    depends_on:
      - epermitdb
    env_file: 
      - epermit.env
    ports:
      - "3020:8080"
  public-api:
    image: ghcr.io/e-permit/publicapi:latest
    depends_on:
      - epermitdb
    env_file: 
      - epermit.env
    ports:
      - "3021:8080"
  epermitdb:
    image: 'postgres:13.1-alpine'
    container_name: epermitdb
    environment:
      - POSTGRES_USER=compose-postgres
      - POSTGRES_PASSWORD=compose-postgres
      - POSTGRES_DB=devdb
    ports:
      - "5432:5432"
```

### Environment Variables

```properties
SPRING_PROFILES_ACTIVE=dev
HIBERNATE_DDL_AUTO=none
FLYWAY_ENABLED=true
FLYWAY_SCHEMAS=public
SPRING_DATASOURCE_URL=jdbc:postgresql://epermitdb:5432/devdb
SPRING_DATASOURCE_USERNAME=compose-postgres
SPRING_DATASOURCE_PASSWORD=compose-postgres
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_DATASOURCE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
EPERMIT_ISSUER_CODE=TR
EPERMIT_ISSUER_NAME=Turkiye
EPERMIT_ADMIN_PASSWORD=*
EPERMIT_KEY_PASSWORD=*
```


## Build 

```mvn package```

To make services up:

```docker-compose pull  && docker-compose up -d```

Your internal api: ```http://localhost:3020``` or ```https://<your domain>:3020```

Your public api: ```http://localhost:3021``` or ```https://<your domain>:3021```

After services are up and running following script can be executed:

To add a new country, send http post to your internal api(http://localhost:3020/authorities). Suppose country api url is "http:/country.gov" then post body should be like this:

`POST` to `http://localhost:3020/authorities`

```json
{
  "api_uri": "http:/country.gov"
}
```

Then executing same request for other side(country), handshaking will be estabilished. 

After handshaking you can define a quota for that country(suppose TR) with:

`POST` to `http://localhost:3020/authority_quotas`

```json
{
    "authority_code": "TR",
    "permit_type": "BILATERAL",
    "permit_year": 2021,
    "start_number": 1,
    "end_number": 250
}
```

Now other country can define permits for its own vehicles.
If other country gives you some quota with same way, you can also define permit like below:


`POST` to `http://localhost:3020/permits`

```json
{
    "issued_for": "TR",
    "permit_year": 2021,
    "permit_type": "BILATERAL",
    "company_name": "TECT",
    "company_id": "123",
    "plate_number": "TECT"
}
```

When vehicle does enter-exit, target country can send info via:

`POST` to `http://localhost:3020/permits/{id}/activities`

id: e.g TR-UZ-2022-1-1

```json
{
    "activity_type": "ENTRANCE | EXIT",
    "activity_timestamp": 1656406166,
    "activity_details": "Optional"
}
```

If a country needs to revoke unused permit:

`DELETE` to `http://localhost:3020/permits/{id}`

id: e.g TR-UZ-2022-1-1

Sample script with tr-uz scenerio:

```
#!/bin/bash 
CT="Content-Type: application/json"
TR_INTERNAL_URI="http://localhost:3020"
UZ_INTERNAL_URI="http://localhost:3030"
AUTH="admin:******"

create_tr_authority()
{
  cat <<EOF
  {
    "api_uri": "http://localhost:3021"
  }
EOF
}

create_uz_authority()
{
  cat <<EOF
  {
    "api_uri": "http://localhost:3031"
  }
EOF
}

create_tr_quota()
{
  cat <<EOF
  {
    "authority_code": "TR",
    "permit_type": "BILATERAL",
    "permit_year": 2021,
    "start_number": 1,
    "end_number": 250
  }
EOF
}

create_uz_quota()
{
  cat <<EOF
  {
    "authority_code": "UZ",
    "permit_type": "BILATERAL",
    "permit_year": 2021,
    "start_number": 1,
    "end_number": 100
  }
EOF
}

create_tr_permit()
{
  cat <<EOF
  {
    "issued_for": "UZ",
    "permit_year": 2021,
    "permit_type": "BILATERAL",
    "company_name": "TECT",
    "company_id": "123",
    "plate_number": "TECT"
  }
EOF
}

curl "$TR_INTERNAL_URI/authorities" -u "$AUTH" -X POST --data "$(create_uz_authority)" 
curl "$UZ_INTERNAL_URI/authorities" -u "$AUTH" -X POST --data "$(create_tr_authority)" 
curl "$UZ_INTERNAL_URI/authority_quotas" -u "$AUTH" -X POST --data "$(create_tr_quota)" 
curl "$TR_INTERNAL_URI/authority_quotas" -u "$AUTH" -X POST --data "$(create_uz_quota)" 

for i in {1..250}
do
   curl "$TR_INTERNAL_URI/permits" -u "$AUTH" -X POST --data "$(create_tr_permit)" -H '$CT'; 
done
```
