#!/bin/bash
set -e

echo "Finding Java..."
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
echo "JAVA_HOME: $JAVA_HOME"

echo "Installing Maven..."
MAVEN_VERSION="3.9.6"
MAVEN_HOME="/tmp/maven"

if [ ! -d "$MAVEN_HOME" ]; then
  mkdir -p "$MAVEN_HOME"
  wget -q "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -O /tmp/maven.tar.gz
  tar -xzf /tmp/maven.tar.gz -C "$MAVEN_HOME" --strip-components=1
fi

export PATH="$MAVEN_HOME/bin:$PATH"

echo "Building project..."
mvn clean package -DskipTests

echo "Build completed!"
