#!/bin/sh
# Maven Wrapper startup script

if [ -z "$JAVA_HOME" ] ; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")"; pwd)"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if [ -r "$WRAPPER_JAR" ]; then
    echo "Found $WRAPPER_JAR"
else
    echo "Downloading from: $DOWNLOAD_URL"
    curl -o "$WRAPPER_JAR" "$DOWNLOAD_URL"
    echo "Finished downloading $WRAPPER_JAR"
fi

exec "$JAVA_HOME/bin/java" -classpath "$WRAPPER_JAR" "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" $WRAPPER_LAUNCHER "$@"
