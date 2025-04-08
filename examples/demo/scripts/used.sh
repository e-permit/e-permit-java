#!/bin/bash 
CT="Content-Type: application/json"
B_URI="http://localhost:3060"
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

curl "$B_URI/permits/A-B-2025-1-1/activities" -u "$AUTH" -X POST --data "$(permit_used)" -H 'Content-Type: application/json'