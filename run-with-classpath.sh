#!/bin/bash

# Run with explicit classpath (alternative approach)

SDK_JAR="lib/v2xsdk4java-3.1.0.jar"
APP_JAR="target/step-java-example.jar"

if [ ! -f "$SDK_JAR" ]; then
    echo "Error: SDK jar not found at $SDK_JAR"
    exit 1
fi

# Build without packaging SDK
mvn clean package -DskipTests

# Run with both JARs in classpath
java -cp "$APP_JAR:$SDK_JAR" com.vodafone.v2x.example.V2XApplication
