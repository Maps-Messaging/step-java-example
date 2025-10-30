#!/bin/bash

# V2X STEP Java Application Runner
# This script builds and runs the V2X application

set -e

echo "=== V2X STEP Java Application Runner ==="
echo ""

# Check if the SDK jar exists
if [ ! -f "lib/v2xsdk4java-3.1.0.jar" ]; then
    echo "ERROR: Vodafone V2X SDK jar not found at lib/v2xsdk4java-3.1.0.jar"
    echo "Please place the SDK jar file in the lib/ directory"
    echo ""
    echo "To obtain the SDK:"
    echo "  1. Contact Vodafone or download from official sources"
    echo "  2. Copy to: lib/v2xsdk4java-3.1.0.jar"
    echo ""
    exit 1
fi

# Check if application.properties exists
if [ ! -f "src/main/resources/application.properties" ]; then
    echo "ERROR: Configuration file not found at src/main/resources/application.properties"
    echo "Please create the configuration file with your STEP credentials"
    echo ""
    echo "Example:"
    echo "  app.id=YOUR_APPLICATION_ID_HERE"
    echo "  app.token=YOUR_APPLICATION_TOKEN_HERE"
    echo "  test.latitude=48.866667"
    echo "  test.longitude=2.333333"
    echo ""
    exit 1
fi

# Build the application
echo "Building the application..."
mvn clean package -q

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo ""
    
    # Run the application
    echo "Running V2X STEP Java Application..."
    echo ""
    java -jar target/step-java-example-1.0.0-shaded.jar
else
    echo "Build failed!"
    exit 1
fi
