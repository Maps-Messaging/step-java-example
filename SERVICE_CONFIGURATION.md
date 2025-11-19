# Service Configuration Guide

## Overview

The V2X STEP Java Application supports running CAM and DENM services independently or together. This allows you to:

- Test individual message types in isolation
- Reduce bandwidth for specific use cases
- Focus on particular V2X scenarios
- Simplify debugging and development

## Service Types

### CAM (Cooperative Awareness Messages)
**Purpose:** Periodic broadcasting of vehicle position, speed, and status

**Use cases:**
- Vehicle tracking and awareness
- Traffic density monitoring
- Collision avoidance systems
- Fleet management

**Behavior:**
- Broadcasts at 1 Hz (every second)
- Contains: Position (lat/lon/alt), speed, heading, vehicle dimensions
- Always-on when enabled
- Low event significance (continuous status)

### DENM (Decentralized Environmental Notification Messages)
**Purpose:** Event-based notification of hazards and incidents

**Use cases:**
- Accident notifications
- Road hazard warnings
- Emergency vehicle alerts
- Traffic incident reporting

**Behavior:**
- Event-triggered (not periodic)
- Contains: Event type, location, severity, validity duration
- Transmitted multiple times for reliability
- High event significance (important warnings)

## Configuration Options

### Option 1: DENM Only (Default)

**Configuration:**
```properties
service.cam.enabled=false
service.denm.enabled=true
```

**Behavior:**
- ✓ DENM service starts and connects to MQTT
- ✓ Sends test DENM (ACCIDENT event)
- ✓ Receives DENM messages from other stations
- ✗ No CAM broadcasting
- ✗ No CAM reception

**Output example:**
```
Step 1: Loading configuration...
  CAM Service: DISABLED
  DENM Service: ENABLED
...
Step 9: CAM service disabled (skipping)
Step 10: Starting DENM service...
  DENM service started
Step 11: Sending test DENM...
  DENM triggered with sequence number: 1
```

**Bandwidth:** ~2-5 KB/s (DENM only during active events)

### Option 2: CAM Only

**Configuration:**
```properties
service.cam.enabled=true
service.denm.enabled=false
```

**Behavior:**
- ✓ CAM service starts and connects to MQTT
- ✓ Broadcasts CAM every second (1 Hz)
- ✓ Receives CAM messages from other stations
- ✗ No DENM events
- ✗ No DENM reception

**Output example:**
```
Step 1: Loading configuration...
  CAM Service: ENABLED
  DENM Service: DISABLED
...
Step 9: Starting CAM service...
  CAM service started - broadcasting vehicle presence
Step 10: DENM service disabled (skipping)
Step 11: DENM service disabled (skipping test DENM)
```

**Bandwidth:** ~15-20 KB/s (continuous CAM at 1 Hz)

### Option 3: Both Services

**Configuration:**
```properties
service.cam.enabled=true
service.denm.enabled=true
```

**Behavior:**
- ✓ Both CAM and DENM services start
- ✓ Broadcasts CAM every second
- ✓ Sends test DENM event
- ✓ Receives both CAM and DENM from other stations
- ✓ Full V2X functionality

**Output example:**
```
Step 1: Loading configuration...
  CAM Service: ENABLED
  DENM Service: ENABLED
...
Step 9: Starting CAM service...
  CAM service started - broadcasting vehicle presence
Step 10: Starting DENM service...
  DENM service started
Step 11: Sending test DENM...
  DENM triggered with sequence number: 1
```

**Bandwidth:** ~15-25 KB/s (CAM + occasional DENM)

## How It Works

### SDK Configuration

The application dynamically builds the SDK configuration based on enabled services:

```java
SDKConfiguration.SDKConfigurationBuilder configBuilder = SDKConfiguration.builder()
    .stepInstance(stepInstance)
    .applicationID(config.getApplicationId())
    .applicationToken(config.getApplicationToken())
    .stationType(StationType.PASSENGER_CAR);

// Only configure CAM if enabled
if (config.isCamServiceEnabled()) {
    configBuilder
        .camServiceMode(ServiceMode.TxAndRx)
        .camPublishGroup("926696_216")
        .camSubscribeGroup("926696_216");
}

// Only configure DENM if enabled
if (config.isDenmServiceEnabled()) {
    configBuilder
        .denmServiceMode(ServiceMode.TxAndRx)
        .denmPublishGroup("926696_216")
        .denmSubscribeGroup("926696_216");
}
```

### Event Handler Subscription

Handlers are only created and subscribed for enabled services:

```java
if (config.isCamServiceEnabled()) {
    camHandler = new CAMHandler();
    sdk.subscribe(camHandler, EventType.CAM_LIST_CHANGED);
}

if (config.isDenmServiceEnabled()) {
    denmHandler = new DENMHandler();
    sdk.subscribe(denmHandler, EventType.DENM_LIST_CHANGED);
}
```

### Service Lifecycle

Services are conditionally started and stopped:

```java
// Start
if (config.isCamServiceEnabled()) {
    sdk.startCAMService();
}

if (config.isDenmServiceEnabled()) {
    sdk.startDENMService();
}

// Stop (during shutdown)
if (config.isCamServiceEnabled()) {
    sdk.stopCAMService();
}

if (config.isDenmServiceEnabled()) {
    sdk.stopDENMService();
}
```

## MQTT Connection Timing

**Important:** MQTT connection is established when the **first service starts**, not during V2X service initialization.

- If only DENM enabled → MQTT connects when DENM service starts
- If only CAM enabled → MQTT connects when CAM service starts
- If both enabled → MQTT connects when first service (CAM) starts

The application waits for MQTT connection **after** starting services (Step 10.5).

## Common Use Cases

### Development: Testing DENM Logic
```properties
service.cam.enabled=false
service.denm.enabled=true
debug.mode=true
```
Focus on event-based messaging without CAM noise in logs.

### Production: Full V2X
```properties
service.cam.enabled=true
service.denm.enabled=true
debug.mode=false
```
Complete V2X functionality for real-world deployment.

### Monitoring: Receive Only
Edit V2XApplication.java to use `ServiceMode.RxOnly`:
```java
.camServiceMode(ServiceMode.RxOnly)
.denmServiceMode(ServiceMode.RxOnly)
```
Receive messages without broadcasting (listener mode).

### Testing: CAM Frequency Analysis
```properties
service.cam.enabled=true
service.denm.enabled=false
debug.mode=true
```
Analyze CAM transmission patterns and timing.

## Performance Considerations

| Configuration | MQTT Topics | Bandwidth | CPU Usage | Use Case |
|--------------|-------------|-----------|-----------|----------|
| DENM only | 9 | Low (~2 KB/s) | Low | Event notifications only |
| CAM only | 9 | Medium (~20 KB/s) | Medium | Position tracking only |
| Both | 18 | Medium (~25 KB/s) | Medium | Full V2X |

## Troubleshooting

### "MQTT connection timeout" with DENM only
**Cause:** DENM service may take longer to establish connection
**Solution:** Increase timeout in Step 10.5 or enable CAM temporarily

### No messages received
**Cause:** Service not enabled or wrong group ID
**Solution:** Check `service.*.enabled=true` and verify group IDs match your STEP configuration

### Both services but only one works
**Cause:** SDK configuration issue
**Solution:** Enable debug mode and check SDK logs for service initialization

## Advanced Configuration

### Custom Groups per Service

Edit V2XApplication.java to use different groups:

```java
if (config.isCamServiceEnabled()) {
    configBuilder
        .camPublishGroup("478346_22")   // Different group
        .camSubscribeGroup("478346_22");
}

if (config.isDenmServiceEnabled()) {
    configBuilder
        .denmPublishGroup("926696_216")  // Different group
        .denmSubscribeGroup("926696_216");
}
```

### Service-Specific Demo Duration

Modify the sleep duration based on service type:

```java
// Shorter demo for DENM-only (events are immediate)
long demoTime = config.isDenmServiceEnabled() && !config.isCamServiceEnabled()
    ? 10000L  // 10 seconds
    : 60000L; // 60 seconds

Thread.sleep(demoTime);
```

## Future Enhancements

The same pattern can be extended for other V2X message types:

```properties
service.cam.enabled=true
service.denm.enabled=true
service.ivim.enabled=false
service.spatem.enabled=false
service.mapem.enabled=false
```

Simply add similar conditional logic for each new service type.
