# Contributing to V2X STEP Java Application

Thank you for your interest in contributing to the V2X STEP Java Application!

## Getting Started

1. **Fork and Clone**
   ```bash
   git clone <your-fork-url>
   cd step-java-example
   ```

2. **Install Prerequisites**
   - Java 21 or higher
   - Maven 3.6+
   - Vodafone V2X SDK jar file (v2xsdk4java-3.1.0.jar)

3. **Place SDK**
   - Copy `v2xsdk4java-3.1.0.jar` to the `lib/` directory

4. **Configure**
   - Create `src/main/resources/application.properties`
   - Add your STEP credentials

5. **Build**
   ```bash
   mvn clean package
   ```

## Development Guidelines

### Code Style

- Follow standard Java naming conventions
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Add Javadoc comments for all public classes and methods
- Use clear, descriptive variable names
- Follow existing patterns in the codebase

### Adding New Features

When adding new V2X message types or features:

1. **Handler Classes**
   - Create new handler in `handlers/` package
   - Implement `EventListener` interface
   - Follow the pattern of `CAMHandler` or `DENMHandler`

2. **Event Subscription**
   - Subscribe to appropriate `EventType` in main application
   - Handle events in `onMessageBusEvent()` method

3. **Service Lifecycle**
   - Start service in the application flow (e.g., `sdk.startIVIMService()`)
   - Stop service during cleanup

4. **Documentation**
   - Update README.md with usage examples
   - Add comments referencing SDK user guide sections
   - Document ETSI standards if applicable

### Supported V2X Message Types

The SDK supports the following message types:

- **CAM** - Cooperative Awareness Message (ETSI TS 103 900)
- **DENM** - Decentralized Environmental Notification Message (ETSI TS 103 831)
- **IVIM** - Infrastructure to Vehicle Information Message
- **SPATEM** - Signal Phase and Timing Extended Message
- **MAPEM** - MAP Extended Message
- **VAM** - Vulnerable Road User Awareness Message
- **CPM** - Collective Perception Message
- **POIM** - Point of Interest Message

### Error Handling

- Always use try-catch blocks for V2X operations
- Log errors with appropriate context using SLF4J
- Handle SDK exceptions appropriately
- Never silently catch and ignore exceptions
- Ensure proper cleanup in finally blocks or shutdown hooks

### Logging

Use SLF4J with Logback for all logging:

```java
private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

logger.debug("Detailed debug information");
logger.info("Important events");
logger.warn("Warning conditions");
logger.error("Error conditions", exception);
```

Configure logging in `src/main/resources/logback.xml`.

## Project Structure

```
step-java-example/
├── src/main/java/com/vodafone/v2x/example/
│   ├── V2XApplication.java              # Main application class
│   ├── config/
│   │   └── AppConfig.java               # Configuration management
│   ├── location/
│   │   └── FakeLocationProvider.java    # Location provider (Annex 10.3)
│   └── handlers/
│       ├── CAMHandler.java              # CAM message handler
│       └── DENMHandler.java             # DENM message handler
├── src/main/resources/
│   ├── application.properties           # STEP credentials (excluded from git)
│   └── logback.xml                      # Logging configuration
├── lib/
│   └── v2xsdk4java-3.1.0.jar           # V2X SDK (excluded from git)
├── doc/
│   └── Java V2X SDK - User Guide 3.1.0.pdf
├── pom.xml                              # Maven configuration
└── README.md                            # Project documentation
```

## SDK Integration Guidelines

### SDK References

Always reference SDK documentation sections in comments:

```java
// Section 8.3.8 - Starting CAM Service
sdk.startCAMService();
```

### Configuration Pattern

Use the builder pattern for SDK configuration:

```java
SDKConfiguration config = SDKConfiguration.builder()
    .applicationID(appId)
    .applicationToken(token)
    .stationType(StationType.PASSENGERCAR)
    .vehicleRole(VehicleRole.DEFAULT)
    .camServiceMode(ServiceMode.TxAndRx)
    .build();
```

### Event Handling

Implement `EventListener` for handling V2X events:

```java
public class CustomHandler implements EventListener {
    @Override
    public void onMessageBusEvent(BaseEvent baseEvent) {
        if (baseEvent.getEventType() == EventType.CUSTOMTYPE) {
            // Handle event
        }
    }
}
```

## Common Tasks

### Adding a New Message Type Handler

1. Create handler class:
   ```java
   package com.vodafone.v2x.example.handlers;
   
   import com.vodafone.v2xsdk4javav2.facade.events.*;
   
   public class IVIMHandler implements EventListener {
       @Override
       public void onMessageBusEvent(BaseEvent baseEvent) {
           if (baseEvent.getEventType() == EventType.IVIMLISTCHANGED) {
               // Handle IVIM events
           }
       }
   }
   ```

2. Register in main application:
   ```java
   IVIMHandler ivimHandler = new IVIMHandler();
   sdk.subscribe(ivimHandler, EventType.IVIMLISTCHANGED);
   sdk.startIVIMService();
   ```

3. Stop service during cleanup:
   ```java
   sdk.stopIVIMService();
   ```

### Changing Log Levels

Edit `src/main/resources/logback.xml`:

```xml
<logger name="com.vodafone.v2x" level="DEBUG"/>
<logger name="com.vodafone.v2xsdk4javav2" level="DEBUG"/>
```

### Building Without Tests

```bash
mvn clean package -DskipTests
```

### Testing with Different Credentials

Create alternate properties files:

```bash
cp src/main/resources/application.properties config/test.properties
# Edit test.properties with different credentials
```

Then modify code to load from different location if needed.

## Submitting Changes

1. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Write clean, documented code
   - Follow the coding guidelines
   - Reference SDK documentation sections
   - Test your changes

3. **Commit**
   ```bash
   git add .
   git commit -m "Add feature: description"
   ```

4. **Push**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create Pull Request**
   - Provide a clear description
   - Reference SDK sections if applicable
   - Reference any related issues
   - Include test results

## Code Review Process

All contributions go through code review:

- Code follows style guidelines
- Changes are well-documented
- SDK references are included where appropriate
- No breaking changes without discussion
- V2X-specific concepts are clearly explained

## Configuration Management

### Credentials

Never commit credentials to version control:

- `application.properties` is excluded in `.gitignore`
- Provide example configuration in documentation
- Use environment variables for CI/CD if needed

### SDK Jar

The SDK jar is excluded from version control:

- Users must obtain it from Vodafone
- Document the required version clearly
- Keep `lib/README.md` updated

## Questions?

If you have questions:

- Check the README.md
- Review the SDK User Guide in `doc/` folder
- Review existing code examples
- Check the HelloV2XWorld-Android reference
- Open an issue for discussion
- Contact the maintainers

## V2X Resources

Helpful resources for V2X development:

- [ETSI ITS Standards](https://www.etsi.org/technologies/intelligent-transport)
  - CAM: ETSI TS 103 900
  - DENM: ETSI TS 103 831
- [Vodafone STEP Platform](https://developer.vodafone.com/step)
- [HelloV2XWorld-Android](https://github.com/Vodafone/HelloV2XWorld-Android)
- [V2X Communication Overview](https://en.wikipedia.org/wiki/Vehicle-to-everything)

## Reference Documentation

The SDK User Guide (in `doc/` folder) is your primary reference:

- Section 8.3.1: SDK Configuration
- Section 8.3.3: Creating SDK Instance
- Section 8.3.4: Starting V2X Service
- Section 8.3.5: Waiting for Initialization
- Section 8.3.6: Waiting for Connectivity
- Section 8.3.7: Event Subscription
- Section 8.3.8: CAM Service
- Section 8.3.9: DENM Service
- Section 8.3.17: Shutdown
- Annex 10.3: Location Provider Implementation

Thank you for contributing!
