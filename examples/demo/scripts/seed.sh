#!/bin/bash 
CT="Content-Type: application/json"
A_URI="http://localhost:3050"
B_URI="http://localhost:3060"
AUTH="admin:admin"

# Scenerio: A -> B 

create_a_authority()
{
  cat <<EOF
  {
    "code": "A",
    "name": "CountryA",
    "public_api_uri": "http://a-public-api:8080"
  }
EOF
}

create_b_authority()
{
  cat <<EOF
  {
    "code": "B",
    "name": "CountryB",
    "public_api_uri": "http://b-public-api:8080"
  }
EOF
}

create_quota()
{
  cat <<EOF
  {
    "permit_type": "1",
    "permit_year": 2024,
    "quantity": 100
  }
EOF
}

curl "$A_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_b_authority)" 
curl "$B_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_a_authority)" 
curl "$B_URI/authorities/A/quotas" -H 'Content-Type: application/json' -u "$AUTH" -X POST --data "$(create_quota)" 