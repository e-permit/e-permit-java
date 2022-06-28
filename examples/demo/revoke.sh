#!/bin/bash 
CT="Content-Type: application/json"
TR_URI="http://10.6.7.214:3050"
UZ_URI="http://10.6.7.214:3060"
AUTH="admin:admin"

curl "$TR_URI/permits/TR-UZ-2022-1-2" -u "$AUTH" -X DELETE  -H 'Content-Type: application/json'