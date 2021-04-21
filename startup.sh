#!/usr/bin/env bash
set -eu

function wait_for_docker_services() {
  RUNNING=0
  LOOP_COUNT=0
  echo -n "Waiting for service(s) to become healthy ($*) ."
  until [[ ${RUNNING} == $# || ${LOOP_COUNT} == 100 ]]; do
    RUNNING=$(docker-compose ps -q "$@" | xargs docker inspect | jq -rc '[ .[] | select(.State.Health.Status == "healthy")] | length')
    LOOP_COUNT=$((LOOP_COUNT + 1))
    echo -n "."
  done
  if [[ ${LOOP_COUNT} == 100 ]]; then
    echo "FAILED"
    return 1
  fi
  echo " done!"
  return 0
}

echo "Authenticating to AWS account digital-identity-dev using gds-users for Cognito access..."

# Populate AWS credentials in environment variables to call Cognito
eval $(gds-cli aws digital-identity-dev -e)

./gradlew installDist

./gradlew run
