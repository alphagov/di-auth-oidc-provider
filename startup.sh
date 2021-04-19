#!/usr/bin/env bash
set -eu

CONFIG_FILE=oidc-provider.yml

./gradlew installDist

docker-compose down || true
docker-compose up -d

./build/install/di-auth-oidc-provider/bin/di-auth-oidc-provider server $CONFIG_FILE
