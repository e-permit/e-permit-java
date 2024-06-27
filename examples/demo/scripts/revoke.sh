#!/bin/bash 
CT="Content-Type: application/json"
A_URI="http://localhost:3050"
AUTH="admin:admin"

curl "$A_URI/permits/A-B-2024-1-2" -u "$AUTH" -X DELETE  -H 'Content-Type: application/json'