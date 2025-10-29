# Contributing to V2X Sample Application

Thank you for your interest in contributing to the V2X Sample Application!

## Getting Started

1. **Fork and Clone**
   ```bash
   git clone <your-fork-url>
   cd v2x-sample
   ```

2. **Install Prerequisites**
   - Java 21 or higher
   - Maven 3.6+
   - Vodafone STEP SDK jar file

3. **Place SDK**
   - Copy `vodafone-step-sdk.jar` to the `lib/` directory

4. **Build**
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

### Adding New Features

When adding new V2X message types or features:

1. **Sender Side**
   - Add method to `V2XEventSender.java`
   - Follow the pattern of existing methods (e.g., `sendCAM()`, `sendDENM()`)
   - Include detailed Javadoc explaining the message type

2. **Receiver Side**
   - Update `V2XEventReceiver.java` if special handling is needed
   - Consider adding filtering or validation

3. **Payload**
   - Extend `V2XEventPayload.Builder` if new fields are needed
   - Document the purpose and format of new fields

4. **Documentation**
   - Update README.md with usage examples
   - Add comments explaining V2X-specific concepts

### Error Handling

- Always use try-catch blocks for V2X operations
- Log errors with appropriate context using SLF4J
- Throw `V2XException` for V2X-specific errors
- Never silently catch and ignore exceptions

### Logging

Use SLF4J for all logging:

```java
private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

logger.debug("Detailed debug information");
logger.info("Important events");
logger.warn("Warning conditions");
logger.error("Error conditions", exception);
```

### Testing

When adding tests:

1. Create test classes in `src/test/java`
2. Use JUnit 5 for unit tests
3. Mock the STEP SDK client for testing
4. Test both success and failure scenarios

Example:
```java
@Test
void testSendCAM() throws V2XException {
    V2XEventSender sender = new V2XEventSender(mockStepClient);
    sender.connect();
    
    boolean result = sender.sendCAM(51.5074, -0.1278, 13.89, 45.0);
    
    assertTrue(result);
}
```

## Project Structure

```
v2x-sample/
├── src/main/java/com/vodafone/v2x/sample/
│   ├── V2XSampleApp.java         # Main application entry point
│   ├── V2XEventSender.java       # Handles sending V2X messages
│   ├── V2XEventReceiver.java     # Handles receiving V2X messages
│   ├── V2XEventPayload.java      # Data structure for V2X messages
│   ├── V2XEventListener.java     # Interface for event callbacks
│   └── V2XException.java         # V2X-specific exception
├── src/main/resources/
│   └── simplelogger.properties   # Logging configuration
├── lib/
│   └── vodafone-step-sdk.jar     # STEP SDK (provided by user)
├── pom.xml                        # Maven configuration
└── README.md                      # Project documentation
```

## STEP SDK Integration

The current implementation includes placeholder code marked with `TODO` comments:

```java
// TODO: Implement actual STEP SDK connection logic
// Example (when SDK is available):
// stepClient.connect();
```

When integrating the actual SDK:

1. **Review SDK Documentation**
   - Study the STEP SDK API
   - Understand connection lifecycle
   - Learn message formats and protocols

2. **Update Connection Logic**
   - Replace mock implementations in `connect()` methods
   - Add proper authentication and configuration
   - Handle connection states

3. **Update Send Logic**
   - Replace simulation code with actual SDK calls
   - Convert payload to SDK message format
   - Handle send results and errors

4. **Update Receive Logic**
   - Implement actual message subscription
   - Parse incoming SDK messages
   - Handle message acknowledgment if needed

## Common Tasks

### Adding a New Message Type

1. Add constants:
   ```java
   public static final String MESSAGE_TYPE_IVIM = "IVIM";
   ```

2. Add sender method:
   ```java
   public boolean sendIVIM(/* parameters */) throws V2XException {
       V2XEventPayload payload = V2XEventPayload.builder()
           // Build payload
           .build();
       return sendEvent("IVIM", payload);
   }
   ```

3. Update documentation in README.md

### Changing Log Levels

Edit `src/main/resources/simplelogger.properties`:

```properties
org.slf4j.simpleLogger.defaultLogLevel=debug
```

### Building Without Tests

```bash
mvn clean package -DskipTests
```

### Running with Custom Configuration

```bash
java -Dconfig.file=custom.properties -jar target/v2x-sample-1.0.0.jar
```

## Submitting Changes

1. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Write clean, documented code
   - Follow the coding guidelines
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
   - Reference any related issues
   - Include test results

## Code Review Process

All contributions go through code review:

- Code follows style guidelines
- Changes are well-documented
- Tests pass successfully
- No breaking changes without discussion
- V2X-specific concepts are clearly explained

## Questions?

If you have questions:

- Check the README.md
- Review existing code examples
- Open an issue for discussion
- Contact the maintainers

## V2X Resources

Helpful resources for V2X development:

- [ETSI ITS Standards](https://www.etsi.org/technologies/intelligent-transport)
- [Vodafone STEP Documentation](https://developer.vodafone.com/step)
- [V2X Communication Overview](https://en.wikipedia.org/wiki/Vehicle-to-everything)

Thank you for contributing!
