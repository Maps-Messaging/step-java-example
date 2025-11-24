package com.vodafone.v2x.example;

import com.vodafone.v2x.example.config.AppConfig;
import com.vodafone.v2x.example.handlers.CAMHandler;
import com.vodafone.v2x.example.handlers.DENMHandler;
import com.vodafone.v2x.example.location.FakeLocationProvider;
import com.vodafone.v2xsdk4javav2.facade.V2XSDK;
import com.vodafone.v2xsdk4javav2.facade.SDKConfiguration;
import com.vodafone.v2xsdk4javav2.facade.enums.*;
import com.vodafone.v2xsdk4javav2.facade.events.EventType;
import com.vodafone.v2xsdk4javav2.facade.models.GnssLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V2XApplication {
    private static final Logger logger = LoggerFactory.getLogger(V2XApplication.class);
    private V2XSDK sdk;
    private FakeLocationProvider locationProvider;
    
    public static void main(String[] args) {
        V2XApplication app = new V2XApplication();
        app.run();
    }
    
    public void run() {
        try {
            logger.info("=== V2X STEP Java Application ===");
            logger.info("Vodafone V2X SDK Demo - CAM and DENM Example");
            logger.info("");

            // 1. Load configuration (Section 8.3.1)
            logger.info("Step 1: Loading configuration...");
            AppConfig config = new AppConfig();
            logger.info("  Application ID: {}", config.getApplicationId());
            logger.info("  Test Location: ({}, {})", config.getTestLatitude(), config.getTestLongitude());
            logger.info("  STEP Instance: {}, {}, {}", StepInstance.DE_DEV_FRANKFURT,StepInstance.DE_DEV_FRANKFURT.getMqttHost(),StepInstance.DE_DEV_FRANKFURT.getMqttPort());
            logger.info("  CAM Service: {}", config.isCamServiceEnabled() ? "ENABLED" : "DISABLED");
            logger.info("  DENM Service: {}", config.isDenmServiceEnabled() ? "ENABLED" : "DISABLED");
            logger.info("  Debug Mode: {}", config.isDebugMode());

            // Enable debug logging if configured
            if (config.isDebugMode()) {
                logger.info("  Enabling TLS and MQTT debug logging...");
                System.setProperty("javax.net.debug", "ssl,handshake,trustmanager");
                System.setProperty("org.eclipse.paho.client.mqttv3.logging.enabled", "true");
            }
            logger.info("");
            
            // 2. Create location provider (Annex 10.3)
            logger.info("Step 2: Initializing location provider...");
            locationProvider = new FakeLocationProvider(
                config.getTestLatitude(),
                config.getTestLongitude()
            );
            logger.info("  Location provider initialized");
            logger.info("");
            
            // 3. Configure SDK (Section 8.3.1)
            logger.info("Step 3: Configuring V2X SDK...");
            StepInstance stepInstance = StepInstance.DE_DEV_FRANKFURT;

            // Log STEP instance connection details
            logger.info("  STEP Instance: {}", stepInstance);
            logger.info("  MQTT Host: {}", stepInstance.getMqttHost());
            logger.info("  MQTT Port: {}", stepInstance.getMqttPort());
            logger.info("  Application ID: {}", config.getApplicationId());
            logger.info("  Application Token: {}...", config.getApplicationToken().substring(0, Math.min(8, config.getApplicationToken().length())));

            SDKConfiguration.SDKConfigurationBuilder configBuilder = SDKConfiguration.builder()
                .stepInstance(stepInstance)
                .applicationID(config.getApplicationId())
                .applicationToken(config.getApplicationToken())
                .mqttClientID("testClient123")
                .stationType(StationType.PASSENGER_CAR);

            // Configure CAM service if enabled
            if (config.isCamServiceEnabled()) {
                configBuilder
                    .camServiceMode(ServiceMode.TxAndRx)
                    .camPublishGroup("926696_216")
                    .camSubscribeGroup("926696_216");
                logger.info("  - CAM Service Mode: TxAndRx");
                logger.info("  - CAM Publish Group: 926696_216");
                logger.info("  - CAM Subscribe Group: 926696_216");
            } else {
                logger.info("  - CAM Service: DISABLED");
            }

            // Configure DENM service if enabled
            if (config.isDenmServiceEnabled()) {
                configBuilder
                    .denmServiceMode(ServiceMode.TxAndRx)
                    .denmPublishGroup("926696_216")
                    .denmSubscribeGroup("926696_216");
                logger.info("  - DENM Service Mode: TxAndRx");
                logger.info("  - DENM Publish Group: 926696_216");
                logger.info("  - DENM Subscribe Group: 926696_216");
            } else {
                logger.info("  - DENM Service: DISABLED");
            }

            SDKConfiguration sdkConfig = configBuilder.build();
            logger.info("  SDK configuration created");
            logger.info("  - Station Type: PASSENGERCAR");
            logger.info("");
            
            // 4. Create V2XSDK instance (Section 8.3.3)
            logger.info("Step 4: Creating V2X SDK instance...");

            sdk = new V2XSDK(locationProvider, sdkConfig);
            // Set SDK log level based on debug mode
            LogLevel sdkLogLevel = config.isDebugMode() ? LogLevel.DEBUG : LogLevel.INFO;
            sdk.setSDKLogLevel(sdkLogLevel);
            logger.info("  V2X SDK instance created");
            logger.info("  SDK Log Level set to: {}", sdkLogLevel);
            logger.info("");
            
            // 5. Start V2X Service (Section 8.3.4)
            logger.info("Step 5: Starting V2X service...");
            if (config.isDebugMode()) {
                logger.info("  Note: Watch for SSL/TLS handshake logs below");
            }
            sdk.startV2XService();
            logger.info("  V2X service start initiated");
            logger.info("");
            
            // 6. Wait for initialization (Section 8.3.5)
            logger.info("Step 6: Waiting for V2X service initialization...");
            int initRetries = 0;
            while (sdk.getV2XServiceState() != V2XServiceState.UP_AND_RUNNING) {
                Thread.sleep(1000L);
                logger.debug("  Service state: {}", sdk.getV2XServiceState());
                initRetries++;
                if (initRetries > 10) {
                    throw new RuntimeException("Service initialization timeout");
                }
            }
            logger.info("  V2X service is UP AND RUNNING");
            logger.info("");
            
            // 7. Check initial connectivity state (connection may happen after service start)
            logger.info("Step 7: Checking initial connectivity state...");
            logger.info("  Initial connectivity state: {}", sdk.getV2XConnectivityState());
            logger.info("  Note: MQTT connection typically establishes when CAM/DENM services start");
            logger.info("");
            
            // 8. Subscribe to events (Section 8.3.7)
            logger.info("Step 8: Subscribing to SDK events...");
            CAMHandler camHandler = null;
            DENMHandler denmHandler = null;

            if (config.isCamServiceEnabled()) {
                camHandler = new CAMHandler();
                sdk.subscribe(camHandler, EventType.CAM_LIST_CHANGED);
                logger.info("  - CAM list change events subscribed");
            }

            if (config.isDenmServiceEnabled()) {
                denmHandler = new DENMHandler();
                sdk.subscribe(denmHandler, EventType.DENM_LIST_CHANGED);
                logger.info("  - DENM list change events subscribed");
            }

            logger.info("  Event handlers registered");
            logger.info("");
            
            // 9. Start CAM Service (Section 8.3.8)
            if (config.isCamServiceEnabled()) {
                logger.info("Step 9: Starting CAM service...");
                sdk.startCAMService();
                logger.info("  CAM service started - broadcasting vehicle presence");
                logger.info("");
            } else {
                logger.info("Step 9: CAM service disabled (skipping)");
                logger.info("");
            }

            // 10. Start DENM Service (Section 8.3.9)
            if (config.isDenmServiceEnabled()) {
                logger.info("Step 10: Starting DENM service...");
                sdk.startDENMService();
                logger.info("  DENM service started");
                logger.info("");
            } else {
                logger.info("Step 10: DENM service disabled (skipping)");
                logger.info("");
            }

            // 10.5. Wait for MQTT connectivity after services are started
            logger.info("Step 10.5: Waiting for MQTT connection (now that services are started)...");
            int connectRetries = 0;
            while (sdk.getV2XConnectivityState() != V2XConnectivityState.CONNECTED) {
                Thread.sleep(1000L);
                logger.info("  Connectivity state: {} (retry {}/15)", sdk.getV2XConnectivityState(), connectRetries + 1);
                connectRetries++;
                if (connectRetries > 15) {
                    logger.warn("  MQTT connection not established after 15 seconds, continuing anyway...");
                    break;
                }
            }
            if (sdk.getV2XConnectivityState() == V2XConnectivityState.CONNECTED) {
                logger.info("  ✓ Connected to STEP MQTT platform!");
            }
            logger.info("");

            // 11. Send a test DENM (Section 8.3.9 - Trigger DENM)
            long sequenceNumber = -1;
            if (config.isDenmServiceEnabled()) {
                logger.info("Step 11: Sending test DENM...");
                GnssLocation eventLocation = new GnssLocation(
                    config.getTestLatitude(),
                    config.getTestLongitude(),
                        50.0,
                        0.0F,
                        0.0f,
                        2.0f,
                    sdk.getUTCTimeInMs()
                );
                sequenceNumber = sdk.denmTrigger(DENMType.ACCIDENT_UNSECUREDACCIDENT, eventLocation);
                logger.info("  DENM triggered with sequence number: {}", sequenceNumber);
                logger.info("  Event Type: UNSECURED_ACCIDENT");
                logger.info("  Location: ({}, {})", config.getTestLatitude(), config.getTestLongitude());
                logger.info("");
            } else {
                logger.info("Step 11: DENM service disabled (skipping test DENM)");
                logger.info("");
            }
            
            // 12. Run for demonstration period
            logger.info("Step 12: Application running - receiving V2X messages...");
            logger.info("  Press Ctrl+C to stop");
            logger.info("  Monitoring period: 5 seconds");
            logger.info("");
            
            Thread.sleep(5000L);
            
            // 13. Terminate DENM (Section 8.3.16)
            logger.info("");
            if (config.isDenmServiceEnabled() && sequenceNumber >= 0) {
                logger.info("Step 13: Terminating DENM...");
                sdk.denmTerminate(sequenceNumber);
                logger.info("  DENM terminated");
                logger.info("");
            } else {
                logger.info("Step 13: No DENM to terminate (skipping)");
                logger.info("");
            }
            
            // 14. Cleanup (Section 8.3.17)
            shutdown();
            
            logger.info("");
            logger.info("=== Application completed successfully ===");
            
        } catch (Exception e) {
            logger.error("Application error", e);
            shutdown();
            System.exit(1);
        }
    }
    
    private void shutdown() {
        logger.info("Step 14: Performing cleanup...");

        if (sdk != null) {
            try {
                // Only stop services that were started
                AppConfig config = new AppConfig();

                if (config.isCamServiceEnabled()) {
                    logger.info("  Stopping CAM service...");
                    sdk.stopCAMService();
                }

                if (config.isDenmServiceEnabled()) {
                    logger.info("  Stopping DENM service...");
                    sdk.stopDENMService();
                }

                logger.info("  Stopping V2X service...");
                sdk.stopV2XService();

                logger.info("  All services stopped");
            } catch (Exception e) {
                logger.error("Error during shutdown", e);
            }
        }


        if (locationProvider != null) {
            try {
                locationProvider.turnOff();
            } catch (Exception e) {
                logger.error("Error stopping location provider", e);
            }
        }

        logger.info("  Cleanup completed");
    }
}
