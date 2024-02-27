#!/usr/bin/env bash

JAR_FILE_NAME="busybeaver-api.jar";

pushd . &> /dev/null;
cd "$(dirname "$0")";
SCRIPT_DIR="$(pwd)" &> /dev/null;

cd $SCRIPT_DIR;

./gradlew clean build;

./gradlew bootJar;

mv build/libs/$JAR_FILE_NAME .;

echo "Created jar and moved it over";

popd &> /dev/null;

