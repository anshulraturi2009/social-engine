@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM This script will download maven if not exists and run maven

@echo off
setlocal enabledelayedexpansion

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
setlocal enabledelayedexpansion
cd /d "%DIRNAME%.."

set MAVEN_PROJECTBASEDIR=%cd%
set MAVEN_SCRIPT_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
set MAVEN_WRAPPER_JAR="%MAVEN_SCRIPT_DIR%\maven-wrapper.jar"

if not exist "%MAVEN_WRAPPER_JAR%" (
  if "%MAVEN_VERBOSE%"=="true" (
    echo Couldn't find %MAVEN_WRAPPER_JAR%, downloading it ...
  )
  if have %JAVA_HOME% goto findJavaFromJavaHome
  set JAVA_EXE=java.exe
  %JAVA_EXE% -version >nul 2>&1
  if "%ERRORLEVEL%"=="0" goto init
  echo Error: JAVA_HOME not set and no 'java' command could be found in your PATH. >&2
  exit /b 1
  :findJavaFromJavaHome
  set JAVA_HOME=%JAVA_HOME:"=%
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
  if exist "%JAVA_EXE%" goto init
  echo Error: JAVA_HOME is set to an invalid directory: %JAVA_HOME% >&2
  exit /b 1
)

:init
@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%

java -cp %MAVEN_WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
if %ERRORLEVEL% neq 0 (
  if "%MAVEN_VERBOSE%"=="true" echo Failed at %ERRORLEVEL%.
  exit /b %ERRORLEVEL%
)
