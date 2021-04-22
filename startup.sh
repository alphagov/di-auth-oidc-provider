#!/usr/bin/env bash
set -eu

./gradlew installDist

./gradlew run
