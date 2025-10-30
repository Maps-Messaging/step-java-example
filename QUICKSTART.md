# Quick Start Guide

## 1. Prerequisites Check

```bash
# Verify Java 21 is installed
java -version
# Should show: openjdk version "21..."

# Verify Maven is installed
mvn -version
# Should show: Apache Maven 3.6+
```

## 2. Setup SDK (Important!)

Place your Vodafone V2X SDK jar file in the `lib/` directory:

```bash
cp /path/to/your/v2xsdk4java-3.1.0.jar lib/
```

**Note:** The jar MUST be named `v2xsdk4java-3.1.0.jar`

## 3. Configure Credentials

Copy the example configuration and add your STEP credentials:

```bash
# Configuration file is at: src/main/resources/application.properties
# Edit it to add your credentials:
app.id=YOUR_APPLICATION_ID_HERE
app.token=YOUR_APPLICATION_TOKEN_HERE
```

Get credentials from: https://developer.vodafone.com/step

## 4. Build

```bash
mvn clean package
```

## 5. Run

```bash
# Option 1: Use the convenience script
./run.sh

# Option 2: Run the shaded jar directly
java -jar target/step-java-example-1.0.0-shaded.jar

# Option 3: Run with Maven
mvn exec:java -Dexec.mainClass="com.vodafone.v2x.example.V2XApplication"
```

## 6. Expected Output

You should see output showing:
- ✓ Configuration loading
- ✓ Location provider initialization
- ✓ V2X SDK configuration and startup
- ✓ Service initialization and STEP connectivity
- ✓ CAM service started (broadcasting vehicle presence)
- ✓ DENM service started
- ✓ Test DENM triggered (ACCIDENT event)
- ✓ Receiving CAM/DENM messages from other stations
- ✓ Clean shutdown

## What's Next?

1. **Read the full README.md** for detailed documentation
2. **Review the SDK User Guide** in `doc/Java V2X SDK - User Guide 3.1.0.pdf`
3. **Check the code** in `src/main/java/com/vodafone/v2x/example/`
4. **Extend the application** by adding more message types (IVIM, SPATEM, MAPEM, etc.)

## Common Issues

### Issue: "Cannot find SDK jar"
**Solution:** Make sure `lib/v2xsdk4java-3.1.0.jar` exists

### Issue: "Wrong Java version"
**Solution:** Install Java 21 or update `pom.xml` to use your Java version

### Issue: "Missing application.properties"
**Solution:** Create `src/main/resources/application.properties` with your STEP credentials

### Issue: "Service initialization timeout"
**Solution:** 
- Check network connectivity
- Verify STEP credentials are correct
- Ensure firewall allows connections to STEP platform

### Issue: "Build fails"
**Solution:** Run `mvn clean install -U` to force update dependencies

## Need Help?

- Check the **README.md** for comprehensive documentation
- Review the **SDK User Guide** in the `doc/` folder
- Check **CONTRIBUTING.md** for development guidelines
- Reference: https://github.com/Vodafone/HelloV2XWorld-Android
- Open an issue for questions or problems
