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

Place your Vodafone STEP SDK jar file in the `lib/` directory:

```bash
cp /path/to/your/vodafone-step-sdk.jar lib/
```

**Note:** The jar MUST be named `vodafone-step-sdk.jar`

## 3. Build

```bash
mvn clean package
```

## 4. Run

```bash
# Option 1: Use the convenience script
./run.sh

# Option 2: Run the jar directly
java -jar target/v2x-sample-1.0.0.jar
```

## 5. Expected Output

You should see output showing:
- ✓ STEP client initialization
- ✓ Sender and receiver connection
- ✓ CAM message sent (Cooperative Awareness Message)
- ✓ DENM message sent (Decentralized Environmental Notification Message)
- ✓ Events received via listener
- ✓ Clean shutdown

## What's Next?

1. **Read the full README.md** for detailed documentation
2. **Check CONTRIBUTING.md** if you want to extend the application
3. **Review the code** in `src/main/java/com/vodafone/v2x/sample/`
4. **Integrate actual SDK** by updating the TODO sections in the code

## Common Issues

### Issue: "Cannot find SDK jar"
**Solution:** Make sure `lib/vodafone-step-sdk.jar` exists

### Issue: "Wrong Java version"
**Solution:** Install Java 21 or update `pom.xml` to use your Java version

### Issue: "Build fails"
**Solution:** Run `mvn clean install -U` to force update dependencies

## Need Help?

- Check the **README.md** for comprehensive documentation
- Review **CONTRIBUTING.md** for development guidelines
- Open an issue for questions or problems
