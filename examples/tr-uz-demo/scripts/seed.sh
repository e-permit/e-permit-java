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
    "client_id": "TR/GOV/UBAK/HAULAGE"
  }
EOF
}

create_uz_authority()
{
  cat <<EOF
  {
    "code": "UZ",
    "name": "Uzbekistan",
    "client_id": "UZ/GOV/MT/HAULAGE"
  }
EOF
}

create_quota()
{
  cat <<EOF
  {
    "authority_code": "TR",
    "permit_type": "BILATERAL",
    "permit_year": 2023,
    "quantity": 100
  }
EOF
}

curl "$TR_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_uz_authority)" 
curl "$UZ_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_tr_authority)" 
curl "$UZ_URI/authority_quotas" -H 'Content-Type: application/json' -u "$AUTH" -X POST --data "$(create_quota)" 