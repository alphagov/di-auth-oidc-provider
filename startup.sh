#!/usr/bin/env bash
set -eu

CONFIG_FILE=oidc-provider.yml

./gradlew installDist

./build/install/di-auth-oidc-provider/bin/di-auth-oidc-provider server $CONFIG_FILE
