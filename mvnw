#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# This script will download maven if not exists and run maven

set -e

basedir=$(dirname "$0")
basedir=$(cd "$basedir/.." && pwd)

MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$basedir"}
MAVEN_SCRIPT_DIR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
MAVEN_WRAPPER_JAR="$MAVEN_SCRIPT_DIR/maven-wrapper.jar"
MAVEN_DOWNLOAD_OUTPUT_DIR=${MAVEN_DOWNLOAD_OUTPUT_DIR:-"$MAVEN_SCRIPT_DIR"}

mkdir -p "$MAVEN_DOWNLOAD_OUTPUT_DIR"

if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  if command -v wget > /dev/null; then
    wget -q "$wrapperUrl" -O "$MAVEN_WRAPPER_JAR" 2>/dev/null || wget "$wrapperUrl" -O "$MAVEN_WRAPPER_JAR"
  elif command -v curl > /dev/null; then
    curl -s -o "$MAVEN_WRAPPER_JAR" "$wrapperUrl"
  else
    echo "Downloading $wrapperUrl"
    java -cp "$MAVEN_SCRIPT_DIR" MavenWrapperDownloader "$wrapperUrl" "$MAVEN_WRAPPER_JAR"
  fi
fi

exec java \
  -classpath "$MAVEN_WRAPPER_JAR" \
  "-Dmaven.home=${MAVEN_HOME}" \
  "-Dclassworlds.conf=${MAVEN_PROJECTBASEDIR}/.mvn/wrapper/m2.conf" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
