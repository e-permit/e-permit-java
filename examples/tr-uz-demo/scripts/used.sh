#!/bin/bash 
CT="Content-Type: application/json"
UZ_URI="http://localhost:3060"
AUTH="admin:admin"

permit_used()
{
  cat <<EOF
  {
    "activity_type": "ENTRANCE",
    "activity_timestamp": 1656406166,
    "activity_details": "Some Info"
  }
EOF
}

curl "$UZ_URI/permits/TR-UZ-2024-1-1/activities" -u "$AUTH" -X POST --data "$(permit_used)" -H 'Content-Type: application/json'