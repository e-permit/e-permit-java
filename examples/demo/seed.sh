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
    "api_uri": "http://localhost:3051"
  }
EOF
}

create_uz_authority()
{
  cat <<EOF
  {
    "api_uri": "http://10.6.7.214:3061"
  }
EOF
}

create_quota()
{
  cat <<EOF
  {
    "authority_code": "TR",
    "permit_type": "BILATERAL",
    "permit_year": 2022,
    "start_number": 1,
    "end_number": 250
  }
EOF
}

curl "$TR_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_uz_authority)" 
curl "$UZ_URI/authorities" -u "$AUTH" -H 'Content-Type: application/json' -X POST --data "$(create_tr_authority)" 
curl "$UZ_URI/authority_quotas" -H 'Content-Type: application/json' -u "$AUTH" -X POST --data "$(create_quota)" 