#!/bin/sh

PASSWORD=changeit

# Import all custom certificates from /opt/certs into the truststore
for cert in /opt/certs/*.crt; do
  if [ -f "$cert" ]; then
    echo "Adding $cert to cacerts"
    keytool -importcert -noprompt -storepass "$PASSWORD" -cacerts -file "$cert" -alias $(basename "$cert")
    echo "Verifying certificate $(basename "$cert") in the truststore..."
    keytool -list -storepass "$PASSWORD" -cacerts -alias $(basename "$cert")
  fi
done

# Start the Java application
exec "$@"
