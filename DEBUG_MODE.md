# Debug Mode Usage Guide

## Overview

The application supports two logging modes via the `debug.mode` setting in `application.properties`:

- **INFO Mode** (default): Clean, production-friendly output showing only important events
- **DEBUG Mode**: Verbose output with TLS handshakes, MQTT packets, and SDK internals

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# For normal operation (default)
debug.mode=false

# For troubleshooting connection issues
debug.mode=true
```

## What's Different in Debug Mode?

### Normal Mode (debug.mode=false)

**Output includes:**
- Application startup steps
- Configuration summary
- Service state transitions (UP_AND_RUNNING, CONNECTED)
- CAM/DENM message counts
- Clean shutdown logs

**Example output:**
```
14:42:30.123 [main] INFO  c.v.v2x.example.V2XApplication - === V2X STEP Java Application ===
14:42:30.124 [main] INFO  c.v.v2x.example.V2XApplication - Step 1: Loading configuration...
14:42:30.125 [main] INFO  c.v.v2x.example.V2XApplication -   Application ID: b9027371-deeb-4922-a949-7ec5d2a954e0
14:42:30.126 [main] INFO  c.v.v2x.example.V2XApplication -   Debug Mode: false
14:42:32.456 [main] INFO  c.v.v2x.example.V2XApplication -   ✓ Connected to STEP MQTT platform!
14:42:37.698 [Thread-0] INFO  c.v.v2x.example.handlers.CAMHandler - Received 1 CAM messages
14:42:37.699 [Thread-0] INFO  c.v.v2x.example.handlers.CAMHandler -   CAM from StationID: 2211072035
```

### Debug Mode (debug.mode=true)

**Additional output includes:**
- TLS/SSL handshake details (certificates, cipher suites)
- MQTT connection attempts and subscriptions
- Binary message payloads (CAM/DENM packets)
- Internal SDK state machines
- Geohash zone calculations
- Network I/O operations

**Example output (additional debug info shown):**
```
14:42:30.126 [main] INFO  c.v.v2x.example.V2XApplication -   Debug Mode: true
14:42:30.127 [main] INFO  c.v.v2x.example.V2XApplication -   Enabling TLS and MQTT debug logging...
14:42:30.128 [main] INFO  c.v.v2x.example.V2XApplication -   Note: Watch for SSL/TLS handshake logs below

javax.net.ssl|DEBUG|MQTT Conn|2025-11-19 14:42:32.456 EET|SSLSocketImpl.java:234|Handshake started
javax.net.ssl|DEBUG|MQTT Conn|2025-11-19 14:42:32.457 EET|SSLCipher.java:123|Negotiated cipher: TLS_AES_256_GCM_SHA384

2025-11-19 12:42:32.458 [DEBUG] PahoMqttClient1           connect() -> dev-de-mn.mqtt.step.vodafone.com:8883
2025-11-19 12:42:32.459 [DEBUG] MqttService1              updateConnectivityState() -> CONNECTED
2025-11-19 12:42:36.683 [DEBUG] MqttService1              onMessage() ,topic=v2x/cam/926696_216/g8/u/0/9/t/v/p/g/h
2025-11-19 12:42:36.684 [DEBUG] CAMService1               CAM-Signature = 020283CA4823C051405A58ABE5...
2025-11-19 12:42:36.685 [DEBUG] CAMService1               camRecord: {"generationUTCTime":1763556156617,"stationID":2211072035...}
```

## When to Use Debug Mode

### Use debug.mode=true when:
- ✓ Troubleshooting MQTT connection failures
- ✓ Diagnosing TLS certificate issues
- ✓ Verifying message payloads are correct
- ✓ Understanding SDK state machine behavior
- ✓ Investigating timing or synchronization issues
- ✓ Analyzing network connectivity problems

### Use debug.mode=false when:
- ✓ Running in production
- ✓ Normal development (clean logs)
- ✓ Demonstrating the application
- ✓ Performance testing (less I/O overhead)
- ✓ Log file size is a concern

## Performance Impact

Debug mode generates significantly more log output:
- **Normal mode**: ~50 lines/minute
- **Debug mode**: ~500-1000 lines/minute (10-20x more)

The performance impact is minimal (< 1%), but log files grow much faster.

## Common Debug Scenarios

### Scenario 1: Connection Timeout

**Problem:** Application times out waiting for MQTT connection

**Debug steps:**
1. Set `debug.mode=true`
2. Run application: `./run.sh 2>&1 | grep -E "(SSL|MQTT|Connection)"`
3. Look for:
   - SSL handshake completion
   - MQTT connect attempt
   - Authentication success/failure

### Scenario 2: No Messages Received

**Problem:** Connected but not receiving CAM/DENM messages

**Debug steps:**
1. Set `debug.mode=true`
2. Run application: `./run.sh 2>&1 | grep -E "(subscribe|topic|onMessage)"`
3. Verify:
   - Subscriptions to correct topics
   - Geohash zones match your location
   - Messages arriving but maybe filtered

### Scenario 3: Certificate Errors

**Problem:** TLS/SSL handshake failures

**Debug steps:**
1. Set `debug.mode=true`
2. Run application: `./run.sh 2>&1 | grep -E "(certificate|trustStore|PKIX)"`
3. Check:
   - Certificate chain validation
   - Trust store contents
   - Certificate expiration dates

## Reverting to Normal Mode

After troubleshooting, remember to disable debug mode:

```properties
debug.mode=false
```

Or use environment variable override (if implemented):
```bash
DEBUG_MODE=false ./run.sh
```

## Log File Management

When using debug mode, consider:

```bash
# Redirect to file with rotation
./run.sh 2>&1 | tee mqtt-debug-$(date +%Y%m%d-%H%M%S).log

# Filter for specific issues
./run.sh 2>&1 | grep -i error | tee errors.log

# Limit log size
./run.sh 2>&1 | head -1000 > debug-sample.log
```

## Tips

1. **Start with normal mode** - Only enable debug when you need it
2. **Use grep** - Filter debug logs for relevant information
3. **Compare logs** - Run with debug mode on working vs failing systems
4. **Save debug logs** - Helpful for bug reports and support requests
5. **Check timestamps** - Debug logs show exact timing of events
