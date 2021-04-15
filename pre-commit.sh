#!/usr/bin/env bash

printf "\nRunning build and unit tests...\n"

./gradlew clean build

build_and_test_exit_code=$?
if [ ${build_and_test_exit_code} -ne 0 ]
then
    printf "\npre-commit failed.\n"
else
    printf "\npre-commit SUCCEEDED.\n"
fi