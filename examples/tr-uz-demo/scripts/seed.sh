#!/bin/bash 
CT="Content-Type: application/json"
TR_URI="http://localhost:3050"
UZ_URI="http://localhost:3060"
AUTH="admin:admin"

# Scenerio: TR -> UZ 

create_tr_authority()
{
  cat <<EOF
  {
    "code": "TR",
    "name": "Türkiye",
    "public_api_uri": "http://tr-public-api:8080"
  }
EOF
}

create_uz_authority()
{
  cat <<EOF
  {
    "code": "UZ",
    "name": "Uzbekistan",
    "public_api_uri": "http://uz-public-api:8080"
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

curl "$TR_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_uz_authority)" 
curl "$UZ_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_tr_authority)" 
curl "$UZ_URI/authorities/TR/quotas" -H 'Content-Type: application/json' -u "$AUTH" -X POST --data "$(create_quota)" 