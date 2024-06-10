#!/bin/bash 
CT="Content-Type: application/json"
TR_URI="http://localhost:3050"
AUTH="admin:admin"


create_permit()
{
  cat <<EOF
  {
    "issued_for": "UZ",
    "permit_year": 2024,
    "permit_type": "1",
    "company_name": "TECT",
    "company_id": "123",
    "plate_number": "TECT",
    "arrival_country": "UZ"
  }
EOF
}
 
for i in {1..100}
do
  curl "$TR_URI/permits" -u "$AUTH" -X POST --data "$(create_permit)" -H 'Content-Type: application/json'
done
