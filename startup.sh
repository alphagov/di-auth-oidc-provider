#!/usr/bin/env bash
set -eu

echo "Authenticating to AWS account digital-identity-dev using gds-users for Cognito access..."

# Populate AWS credentials in environment variables to call Cognito
eval $(gds-cli aws digital-identity-dev -e)

./gradlew installDist

./gradlew run
