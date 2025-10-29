#!/bin/bash

# V2X Sample Application Runner
# This script builds and runs the V2X sample application

set -e

echo "=== V2X Sample Application Runner ==="
echo ""

# Check if the SDK jar exists
if [ ! -f "lib/vodafone-step-sdk.jar" ]; then
    echo "WARNING: Vodafone STEP SDK jar not found at lib/vodafone-step-sdk.jar"
    echo "The application will run in simulation mode."
    echo ""
fi

# Build the application
echo "Building the application..."
mvn clean package -q

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo ""
    
    # Run the application
    echo "Running V2X Sample Application..."
    echo ""
    java -jar target/v2x-sample-1.0.0.jar
else
    echo "Build failed!"
    exit 1
fi
