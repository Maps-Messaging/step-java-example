#!/bin/bash

# Test script to demonstrate different service configurations

PROPS_FILE="src/main/resources/application.properties"

echo "=== V2X Service Configuration Test ==="
echo ""

# Function to set service config
set_config() {
    local cam=$1
    local denm=$2
    sed -i '' "s/service.cam.enabled=.*/service.cam.enabled=$cam/" "$PROPS_FILE"
    sed -i '' "s/service.denm.enabled=.*/service.denm.enabled=$denm/" "$PROPS_FILE"
}

# Test 1: DENM only (default)
echo "Test 1: DENM Only (default configuration)"
echo "=========================================="
set_config false true
echo "Configuration:"
grep "service\." "$PROPS_FILE" | head -2
echo ""
echo "Running for 10 seconds..."
timeout 10 ./run-with-classpath.sh 2>&1 | grep -E "(CAM Service:|DENM Service:|Step 9|Step 10|Step 11)" | head -10
echo ""
echo "Press Enter to continue..."
read

# Test 2: CAM only
echo ""
echo "Test 2: CAM Only"
echo "================"
set_config true false
echo "Configuration:"
grep "service\." "$PROPS_FILE" | head -2
echo ""
echo "Running for 10 seconds..."
timeout 10 ./run-with-classpath.sh 2>&1 | grep -E "(CAM Service:|DENM Service:|Step 9|Step 10|Step 11)" | head -10
echo ""
echo "Press Enter to continue..."
read

# Test 3: Both services
echo ""
echo "Test 3: Both CAM and DENM"
echo "=========================="
set_config true true
echo "Configuration:"
grep "service\." "$PROPS_FILE" | head -2
echo ""
echo "Running for 10 seconds..."
timeout 10 ./run-with-classpath.sh 2>&1 | grep -E "(CAM Service:|DENM Service:|Step 9|Step 10|Step 11)" | head -10
echo ""

# Restore default
echo ""
echo "Restoring default configuration (DENM only)..."
set_config false true
echo "Done!"
