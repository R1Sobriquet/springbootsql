#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.2.0
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_CMD" ]; then
  MAVEN_CMD=mvn
fi

if [ -z "$JAVA_HOME" ]; then
  javaExecutable="$(which javac)"
  if [ -n "$javaExecutable" ] && ! [ "$(expr "$javaExecutable" : '\([^ ]*\)')" = "no" ]; then
    readLink="$(which readlink)"
    if [ ! "$(expr "$readLink" : '\([^ ]*\)')" = "no" ]; then
      if [ "$(uname)" = "Darwin" ]; then
        javaHome="$(dirname "$javaExecutable")"
        javaHome="$(cd "$javaHome" && pwd -P)"
      else
        javaExecutable="$(readlink -f "$javaExecutable")"
        javaHome="$(dirname "$javaExecutable")"
        javaHome="$(cd "$javaHome" && cd .. && pwd)"
      fi
      JAVA_HOME="$javaHome"
      export JAVA_HOME
    fi
  fi
fi

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
export MAVEN_PROJECTBASEDIR

exec $MAVEN_CMD "$@"
