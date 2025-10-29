# V2X Send/Receive Sample Application

A standalone Java console application demonstrating basic send and receive functionality for V2X (Vehicle-to-Everything) events using the Vodafone STEP Java SDK.

## Overview

This sample application provides a pure Java implementation for V2X communication, without any UI or Android OS dependencies. It demonstrates how to:

- Initialize the Vodafone STEP SDK client
- Send V2X messages (CAM, DENM, and custom events)
- Receive V2X messages from other vehicles and infrastructure
- Handle V2X events with proper lifecycle management
- Implement error handling and logging

**Inspired by:** [HelloV2XWorld-Android](https://github.com/Vodafone/HelloV2XWorld-Android)

## V2X Message Types

The application demonstrates the following V2X message types:

### CAM (Cooperative Awareness Message)
- Broadcast periodically to inform other vehicles about presence, position, speed, and status
- Typical frequency: 1-10 Hz depending on vehicle dynamics
- Contains: position (lat/lon), speed, heading, timestamp

### DENM (Decentralized Environmental Notification Message)
- Event-triggered messages for hazardous situations
- Used for: accidents, road works, weather conditions, obstacles
- Contains: event location, event type, severity level, timestamp

### Custom Events
- Application-specific V2X messages
- Flexible payload structure for custom use cases

## Prerequisites

- **Java 21 or higher** - The project targets Java 21
- **Maven 3.6+** - For building the project
- **Vodafone STEP SDK** - The SDK jar file (to be placed in `lib/` directory)

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
v2x-sample/
├── pom.xml                          # Maven project configuration
├── README.md                        # This file
├── lib/                             # SDK jar location
│   ├── README.md                    # Instructions for SDK placement
│   └── vodafone-step-sdk.jar       # Place SDK jar here
├── src/
│   └── main/
│       ├── java/
│       │   └── com/vodafone/v2x/sample/
│       │       ├── V2XSampleApp.java       # Main application
│       │       ├── V2XEventSender.java     # Sends V2X events
│       │       ├── V2XEventReceiver.java   # Receives V2X events
│       │       ├── V2XEventPayload.java    # Event data structure
│       │       ├── V2XEventListener.java   # Event listener interface
│       │       └── V2XException.java       # V2X exception class
│       └── resources/
│           └── simplelogger.properties     # Logging configuration
└── target/                          # Build output (generated)
```

## Setup Instructions

### 1. Clone or Download the Project

```bash
git clone <repository-url>
cd v2x-sample
```

### 2. Place the STEP SDK Jar

Copy the Vodafone STEP SDK jar file to the `lib/` directory:

```bash
cp /path/to/vodafone-step-sdk.jar lib/
```

**Important:** The jar file must be named `vodafone-step-sdk.jar`

### 3. Configure STEP SDK Credentials (Optional)

For production use, you may need to configure API credentials. This can be done via:

- Environment variables
- Configuration file
- Command-line arguments

Example environment variables:

```bash
export STEP_API_KEY="your-api-key"
export STEP_CLIENT_ID="your-client-id"
export STEP_API_ENDPOINT="https://api.step.vodafone.com"
```

## Building the Application

### Build with Maven

```bash
mvn clean package
```

This will:
1. Compile the Java source files
2. Run tests (if any)
3. Create a regular jar: `target/v2x-sample-1.0.0.jar`
4. Create an uber jar with dependencies: `target/v2x-sample-1.0.0-shaded.jar`

### Build Output

After a successful build, you'll find:

- `target/v2x-sample-1.0.0.jar` - Regular jar (requires classpath)
- `target/v2x-sample-1.0.0-shaded.jar` - Uber jar with all dependencies

## Running the Application

### Option 1: Run with Maven

```bash
mvn exec:java -Dexec.mainClass="com.vodafone.v2x.sample.V2XSampleApp"
```

### Option 2: Run the Uber Jar

```bash
java -jar target/v2x-sample-1.0.0-shaded.jar
```

### Option 3: Run with Classpath

```bash
java -cp target/v2x-sample-1.0.0.jar com.vodafone.v2x.sample.V2XSampleApp
```

## Expected Output

When you run the application, you should see output similar to:

```
=== V2X Send/Receive Sample Application ===
Vodafone STEP SDK - Pure Java Console Application

Step 1: Initializing STEP client...
STEP client initialized successfully

Step 2: Creating sender and receiver...
Sender and receiver created

Step 3: Connecting sender...
V2X Event Sender connected successfully

Step 4: Connecting receiver...
V2X Event Receiver connected successfully

Step 5: Setting up event listener...
Event listener registered

Step 6: Starting receiver...
V2X event listener started successfully

Step 7: Demonstrating V2X message sending...
Sending CAM (Cooperative Awareness Message)...
  Location: 51.5074° N, 0.1278° W (London)
  Speed: 13.89 m/s (50 km/h)
  Heading: 45° (Northeast)
✓ CAM sent successfully

Sending DENM (Decentralized Environmental Notification Message)...
  Location: 51.5074° N, 0.1278° W
  Event: ROAD_WORKS
  Severity: 3 (Moderate)
✓ DENM sent successfully

Step 8: Demonstrating V2X message receiving...
>>> Event Received via Listener <<<
    Type: CAM
    Latitude: 52.52
    Longitude: 13.405
    Speed: 11.11 m/s
    ...

Step 9: Cleanup and shutdown...
=== Application completed successfully ===
```

## Usage Examples

### Sending a CAM Message

```java
V2XEventSender sender = new V2XEventSender(stepClient);
sender.connect();

// Send CAM with vehicle position and dynamics
boolean sent = sender.sendCAM(
    51.5074,  // latitude
    -0.1278,  // longitude
    13.89,    // speed in m/s
    45.0      // heading in degrees
);
```

### Sending a DENM Message

```java
// Alert about road works
boolean sent = sender.sendDENM(
    51.5074,       // latitude
    -0.1278,       // longitude
    "ROAD_WORKS",  // event type
    3              // severity (1-5)
);
```

### Receiving V2X Events

```java
V2XEventReceiver receiver = new V2XEventReceiver(stepClient);
receiver.connect();

// Add event listener
receiver.addListener((eventType, payload) -> {
    System.out.println("Received: " + eventType);
    System.out.println("Location: " + payload.getLatitude() + 
                       ", " + payload.getLongitude());
});

// Start listening
receiver.startListening();
```

### Custom Event Payload

```java
V2XEventPayload payload = V2XEventPayload.builder()
    .latitude(48.8566)
    .longitude(2.3522)
    .speed(16.67)
    .heading(180.0)
    .timestamp(System.currentTimeMillis())
    .additionalData("Custom data")
    .build();

sender.sendEvent("CUSTOM", payload);
```

## Configuration

### Logging Configuration

Logging is configured via `src/main/resources/simplelogger.properties`.

To change log level:

```properties
org.slf4j.simpleLogger.defaultLogLevel=debug
```

Available levels: `trace`, `debug`, `info`, `warn`, `error`

### SDK Configuration

The SDK jar is configured in `pom.xml` as a system-scoped dependency:

```xml
<dependency>
    <groupId>com.vodafone</groupId>
    <artifactId>step-sdk</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/vodafone-step-sdk.jar</systemPath>
</dependency>
```

## Development Notes

### Current Implementation Status

The current implementation includes:
- ✅ Complete project structure
- ✅ Maven build configuration
- ✅ V2X sender and receiver classes
- ✅ Event payload and listener interfaces
- ✅ Main application with lifecycle management
- ✅ Error handling and logging
- ✅ Comprehensive documentation

**Note:** The actual STEP SDK integration points are marked with `TODO` comments in the code. When the SDK jar is available and its API is known, these sections should be updated with actual SDK method calls.

### Extending the Application

To add new V2X message types:

1. Add a new method to `V2XEventSender` (e.g., `sendIVIM()` for Infrastructure to Vehicle Information Messages)
2. Define the payload structure in `V2XEventPayload.Builder`
3. Update the event type constants if needed
4. Add corresponding handler in the receiver if needed

### Testing

To add unit tests:

```bash
mkdir -p src/test/java/com/vodafone/v2x/sample
```

Add test dependencies to `pom.xml`:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

## Troubleshooting

### Issue: SDK jar not found

**Error:**
```
Cannot resolve system-scoped dependency: com.vodafone:step-sdk:jar:1.0.0
```

**Solution:**
Ensure the SDK jar is placed at `lib/vodafone-step-sdk.jar` with the exact filename.

### Issue: Java version mismatch

**Error:**
```
Unsupported class file major version XX
```

**Solution:**
Install Java 21 or update the `maven.compiler.release` property in `pom.xml` to match your Java version.

### Issue: Port already in use

If the STEP SDK requires a specific port that's already in use, check running processes:

```bash
lsof -i :PORT_NUMBER
```

And stop conflicting services.

## References

- [Vodafone STEP Platform Documentation](https://developer.vodafone.com/step)
- [HelloV2XWorld-Android Example](https://github.com/Vodafone/HelloV2XWorld-Android)
- [ETSI ITS Standards](https://www.etsi.org/technologies/intelligent-transport)
- [V2X Communication Overview](https://en.wikipedia.org/wiki/Vehicle-to-everything)

## License

Please refer to the Vodafone STEP SDK license terms for usage restrictions and requirements.

## Support

For issues related to:
- **This sample application**: Open an issue in this repository
- **Vodafone STEP SDK**: Contact Vodafone STEP support
- **V2X standards**: Refer to ETSI ITS documentation

## Next Steps

This is a starting point for V2X development. Potential enhancements:

1. Add support for more V2X message types (IVIM, SPATEM, MAPEM)
2. Implement message validation and security
3. Add geo-fencing and proximity-based filtering
4. Create a configuration file for runtime parameters
5. Add metrics and monitoring
6. Implement message persistence
7. Add support for multiple simultaneous connections
8. Create integration tests with real V2X scenarios

## Contributing

When contributing to this sample:

1. Follow existing code style and conventions
2. Add appropriate comments for V2X-specific concepts
3. Include error handling for all V2X operations
4. Update documentation for new features
5. Test with the actual STEP SDK when available

---

**Version:** 1.0.0  
**Last Updated:** 2024  
**Maintainer:** Vodafone V2X Development Team
