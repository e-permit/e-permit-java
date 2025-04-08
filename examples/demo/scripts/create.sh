#!/bin/bash 
CT="Content-Type: application/json"
A_URI="http://localhost:3050"
AUTH="admin:admin"


create_permit()
{
  cat <<EOF
  {
    "issued_for": "B",
    "permit_year": 2025,
    "permit_type": "1",
    "company_name": "TECT",
    "company_id": "123",
    "plate_number": "TECT",
    "arrival_country": "B"
  }
EOF
}
 
for i in {1..11}
do
  curl "$A_URI/permits" -u "$AUTH" -X POST --data "$(create_permit)" -H 'Content-Type: application/json'
done
