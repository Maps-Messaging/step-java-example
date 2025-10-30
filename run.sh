#!/bin/bash

# V2X Example Application Runner

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting V2X Example Application...${NC}"

# Check if SDK jar exists
SDK_JAR="lib/v2xsdk4java-3.1.0.jar"
if [ ! -f "$SDK_JAR" ]; then
    echo -e "${RED}Error: SDK jar not found at $SDK_JAR${NC}"
    echo "Please copy the Vodafone V2X SDK jar to the lib/ directory"
    exit 1
fi

# Check if application properties exist
if [ ! -f "src/main/resources/application.properties" ]; then
    echo -e "${RED}Error: application.properties not found${NC}"
    exit 1
fi

# Option 1: Run using the fat JAR (recommended after mvn package)
if [ -f "target/step-java-example.jar" ]; then
    echo -e "${GREEN}Running from packaged JAR...${NC}"
    java -jar target/step-java-example.jar
    exit $?
fi

# Option 2: Run using Maven with explicit classpath
echo -e "${YELLOW}JAR not found, running with Maven...${NC}"
echo "Building and running..."

# Build first
mvn clean package -DskipTests

# Run the fat JAR
if [ -f "target/step-java-example.jar" ]; then
    echo -e "${GREEN}Running application...${NC}"
    java -jar target/step-java-example.jar
else
    echo -e "${RED}Build failed or JAR not created${NC}"
    exit 1
fi
