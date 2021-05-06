#!/usr/bin/env bash
set -eu

USE_AWS=0
export AUTHENTICATION_SERVICE_PROVIDER=user
while [[ $# -gt 0 ]]; do
  case $1 in
  --aws)
    USE_AWS=1
    ;;
  --cognito)
    export AUTHENTICATION_SERVICE_PROVIDER=cognito
    USE_AWS=1
    ;;
  --user)
    ;;
  --srp)
    export AUTHENTICATION_SERVICE_PROVIDER=srp
    ;;
  esac
  shift
done

if [[ ${USE_AWS} == "1" ]]; then
  echo "Authenticating to AWS account digital-identity-dev using gds-users..."
  # Populate AWS credentials in environment variables to use AWS
  eval $(gds-cli aws digital-identity-dev -e)
fi

./gradlew installDist
./gradlew run
