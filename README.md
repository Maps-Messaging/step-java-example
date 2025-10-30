# V2X STEP Java Application

A standalone Java console application demonstrating V2X event send/receive using the Vodafone STEP Java SDK (com.vodafone.v2xsdk4javav2.facade).

## Overview

This application provides a complete working example of V2X communication using the Vodafone STEP platform. It demonstrates:

- Connecting to the STEP platform using the Vodafone V2X SDK
- Broadcasting CAM (Cooperative Awareness Messages) with vehicle position and status
- Receiving CAM messages from nearby vehicles
- Sending DENM (Decentralized Environmental Notification Messages) for hazardous events
- Receiving DENM messages from other road users
- Proper SDK lifecycle management and graceful shutdown

**Inspired by:** [HelloV2XWorld-Android](https://github.com/Vodafone/HelloV2XWorld-Android)

## Prerequisites

- **Java 21 or later** - The project targets Java 21
- **Maven 3.6+** - For building the project
- **Vodafone STEP credentials** - ApplicationID and ApplicationToken from the [STEP portal](https://developer.vodafone.com/step)
- **V2X SDK jar file** - v2xsdk4java-3.1.0.jar (to be placed in `lib/` directory)

### Checking Java Version

```bash
java -version
```

Expected output should show Java 21 or higher.

### Checking Maven Version

```bash
mvn -version
```

## Project Structure

```
step-java-example/
├── pom.xml                                      # Maven build configuration
├── README.md                                    # This file
├── doc/
│   └── Java V2X SDK - User Guide 3.1.0.pdf     # SDK documentation
├── lib/
│   └── v2xsdk4java-3.1.0.jar                   # Place SDK jar here
├── src/main/
│   ├── java/com/vodafone/v2x/example/
│   │   ├── V2XApplication.java                 # Main application class
│   │   ├── config/
│   │   │   └── AppConfig.java                  # Configuration holder
│   │   ├── location/
│   │   │   └── FakeLocationProvider.java       # Test location provider (Annex 10.3)
│   │   └── handlers/
│   │       ├── CAMHandler.java                 # CAM event handler
│   │       └── DENMHandler.java                # DENM event handler
│   └── resources/
│       ├── application.properties              # App credentials & config
│       └── logback.xml                         # Logging configuration
└── target/                                      # Build output (generated)
```

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd step-java-example
```

### 2. Place the SDK Jar

Copy the Vodafone V2X SDK jar file to the `lib/` directory:

```bash
cp /path/to/v2xsdk4java-3.1.0.jar lib/
```

**Important:** The jar file must be named exactly `v2xsdk4java-3.1.0.jar`

### 3. Configure STEP Credentials

Edit `src/main/resources/application.properties` and add your STEP credentials:

```properties
# STEP Platform Credentials (obtain from STEP portal)
app.id=YOUR_APPLICATION_ID_HERE
app.token=YOUR_APPLICATION_TOKEN_HERE

# Test Location (Paris coordinates)
test.latitude=48.866667
test.longitude=2.333333
```

To obtain credentials:
1. Register at the [Vodafone STEP Developer Portal](https://developer.vodafone.com/step)
2. Create a new application
3. Copy the Application ID and Application Token

### 4. Build the Application

```bash
mvn clean package
```

This will:
1. Compile the Java source files
2. Create a fat JAR at `target/step-java-example.jar` that includes all dependencies (including the system-scoped SDK jar)

**Important:** The Maven Shade Plugin is configured with `<includeSystemScope>true</includeSystemScope>` to ensure the V2X SDK jar is bundled into the executable JAR.

## Running the Application

### Option 1: Using the run script (recommended)

```bash
./run.sh
```

This script:
- Verifies SDK jar and configuration files exist
- Builds the application if needed
- Runs the fat JAR with all dependencies included

### Option 2: Direct JAR execution

```bash
java -jar target/step-java-example.jar
```

**Note:** Ensure you've run `mvn clean package` first to create the fat JAR.

### Option 3: Using Maven Exec Plugin

```bash
mvn exec:java
```

The exec plugin is configured to include the system-scoped SDK dependency automatically.

### Option 4: Run with explicit classpath (alternative)

```bash
./run-with-classpath.sh
```

This alternative approach:
- Builds the application without bundling the SDK
- Runs with both JARs in the classpath: `-cp target/step-java-example.jar:lib/v2xsdk4java-3.1.0.jar`
- Keeps JARs separate but requires more complex run configuration

## Expected Output

When you run the application, you should see output similar to:

```
=== V2X STEP Java Application ===
Vodafone V2X SDK Demo - CAM and DENM Example

Step 1: Loading configuration...
  Application ID: your-app-id
  Test Location: (48.866667, 2.333333)

Step 2: Initializing location provider...
  Location provider initialized

Step 3: Configuring V2X SDK...
  SDK configuration created
  - Station Type: PASSENGERCAR
  - CAM Service Mode: TxAndRx
  - DENM Service Mode: TxAndRx

Step 4: Creating V2X SDK instance...
  V2X SDK instance created

Step 5: Starting V2X service...
  V2X service start initiated

Step 6: Waiting for V2X service initialization...
  V2X service is UP AND RUNNING

Step 7: Waiting for STEP connectivity...
  Connected to STEP platform

Step 8: Subscribing to SDK events...
  Event handlers registered
  - CAM list change events
  - DENM list change events

Step 9: Starting CAM service...
  CAM service started - broadcasting vehicle presence

Step 10: Starting DENM service...
  DENM service started

Step 11: Sending test DENM...
  DENM triggered with sequence number: 12345
  Event Type: ACCIDENT
  Location: (48.866667, 2.333333)

Step 12: Application running - receiving V2X messages...
  Press Ctrl+C to stop
  Monitoring period: 60 seconds

Received 3 CAM messages
  CAM from StationID: 123, Position: (48.85, 2.34), Speed: 50.0 km/h
  CAM from StationID: 456, Position: (48.87, 2.35), Speed: 60.0 km/h
  ...

Received 1 DENM messages
  DENM from StationID: 789, CauseCode: 6, SubCauseCode: 1

Step 13: Terminating DENM...
  DENM terminated

Step 14: Performing cleanup...
  Stopping CAM service...
  Stopping DENM service...
  Stopping V2X service...
  All services stopped
  Cleanup completed

=== Application completed successfully ===
```

## What It Does

### SDK Initialization (Sections 8.3.1-8.3.6)
1. Loads configuration from `application.properties`
2. Creates a fake location provider for testing (Annex 10.3)
3. Configures the SDK with STEP credentials and service modes
4. Creates and starts the V2XSDK instance
5. Waits for service initialization and STEP platform connectivity

### CAM Service (Section 8.3.8)
- Broadcasts CAM messages periodically (1 Hz default)
- Contains vehicle position, speed, heading, and timestamp
- Receives CAM messages from other vehicles via `CAMHandler`
- Standard ETSI TS 103 900

### DENM Service (Section 8.3.9)
- Triggers event-based DENM messages for hazardous situations
- Test DENM sent with type "ACCIDENT" at configured location
- Receives DENM messages from other road users via `DENMHandler`
- Standard ETSI TS 103 831

### Graceful Shutdown (Section 8.3.17)
- Terminates active DENM messages
- Stops CAM and DENM services
- Stops V2X service
- Cleans up location provider

## Configuration

### Application Properties

Edit `src/main/resources/application.properties`:

```properties
# STEP credentials
app.id=your-application-id
app.token=your-application-token

# Test location (latitude, longitude)
test.latitude=48.866667
test.longitude=2.333333
```

### Logging Configuration

Edit `src/main/resources/logback.xml` to adjust log levels:

```xml
<logger name="com.vodafone.v2x" level="DEBUG"/>
<logger name="com.vodafone.v2xsdk4javav2" level="DEBUG"/>
```

Available levels: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`

## Extending the Application

### Adding More Message Types

The SDK supports additional V2X message types:

- **IVIM** (Infrastructure to Vehicle Information Message)
- **SPATEM** (Signal Phase and Timing Extended Message)
- **MAPEM** (MAP Extended Message)
- **VAM** (Vulnerable Road User Awareness Message)
- **CPM** (Collective Perception Message)
- **POIM** (Point of Interest Message)

To add support:
1. Create a new handler implementing `EventListener`
2. Subscribe to the appropriate `EventType`
3. Start the corresponding service (e.g., `sdk.startIVIMService()`)

### Custom Event Handlers

Create custom handlers by implementing the `EventListener` interface:

```java
public class CustomHandler implements EventListener {
    @Override
    public void onMessageBusEvent(BaseEvent baseEvent) {
        // Handle event
    }
}
```

## Implementation Details

### FakeLocationProvider (Annex 10.3)

The location provider extends `LocationProvider` and implements `Runnable`:
- Provides fixed test coordinates at 1-second intervals
- Thread-safe start/stop with `turnOn()` and `turnOff()`
- Based on SDK documentation Annex 10.3 (FakeGNSSReceiver)

### SDK Configuration (Section 8.3.1)

Uses the builder pattern:
```java
SDKConfiguration config = SDKConfiguration.builder()
    .applicationID(appId)
    .applicationToken(token)
    .stationType(StationType.PASSENGERCAR)
    .vehicleRole(VehicleRole.DEFAULT)
    .camServiceMode(ServiceMode.TxAndRx)
    .denmServiceMode(ServiceMode.TxAndRx)
    .build();
```

### Event Subscription (Section 8.3.7)

Subscribe to SDK events:
```java
sdk.subscribe(camHandler, EventType.CAMLISTCHANGED);
sdk.subscribe(denmHandler, EventType.DENMLISTCHANGED);
```

### DENM Triggering (Section 8.3.9)

Trigger and terminate DENM events:
```java
// Trigger
long seqNum = sdk.denmTrigger(DENMType.ACCIDENT, location);

// Later terminate
sdk.denmTerminate(seqNum);
```

## Troubleshooting

### Issue: NoClassDefFoundError at runtime

**Error:**
```
Caused by: java.lang.NoClassDefFoundError: com/vodafone/v2xsdk4javav2/facade/locationprovider/LocationProvider
```

**Cause:** The V2X SDK jar is not included in the runtime classpath or packaged into the executable jar.

**Solution:**
1. Ensure `lib/v2xsdk4java-3.1.0.jar` exists
2. Rebuild with `mvn clean package` to create the fat JAR
3. Verify the Maven Shade Plugin in `pom.xml` has `<includeSystemScope>true</includeSystemScope>`
4. Run with `java -jar target/step-java-example.jar` (not the original jar without dependencies)

**Alternative Solution:**
Use the explicit classpath approach:
```bash
./run-with-classpath.sh
```
or manually:
```bash
java -cp target/step-java-example.jar:lib/v2xsdk4java-3.1.0.jar com.vodafone.v2x.example.V2XApplication
```

### Issue: SDK jar not found during build

**Error:**
```
Cannot resolve system-scoped dependency: com.vodafone:v2xsdk4java:jar:3.1.0
```

**Solution:**
Ensure the SDK jar is placed at `lib/v2xsdk4java-3.1.0.jar` with the exact filename.

### Issue: Missing credentials

**Error:**
```
Unable to find application.properties
```

**Solution:**
Create `src/main/resources/application.properties` and add your STEP credentials.

### Issue: Service initialization timeout

**Error:**
```
Service initialization timeout
```

**Solution:**
- Check network connectivity
- Verify STEP credentials are correct
- Ensure STEP platform is accessible
- Check firewall settings

### Issue: Java version mismatch

**Error:**
```
Unsupported class file major version
```

**Solution:**
Install Java 21 or update the `maven.compiler.release` property in `pom.xml`.

## SDK Documentation

For detailed SDK information, refer to:
- `doc/Java V2X SDK - User Guide 3.1.0.pdf` - Complete SDK documentation
- Section 8.3: SDK initialization and lifecycle
- Annex 10.3: Location provider implementation

## References

- [Vodafone STEP Platform](https://developer.vodafone.com/step)
- [HelloV2XWorld-Android Example](https://github.com/Vodafone/HelloV2XWorld-Android)
- [ETSI ITS Standards](https://www.etsi.org/technologies/intelligent-transport)
  - CAM: ETSI TS 103 900
  - DENM: ETSI TS 103 831
- [V2X Communication Overview](https://en.wikipedia.org/wiki/Vehicle-to-everything)

## License

Please refer to the Vodafone STEP SDK license terms for usage restrictions and requirements.

## Support

For issues related to:
- **This example application**: Open an issue in this repository
- **Vodafone STEP SDK**: Contact Vodafone STEP support
- **V2X standards**: Refer to ETSI ITS documentation

---

**Version:** 1.0.0  
**Last Updated:** 2024  
**SDK Version:** 3.1.0  
**Java Version:** 21
