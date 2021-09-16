# e-permit-java

Create ```tr.env``` and ```uz.env``` files in the root directory to run services. The sample ```.env``` file is like following:

#### tr.env

```
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://trdb:5432/devdb
SPRING_DATASOURCE_USERNAME=compose-postgres
SPRING_DATASOURCE_PASSWORD=compose-postgres
EPERMIT_ISSUER_CODE=TR
EPERMIT_ISSUER_NAME=Turkey
EPERMIT_ADMIN_PASSWORD=******
EPERMIT_KEY_PASSWORD=******
EPERMIT_VERIFY_URI=https://e-permit.github.io/verify
EPERMIT_ADMIN_PASSWORD=******
EPERMIT_PRIVATEKEY=******
```

#### uz.env

```
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://uzdb:5432/devdb
SPRING_DATASOURCE_USERNAME=compose-postgres
SPRING_DATASOURCE_PASSWORD=compose-postgres
EPERMIT_ISSUER_CODE=UZ
EPERMIT_ISSUER_NAME=Uzbekistan
EPERMIT_ADMIN_PASSWORD=******
EPERMIT_KEY_PASSWORD=******
EPERMIT_VERIFY_URI=https://e-permit.github.io/verify
EPERMIT_ADMIN_PASSWORD=******
EPERMIT_PRIVATEKEY=******
```

```docker-compose up```

After services are up and running following script can be executed:

```
#!/bin/bash 
CT="Content-Type: application/json"
TR_URI="http://10.6.7.214:3020"
UZ_URI="http://10.6.7.214:3030"
AUTH="admin:******"

create_tr_authority()
{
  cat <<EOF
  {
    "api_uri": "$TR_URI"
  }
EOF
}

create_uz_authority()
{
  cat <<EOF
  {
    "api_uri": "$UZ_URI"
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

curl "$TR_URI/authorities" -H "Content-Type: application/json" -u "$AUTH" -X POST --data "$(create_uz_authority)" 
curl "$UZ_URI/authorities" -H "Content-Type: application/json" -u "$AUTH" -X POST --data "$(create_tr_authority)" 
curl "$UZ_URI/authority_quotas" -H "Content-Type: application/json" -u "$AUTH" -X POST --data "$(create_tr_quota)" 
curl "$TR_URI/authority_quotas" -H "Content-Type: application/json" -u "$AUTH" -X POST --data "$(create_uz_quota)" 

for i in {1..250}
do
   curl "$TR_URI/permits" -H "Content-Type: application/json" -u "$AUTH" -X POST --data "$(create_tr_permit)" -H '$CT'; 
done
```
