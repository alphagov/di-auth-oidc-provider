#!/usr/bin/env bash
DOCKER_BASE=docker-compose
function wait_for_docker_services() {
  RUNNING=0
  LOOP_COUNT=0
  echo -n "Waiting for service(s) to become healthy ($*) ."
  until [[ ${RUNNING} == $# || ${LOOP_COUNT} == 100 ]]; do
    RUNNING=$(${DOCKER_BASE} ps -q "$@" | xargs docker inspect | jq -rc '[ .[] | select(.State.Health.Status == "healthy")] | length')
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

function start_docker_services() {
  ${DOCKER_BASE} up --build -d --no-deps --quiet-pull "$@"
}

function stop_docker_services() {
  ${DOCKER_BASE} down --rmi local --remove-orphans
}

function build_docker_service() {
  ${DOCKER_BASE} build --quiet "$@"
}

printf "\nRunning build and unit tests...\n"

./gradlew clean build -x :acceptance-tests:test

build_and_test_exit_code=$?
if [ ${build_and_test_exit_code} -ne 0 ]
then
    printf "\npre-commit failed.\n"
    exit 1
fi

printf "\nRunning build and unit tests...\n"
build_docker_service op-db flyway
start_docker_services op-db flyway selenium
wait_for_docker_services op-db

build_docker_service di-auth-stub-relying-party di-auth-oidc-provider
start_docker_services di-auth-stub-relying-party di-auth-oidc-provider
wait_for_docker_services di-auth-stub-relying-party di-auth-oidc-provider

SELENIUM_URL="http://localhost:4444/wd/hub" IDP_URL="http://di-auth-oidc-provider:8080/" RP_URL="http://di-auth-stub-relying-party:8081/" ./gradlew cucumber
build_and_test_exit_code=$?

stop_docker_services op-db flyway selenium di-auth-stub-relying-party di-auth-oidc-provider

if [ ${build_and_test_exit_code} -ne 0 ]
then
    printf "\npre-commit failed.\n"

else
    printf "\npre-commit SUCCEEDED.\n"
fi