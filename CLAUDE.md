# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

V2X STEP Java Application - A standalone Java console application demonstrating V2X (Vehicle-to-Everything) event send/receive using the Vodafone STEP Java SDK. This is an example implementation inspired by [HelloV2XWorld-Android](https://github.com/Vodafone/HelloV2XWorld-Android).

## Build and Run Commands

### Build
```bash
# Clean build (creates fat JAR with all dependencies)
mvn clean package

# The output is target/step-java-example.jar (shaded JAR with SDK included)
```

### Run
```bash
# Recommended: Use the run script
./run.sh

# Direct JAR execution (after build)
java -jar target/step-java-example.jar

# Using Maven exec plugin
mvn exec:java

# Alternative: Run with explicit classpath
./run-with-classpath.sh
```

### Test
This is a demo application without formal tests. Run the application to verify functionality.

## Critical Dependencies

### System-Scoped V2X SDK
- The Vodafone V2X SDK jar (`v2xsdk4java-3.1.0.jar`) MUST be placed in the `lib/` directory
- It is configured as a system-scoped dependency in pom.xml (lines 26-33)
- The Maven Shade Plugin is configured with `<includeSystemScope>true</includeSystemScope>` (line 108) to bundle the SDK into the executable JAR
- Without this SDK jar, the build will fail

### Credentials Configuration
- STEP credentials are required in `src/main/resources/application.properties`
- Properties needed: `app.id`, `app.token`, `test.latitude`, `test.longitude`, `service.cam.enabled`, `service.denm.enabled`, `debug.mode`
- This file is gitignored; use `application.properties.example` as a template
- Credentials obtained from: https://developer.vodafone.com/step

### Service Configuration
- `service.cam.enabled`: Enable/disable CAM (Cooperative Awareness Messages) - default: `false`
- `service.denm.enabled`: Enable/disable DENM (Decentralized Environmental Notification Messages) - default: `true`
- Services can be enabled individually or together
- MQTT connection establishes when at least one service is started
- Only enabled services are configured in SDKConfiguration and started

### Debug Mode
- Set `debug.mode=true` in `application.properties` for detailed logging
- Debug mode enables:
  - TLS/SSL handshake logging (`javax.net.debug`)
  - Paho MQTT client verbose logging
  - SDK log level set to DEBUG (instead of INFO)
- Default: `debug.mode=false` (INFO level logging)
- Use debug mode for troubleshooting MQTT connection issues

## Architecture

### Main Application Flow (V2XApplication.java)
The application follows a 14-step initialization and execution sequence based on the Vodafone SDK User Guide (Section 8.3):

1. Load configuration from properties file
2. Initialize FakeLocationProvider with test coordinates (Annex 10.3)
3. Configure SDK with STEP credentials and enabled service modes (conditional based on config)
4. Create V2XSDK instance
5. Start V2X service
6. Wait for service to be UP_AND_RUNNING (with 10-second timeout)
7. Check initial connectivity state (note: connection happens when services start)
8. Subscribe event handlers (only for enabled services: EventType.CAM_LIST_CHANGED and/or EventType.DENM_LIST_CHANGED)
9. Start CAM service if enabled (broadcasts vehicle presence at 1 Hz)
10. Start DENM service if enabled
10.5. Wait for MQTT CONNECTED state (15-second timeout after services start)
11. Trigger test DENM if DENM service enabled (ACCIDENT_UNSECUREDACCIDENT type)
12. Run for 15 seconds receiving messages
13. Terminate DENM if triggered
14. Graceful shutdown (stop enabled services, cleanup location provider)

### Key Components

**config/AppConfig.java**
- Loads application.properties from classpath
- Provides STEP credentials and test location coordinates
- Throws IOException if properties file not found

**location/FakeLocationProvider.java**
- Extends SDK's LocationProvider and implements Runnable
- Provides fixed test coordinates at 1-second intervals via notifyFreshLocation()
- Thread-safe start/stop with turnOn()/turnOff()
- Based on SDK Annex 10.3 (FakeGNSSReceiver pattern)

**handlers/CAMHandler.java**
- Implements EventListener for Cooperative Awareness Messages (ETSI TS 103 900)
- Handles EventType.CAM_LIST_CHANGED events
- Detects own station ID from first received message
- Tags messages as [OWN] or [OTHER] based on station ID
- Logs received CAM messages with station ID, position, speed, and heading

**handlers/DENMHandler.java**
- Implements EventListener for Decentralized Environmental Notification Messages (ETSI TS 103 831)
- Handles EventType.DENM_LIST_CHANGED events
- Detects own station ID from first received message
- Tags messages as [OWN] or [OTHER] based on originator ID
- Logs received DENM messages with originator ID, sequence number, cause code, and sub-cause code

### V2X Message Types
Currently implemented: CAM, DENM

The SDK also supports: IVIM, SPATEM, MAPEM, VAM, CPM, POIM

To add a new message type:
1. Create handler in `handlers/` implementing EventListener
2. Subscribe to appropriate EventType in V2XApplication
3. Start corresponding service (e.g., `sdk.startIVIMService()`)
4. Stop service in shutdown()

### Understanding Message Repetition and MQTT Echo

**IMPORTANT: You will receive multiple copies of your own messages - this is NORMAL behavior.**

#### Why You See Multiple Messages

1. **ETSI Standard Repetition (Safety-Critical Reliability)**
   - **CAM**: Broadcasts continuously at **1 Hz (every 1 second)** while service is running
     - Purpose: Periodic vehicle presence heartbeat ("I'm here")
     - Use case: Collision avoidance, traffic awareness
     - Duration: Continuous until CAM service stopped

   - **DENM**: Repeats at **2 Hz (every 500ms)** during event validity period
     - Purpose: Safety-critical hazard warnings ("Danger ahead!")
     - Use case: Accident notifications, road hazards
     - Duration: Typically 40 seconds per triggered event

2. **MQTT Echo (Loopback Messages)**
   - The application subscribes to MQTT topics to receive messages from other stations
   - When you publish a message, MQTT delivers it to ALL subscribers, including yourself
   - This is why you see `[OWN]` messages - they're your own transmissions echoed back
   - If other vehicles were in the same group, you'd see `[OTHER]` messages from them

#### Expected Message Patterns

**CAM Service (when enabled):**
```
15:35:27.183  [OWN] CAM - StationID: 2455284956
15:35:28.169  [OWN] CAM - StationID: 2455284956  (+986ms ~ 1 second)
15:35:29.154  [OWN] CAM - StationID: 2455284956  (+985ms ~ 1 second)
```
✅ One CAM message per second = Expected behavior (ETSI TS 103 900)

**DENM Service (when triggered):**
```
15:19:59.864  [OWN] DENM - StationID: 2147090446, SeqNum: 1
15:20:00.151  [OWN] DENM - StationID: 2147090446, SeqNum: 1  (+287ms)
15:20:00.638  [OWN] DENM - StationID: 2147090446, SeqNum: 1  (+487ms)
```
✅ Multiple DENM messages with same SeqNum at ~500ms intervals = Expected behavior (ETSI TS 103 831)

#### Not a Bug: Common Misconceptions

❌ **"I sent 1 DENM but received many"** - Intentional repetition for reliability
❌ **"Messages are looping back"** - MQTT echo is normal (subscribe to same topic you publish)
❌ **"There's a retry/failure"** - No failure, just safety-critical repetition

✅ **This is correct ETSI V2X standard behavior for automotive safety**

### SDK Configuration Pattern
Uses builder pattern (SDKConfiguration.builder()) with:
- stepInstance: StepInstance.DE_DEV_FRANKFURT (development environment)
- applicationID/applicationToken: STEP credentials
- stationType: StationType.PASSENGER_CAR
- serviceMode: ServiceMode.TxAndRx (transmit and receive)
- mqttClientID: Unique client identifier
- Service-specific groups (e.g., denmPublishGroup, denmSubscribeGroup)

### Service Lifecycle
All SDK operations must follow strict order:
1. Create SDK with location provider and configuration
2. Start V2X service
3. Wait for UP_AND_RUNNING state
4. Wait for CONNECTED state
5. Subscribe handlers
6. Start individual services (CAM, DENM, etc.)
7. Perform operations (trigger DENM, etc.)
8. Terminate events (DENM terminate)
9. Stop services in reverse order
10. Stop V2X service

## Common Development Patterns

### SDK References in Code
Always include SDK User Guide section references in comments:
```java
// Section 8.3.8 - Starting CAM Service
sdk.startCAMService();
```

### Error Handling
- All V2X operations should be wrapped in try-catch
- Use SLF4J logger for all logging
- Ensure cleanup in shutdown() even on errors
- Never silently catch exceptions

### Logging Configuration
- Default log level: INFO (clean output for production use)
- Debug mode: Set `debug.mode=true` in `application.properties` for DEBUG level
- Edit `src/main/resources/logback.xml` to adjust static log levels
- SDK log level set programmatically based on debug mode:
  - `debug.mode=false`: `sdk.setSDKLogLevel(LogLevel.INFO)`
  - `debug.mode=true`: `sdk.setSDKLogLevel(LogLevel.DEBUG)`
- Application uses SLF4J with Logback implementation
- Logger levels: TRACE, DEBUG, INFO, WARN, ERROR

### DENM Triggering Pattern
```java
// Create location for event
GnssLocation eventLocation = new GnssLocation(lat, lon, null, null, null, null, timestamp);

// Trigger DENM - returns sequence number
long seqNum = sdk.denmTrigger(DENMType.ACCIDENT_UNSECUREDACCIDENT, eventLocation);

// Later terminate using sequence number
sdk.denmTerminate(seqNum);
```

## Troubleshooting

### NoClassDefFoundError at runtime
- Cause: V2X SDK jar not included in runtime classpath
- Solution: Run `mvn clean package` to create the fat JAR with SDK bundled
- Verify Maven Shade Plugin has `<includeSystemScope>true</includeSystemScope>`
- Use the shaded JAR: `target/step-java-example.jar`

### Build fails: Cannot resolve system-scoped dependency
- Cause: SDK jar not in lib/ directory
- Solution: Place `v2xsdk4java-3.1.0.jar` in `lib/` with exact filename

### Service initialization timeout
- Cause: Network issues or invalid credentials
- Check: Network connectivity, STEP credentials, firewall settings
- Timeout: 30 seconds for service, 10 seconds for connectivity

### MQTT connection timeout (CRITICAL FIX)
- **Problem**: Waiting for CONNECTED state before starting CAM/DENM services causes timeout
- **Root cause**: SDK only establishes MQTT connection AFTER services are started, not during V2X service initialization
- **Solution**: Start CAM/DENM services first (Steps 9-10), THEN wait for MQTT CONNECTED state (Step 10.5)
- **Code pattern**:
  ```java
  // WRONG: Wait for connection before starting services
  while (sdk.getV2XConnectivityState() != CONNECTED) { /* timeout */ }
  sdk.startCAMService();

  // CORRECT: Start services, then wait for connection
  sdk.startCAMService();
  while (sdk.getV2XConnectivityState() != CONNECTED) { /* connects */ }
  ```

### Receiving many messages from same station
- **Not a bug**: ETSI standards require message repetition (see "Understanding Message Repetition" section above)
- CAM: 1 Hz heartbeat (continuous)
- DENM: 2 Hz repetition (40 seconds per event)
- [OWN] tag indicates your own messages via MQTT echo

### Missing application.properties
- Cause: Configuration file not created
- Solution: Copy `application.properties.example` and add STEP credentials

## Important Constraints

- Requires Java 21 or later (configured in pom.xml)
- SDK jar must be manually placed (not in Maven Central)
- STEP credentials required from Vodafone developer portal
- Application runs for fixed 5-second demo period (configurable in V2XApplication.java:235)
- Uses fixed test coordinates from FakeLocationProvider
- DEV STEP instance only (DE_DEV_FRANKFURT)
- MQTT connection only establishes AFTER CAM/DENM services are started (not during V2X service init)

## Session Context Summary

This codebase has been enhanced with the following features during development:

1. **Service Configuration** (`service.cam.enabled`, `service.denm.enabled`)
   - Default: DENM only (CAM disabled, DENM enabled)
   - Services can run independently or together
   - See SERVICE_CONFIGURATION.md for details

2. **Debug Mode** (`debug.mode`)
   - Default: false (INFO level logging)
   - When true: Enables TLS/MQTT debug logs and SDK DEBUG level
   - See DEBUG_MODE.md for details

3. **[OWN]/[OTHER] Message Tagging**
   - CAMHandler and DENMHandler detect own station ID from first message
   - Messages tagged as [OWN] (your own via MQTT echo) or [OTHER] (from other stations)
   - Helps distinguish between your transmissions and messages from other vehicles

4. **MQTT Connection Fix**
   - Corrected service startup sequence: services must start BEFORE waiting for MQTT connection
   - Step 10.5 added: Wait for CONNECTED state after services are started
   - This resolves the "connection timeout" issue

5. **Message Repetition Understanding**
   - CAM: 1 Hz continuous broadcast (vehicle presence heartbeat)
   - DENM: 2 Hz repetition for 40 seconds (safety-critical event warnings)
   - Multiple messages are expected behavior per ETSI standards, not a bug
