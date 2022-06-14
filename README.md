# e-permit-java

Create ```.env``` files in the root directory to run services. 
The sample ```.env``` file is like following:


#### epermit.env

```
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://epermitdb:5432/devdb
SPRING_DATASOURCE_USERNAME=compose-postgres
SPRING_DATASOURCE_PASSWORD=compose-postgres
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_DATASOURCE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
EPERMIT_ISSUER_CODE=UZ
EPERMIT_ISSUER_NAME=Uzbekistan
EPERMIT_ADMIN_PASSWORD=<pwd>
EPERMIT_KEY_PASSWORD=<pwd>
EPERMIT_VERIFY_URI=<verify uri eg https://e-permit.github.io/verify>
EPERMIT_GRAYLOG_HOST=<Graylog host>
EPERMIT_GRAYLOG_PORT=12301

```
To build: 

```mvn package```

To make services up:

```docker-compose up```

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
    "permit_type": "BILITERAL",
    "permit_year": 2021,
    "start_number": 1,
    "end_number": 250
}
```

Now other country can define permits for own vehicles.
If other country gives you some quota with same way, you can also define permit like below:


`POST` to `http://localhost:3020/permits`

```json
{
    "issued_for": "TR",
    "permit_year": 2021,
    "permit_type": "BILITERAL",
    "company_name": "TECT",
    "company_id": "123",
    "plate_number": "TECT"
}
```

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
    "permit_type": "BILITERAL",
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
    "permit_type": "BILITERAL",
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
    "permit_type": "BILITERAL",
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
