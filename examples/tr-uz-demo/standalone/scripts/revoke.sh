#!/bin/bash 
CT="Content-Type: application/json"
TR_URI="http://localhost:3050"
AUTH="admin:admin"

curl "$TR_URI/permits/TR-UZ-2024-1-2" -u "$AUTH" -X DELETE  -H 'Content-Type: application/json'