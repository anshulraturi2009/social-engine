#!/bin/sh
set -e
basedir=$(dirname "$0")
basedir=$(cd "$basedir" && pwd)
MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$basedir"}
MAVEN_SCRIPT_DIR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
MAVEN_WRAPPER_JAR="$MAVEN_SCRIPT_DIR/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES_PATH="$MAVEN_SCRIPT_DIR/maven-wrapper.properties"

if [ -r "$MAVEN_WRAPPER_PROPERTIES_PATH" ]; then
  . "$MAVEN_WRAPPER_PROPERTIES_PATH"
else
  echo "ERROR: Unable to locate maven-wrapper.properties"
  exit 1
fi

if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  if command -v wget > /dev/null; then
    wget -q "$wrapperUrl" -O "$MAVEN_WRAPPER_JAR"
  elif command -v curl > /dev/null; then
    curl -s -o "$MAVEN_WRAPPER_JAR" "$wrapperUrl"
  else
    echo "ERROR: Unable to download wrapper jar"
    exit 1
  fi
fi

exec java -cp "$MAVEN_WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
