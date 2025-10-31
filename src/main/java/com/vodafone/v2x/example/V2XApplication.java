package com.vodafone.v2x.example;

import com.vodafone.v2x.example.config.AppConfig;
import com.vodafone.v2x.example.handlers.CAMHandler;
import com.vodafone.v2x.example.handlers.DENMHandler;
import com.vodafone.v2x.example.location.FakeLocationProvider;
import com.vodafone.v2xsdk4javav2.facade.V2XSDK;
import com.vodafone.v2xsdk4javav2.facade.SDKConfiguration;
import com.vodafone.v2xsdk4javav2.facade.enums.DENMType;
import com.vodafone.v2xsdk4javav2.facade.enums.LogLevel;
import com.vodafone.v2xsdk4javav2.facade.enums.ServiceMode;
import com.vodafone.v2xsdk4javav2.facade.enums.StationType;
import com.vodafone.v2xsdk4javav2.facade.enums.V2XConnectivityState;
import com.vodafone.v2xsdk4javav2.facade.enums.V2XServiceState;
import com.vodafone.v2xsdk4javav2.facade.enums.VehicleRole;
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
            SDKConfiguration sdkConfig = SDKConfiguration.builder()
                .applicationID(config.getApplicationId())
                .applicationToken(config.getApplicationToken())
                .stationType(StationType.PASSENGER_CAR)
                .vehicleRole(VehicleRole.DEFAULT)
                .camServiceMode(ServiceMode.TxAndRx)
                .camPublishGroup("public")
                .camSubscribeGroup("public")
                .denmServiceMode(ServiceMode.TxAndRx)
                .denmPublishGroup("public")
                .denmSubscribeGroup("public")
                .build();
            logger.info("  SDK configuration created");
            logger.info("  - Station Type: PASSENGERCAR");
            logger.info("  - CAM Service Mode: TxAndRx");
            logger.info("  - DENM Service Mode: TxAndRx");
            logger.info("");
            
            // 4. Create V2XSDK instance (Section 8.3.3)
            logger.info("Step 4: Creating V2X SDK instance...");
            sdk = new V2XSDK(locationProvider, sdkConfig);
            sdk.setSDKLogLevel(LogLevel.DEBUG);
            logger.info("  V2X SDK instance created");
            logger.info("");
            
            // 5. Start V2X Service (Section 8.3.4)
            logger.info("Step 5: Starting V2X service...");
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
                if (initRetries > 30) {
                    throw new RuntimeException("Service initialization timeout");
                }
            }
            logger.info("  V2X service is UP AND RUNNING");
            logger.info("");
            
            // 7. Wait for STEP connectivity (Section 8.3.6)
            logger.info("Step 7: Waiting for STEP connectivity...");
            int connectRetries = 0;
            while (sdk.getV2XConnectivityState() != V2XConnectivityState.CONNECTED) {
                Thread.sleep(1000L);
                logger.debug("  Connectivity state: {}", sdk.getV2XConnectivityState());
                connectRetries++;
                if (connectRetries > 30) {
                    throw new RuntimeException("STEP connectivity timeout");
                }
            }
            logger.info("  Connected to STEP platform");
            logger.info("");
            
            // 8. Subscribe to events (Section 8.3.7)
            logger.info("Step 8: Subscribing to SDK events...");
            CAMHandler camHandler = new CAMHandler();
            DENMHandler denmHandler = new DENMHandler();
            
            sdk.subscribe(camHandler, EventType.CAM_LIST_CHANGED);
            sdk.subscribe(denmHandler, EventType.DENM_LIST_CHANGED);
            logger.info("  Event handlers registered");
            logger.info("  - CAM list change events");
            logger.info("  - DENM list change events");
            logger.info("");
            
            // 9. Start CAM Service (Section 8.3.8)
            logger.info("Step 9: Starting CAM service...");
            sdk.startCAMService();
            logger.info("  CAM service started - broadcasting vehicle presence");
            logger.info("");
            
            // 10. Start DENM Service (Section 8.3.9)
            logger.info("Step 10: Starting DENM service...");
            sdk.startDENMService();
            logger.info("  DENM service started");
            logger.info("");
            
            // 11. Send a test DENM (Section 8.3.9 - Trigger DENM)
            logger.info("Step 11: Sending test DENM...");
            GnssLocation eventLocation = new GnssLocation(
                config.getTestLatitude(),
                config.getTestLongitude(),
                null,
                null,
                null,
                null,
                sdk.getUTCTimeInMs()
            );
            long sequenceNumber = sdk.denmTrigger(DENMType.ACCIDENT_UNSECUREDACCIDENT, eventLocation);
            logger.info("  DENM triggered with sequence number: {}", sequenceNumber);
            logger.info("  Event Type: UNSECURED_ACCIDENT");
            logger.info("  Location: ({}, {})", config.getTestLatitude(), config.getTestLongitude());
            logger.info("");
            
            // 12. Run for demonstration period
            logger.info("Step 12: Application running - receiving V2X messages...");
            logger.info("  Press Ctrl+C to stop");
            logger.info("  Monitoring period: 60 seconds");
            logger.info("");
            
            Thread.sleep(60000L);
            
            // 13. Terminate DENM (Section 8.3.16)
            logger.info("");
            logger.info("Step 13: Terminating DENM...");
            sdk.denmTerminate(sequenceNumber);
            logger.info("  DENM terminated");
            logger.info("");
            
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
                logger.info("  Stopping CAM service...");
                sdk.stopCAMService();
                
                logger.info("  Stopping DENM service...");
                sdk.stopDENMService();
                
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
