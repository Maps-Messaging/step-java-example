package com.vodafone.v2x.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * V2XSampleApp - Main application demonstrating V2X send and receive functionality.
 * 
 * This is a standalone Java console application that demonstrates how to use the
 * Vodafone STEP SDK for V2X communication without UI or Android OS dependencies.
 * 
 * Inspired by: https://github.com/Vodafone/HelloV2XWorld-Android
 */
public class V2XSampleApp {
    
    private static final Logger logger = LoggerFactory.getLogger(V2XSampleApp.class);
    
    public static void main(String[] args) {
        logger.info("=== V2X Send/Receive Sample Application ===");
        logger.info("Vodafone STEP SDK - Pure Java Console Application");
        logger.info("");
        
        V2XSampleApp app = new V2XSampleApp();
        
        try {
            app.run();
        } catch (Exception e) {
            logger.error("Application error", e);
            System.exit(1);
        }
    }
    
    /**
     * Main application execution flow.
     */
    private void run() {
        Object stepClient = null;
        V2XEventSender sender = null;
        V2XEventReceiver receiver = null;
        
        try {
            logger.info("Step 1: Initializing STEP client...");
            stepClient = initializeStepClient();
            logger.info("STEP client initialized successfully");
            logger.info("");
            
            logger.info("Step 2: Creating sender and receiver...");
            sender = new V2XEventSender(stepClient);
            receiver = new V2XEventReceiver(stepClient);
            logger.info("Sender and receiver created");
            logger.info("");
            
            logger.info("Step 3: Connecting sender...");
            sender.connect();
            logger.info("");
            
            logger.info("Step 4: Connecting receiver...");
            receiver.connect();
            logger.info("");
            
            logger.info("Step 5: Setting up event listener...");
            setupEventListener(receiver);
            logger.info("");
            
            logger.info("Step 6: Starting receiver...");
            receiver.startListening();
            logger.info("");
            
            logger.info("Step 7: Demonstrating V2X message sending...");
            demonstrateSending(sender);
            logger.info("");
            
            logger.info("Step 8: Demonstrating V2X message receiving...");
            demonstrateReceiving(sender, receiver);
            logger.info("");
            
            logger.info("Step 9: Cleanup and shutdown...");
            cleanup(sender, receiver);
            logger.info("");
            
            logger.info("=== Application completed successfully ===");
            
        } catch (V2XException e) {
            logger.error("V2X operation failed: {}", e.getMessage(), e);
            cleanup(sender, receiver);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            cleanup(sender, receiver);
        }
    }
    
    /**
     * Initializes the STEP SDK client.
     * In a real implementation, this would configure and create the actual SDK client.
     * 
     * @return The initialized STEP client
     */
    private Object initializeStepClient() {
        logger.info("Initializing Vodafone STEP SDK...");
        
        // TODO: Implement actual STEP SDK initialization
        // Example (when SDK is available):
        // StepConfig config = new StepConfig();
        // config.setApiEndpoint("https://api.step.vodafone.com");
        // config.setApiKey(System.getenv("STEP_API_KEY"));
        // config.setClientId(System.getenv("STEP_CLIENT_ID"));
        // 
        // StepClient client = new StepClient(config);
        // client.initialize();
        // return client;
        
        logger.info("STEP SDK configuration:");
        logger.info("  - API Endpoint: [Configure with actual endpoint]");
        logger.info("  - Client Mode: Simulated (replace with actual SDK)");
        
        return new Object();
    }
    
    /**
     * Sets up an event listener to handle received V2X events.
     * 
     * @param receiver The V2X event receiver
     */
    private void setupEventListener(V2XEventReceiver receiver) {
        receiver.addListener((eventType, payload) -> {
            logger.info(">>> Event Received via Listener <<<");
            logger.info("    Type: {}", eventType);
            logger.info("    Latitude: {}", payload.getLatitude());
            logger.info("    Longitude: {}", payload.getLongitude());
            logger.info("    Speed: {} m/s", payload.getSpeed());
            logger.info("    Heading: {} degrees", payload.getHeading());
            
            if (payload.getEventType() != null && !payload.getEventType().isEmpty()) {
                logger.info("    Event Type: {}", payload.getEventType());
                logger.info("    Severity: {}", payload.getSeverity());
            }
            
            logger.info("    Timestamp: {}", payload.getTimestamp());
            logger.info("");
        });
        
        logger.info("Event listener registered");
    }
    
    /**
     * Demonstrates sending various types of V2X messages.
     * 
     * @param sender The V2X event sender
     * @throws V2XException if sending fails
     */
    private void demonstrateSending(V2XEventSender sender) throws V2XException {
        logger.info("Sending CAM (Cooperative Awareness Message)...");
        logger.info("  Location: 51.5074° N, 0.1278° W (London)");
        logger.info("  Speed: 13.89 m/s (50 km/h)");
        logger.info("  Heading: 45° (Northeast)");
        
        boolean camSent = sender.sendCAM(51.5074, -0.1278, 13.89, 45.0);
        if (camSent) {
            logger.info("✓ CAM sent successfully");
        } else {
            logger.warn("✗ CAM send failed");
        }
        logger.info("");
        
        waitForSeconds(1);
        
        logger.info("Sending DENM (Decentralized Environmental Notification Message)...");
        logger.info("  Location: 51.5074° N, 0.1278° W");
        logger.info("  Event: ROAD_WORKS");
        logger.info("  Severity: 3 (Moderate)");
        
        boolean denmSent = sender.sendDENM(51.5074, -0.1278, "ROAD_WORKS", 3);
        if (denmSent) {
            logger.info("✓ DENM sent successfully");
        } else {
            logger.warn("✗ DENM send failed");
        }
        logger.info("");
        
        waitForSeconds(1);
        
        logger.info("Sending custom V2X event...");
        V2XEventPayload customPayload = V2XEventPayload.builder()
                .latitude(48.8566)
                .longitude(2.3522)
                .speed(16.67)
                .heading(180.0)
                .timestamp(System.currentTimeMillis())
                .additionalData("Custom event data")
                .build();
        
        boolean customSent = sender.sendEvent("CUSTOM", customPayload);
        if (customSent) {
            logger.info("✓ Custom event sent successfully");
        } else {
            logger.warn("✗ Custom event send failed");
        }
    }
    
    /**
     * Demonstrates receiving V2X messages.
     * In a real implementation, these would be actual messages from other vehicles.
     * 
     * @param sender The sender (for simulation purposes)
     * @param receiver The V2X event receiver
     * @throws V2XException if receiving fails
     */
    private void demonstrateReceiving(V2XEventSender sender, V2XEventReceiver receiver) 
            throws V2XException {
        logger.info("Simulating received V2X events...");
        logger.info("(In production, these would be real messages from other vehicles)");
        logger.info("");
        
        waitForSeconds(1);
        
        V2XEventPayload incomingCAM = V2XEventPayload.builder()
                .latitude(52.5200)
                .longitude(13.4050)
                .speed(11.11)
                .heading(270.0)
                .timestamp(System.currentTimeMillis())
                .build();
        
        receiver.simulateReceivedEvent("CAM", incomingCAM);
        
        waitForSeconds(2);
        
        V2XEventPayload incomingDENM = V2XEventPayload.builder()
                .latitude(52.5200)
                .longitude(13.4050)
                .eventType("ACCIDENT")
                .severity(5)
                .timestamp(System.currentTimeMillis())
                .build();
        
        receiver.simulateReceivedEvent("DENM", incomingDENM);
        
        waitForSeconds(1);
        
        logger.info("Checking receive queue...");
        int queueSize = receiver.getQueueSize();
        logger.info("Events in queue: {}", queueSize);
        
        if (queueSize > 0) {
            logger.info("Polling events from queue:");
            V2XEventPayload event;
            while ((event = receiver.pollEvent()) != null) {
                logger.info("  - {}", event);
            }
        }
    }
    
    /**
     * Performs cleanup by disconnecting sender and receiver.
     * 
     * @param sender The sender to disconnect (may be null)
     * @param receiver The receiver to disconnect (may be null)
     */
    private void cleanup(V2XEventSender sender, V2XEventReceiver receiver) {
        logger.info("Performing cleanup...");
        
        if (receiver != null) {
            try {
                receiver.stopListening();
                receiver.disconnect();
            } catch (Exception e) {
                logger.error("Error disconnecting receiver", e);
            }
        }
        
        if (sender != null) {
            try {
                sender.disconnect();
            } catch (Exception e) {
                logger.error("Error disconnecting sender", e);
            }
        }
        
        logger.info("Cleanup completed");
    }
    
    /**
     * Waits for the specified number of seconds.
     * 
     * @param seconds Number of seconds to wait
     */
    private void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted");
        }
    }
}
