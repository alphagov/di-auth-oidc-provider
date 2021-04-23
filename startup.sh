#!/usr/bin/env bash
set -eu

USE_COGNITO=0
while [[ $# -gt 0 ]]; do
  case $1 in
  --cognito)
    USE_COGNITO=1
    ;;
  --user)
    USE_COGNITO=0
    ;;
  esac
  shift
done

if [[ ${USE_COGNITO} == "1" ]]; then
  echo "Authenticating to AWS account digital-identity-dev using gds-users for Cognito access..."
  # Populate AWS credentials in environment variables to call Cognito
  eval $(gds-cli aws digital-identity-dev -e)
  export AUTHENTICATION_SERVICE_PROVIDER=cognito
else
  export AUTHENTICATION_SERVICE_PROVIDER=user
fi

./gradlew installDist

./gradlew run
