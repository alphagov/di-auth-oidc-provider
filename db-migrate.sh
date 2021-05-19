#!/usr/bin/env bash

set -eu
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

OS=$(uname -s)
case "${OS}" in
  Darwin*)
    PGHOST=host.docker.internal
    NETWORK=bridge
    ;;
  Linux*)
    NETWORK=host
    ;;
esac

docker run -it \
  -e FLYWAY_URL="jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}" \
  -e FLYWAY_USER="${PGUSER}" \
  -e FLYWAY_PASSWORD="${PGPASSWORD}" \
  -v "${SCRIPT_DIR}/sql:/flyway/sql" \
  --network "${NETWORK}" \
  flyway/flyway:6.2.1 migrate -locations=filesystem:/flyway/sql
