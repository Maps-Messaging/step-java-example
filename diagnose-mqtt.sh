#!/bin/bash

echo "=== MQTT Connection Diagnostics ==="
echo ""
echo "1. Testing DNS resolution for STEP MQTT server:"
host dev-de-mn.mqtt.step.vodafone.com
echo ""

echo "2. Testing TCP connection to MQTT port 8883:"
timeout 5 bash -c "cat < /dev/null > /dev/tcp/dev-de-mn.mqtt.step.vodafone.com/8883" && echo "✓ TCP connection successful" || echo "✗ TCP connection failed"
echo ""

echo "3. Checking TLS certificate:"
echo | openssl s_client -connect dev-de-mn.mqtt.step.vodafone.com:8883 -servername dev-de-mn.mqtt.step.vodafone.com 2>/dev/null | openssl x509 -noout -subject -issuer -dates
echo ""

echo "4. Testing TLS handshake:"
echo | openssl s_client -connect dev-de-mn.mqtt.step.vodafone.com:8883 -servername dev-de-mn.mqtt.step.vodafone.com 2>&1 | grep -E "(Verify return code|SSL-Session)"
echo ""

echo "5. Java TLS information:"
java -version 2>&1 | head -3
echo ""
echo "Java cacerts location:"
find $JAVA_HOME -name cacerts 2>/dev/null | head -1
echo ""

echo "=== Instructions ==="
echo "Run the application with: ./run.sh"
echo ""
echo "Look for in the output:"
echo "  - SSL handshake logs (javax.net.debug output)"
echo "  - Paho MQTT client connection attempts"
echo "  - Certificate validation errors"
echo "  - PKIX path building errors (indicates trust issues)"
echo ""
