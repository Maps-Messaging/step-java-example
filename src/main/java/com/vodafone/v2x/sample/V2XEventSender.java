package com.vodafone.v2x.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * V2XEventSender demonstrates how to send V2X events using the Vodafone STEP SDK.
 * 
 * V2X (Vehicle-to-Everything) communication enables vehicles to communicate with each other
 * and with infrastructure, pedestrians, and networks. This class shows how to construct
 * and send V2X messages through the STEP platform.
 */
public class V2XEventSender {
    
    private static final Logger logger = LoggerFactory.getLogger(V2XEventSender.class);
    
    private final Object stepClient;
    private boolean isConnected;
    
    /**
     * Creates a new V2X Event Sender.
     * 
     * @param stepClient The initialized STEP SDK client instance
     */
    public V2XEventSender(Object stepClient) {
        this.stepClient = stepClient;
        this.isConnected = false;
    }
    
    /**
     * Connects the sender to the V2X network.
     * In a real implementation, this would establish a connection to the STEP platform.
     * 
     * @throws V2XException if connection fails
     */
    public void connect() throws V2XException {
        try {
            logger.info("Connecting V2X Event Sender to STEP platform...");
            
            // TODO: Implement actual STEP SDK connection logic
            // Example (when SDK is available):
            // stepClient.connect();
            // stepClient.authenticate(credentials);
            
            isConnected = true;
            logger.info("V2X Event Sender connected successfully");
            
        } catch (Exception e) {
            logger.error("Failed to connect V2X Event Sender", e);
            throw new V2XException("Connection failed", e);
        }
    }
    
    /**
     * Sends a V2X event message.
     * 
     * V2X messages typically include:
     * - Message type (CAM - Cooperative Awareness Message, DENM - Decentralized Environmental Notification Message, etc.)
     * - Vehicle position (latitude, longitude, altitude)
     * - Speed and heading
     * - Timestamp
     * - Additional event-specific data
     * 
     * @param eventType The type of V2X event (e.g., "CAM", "DENM", "IVIM")
     * @param payload The message payload containing event data
     * @return true if the message was sent successfully
     * @throws V2XException if sending fails
     */
    public boolean sendEvent(String eventType, V2XEventPayload payload) throws V2XException {
        if (!isConnected) {
            throw new V2XException("Sender is not connected. Call connect() first.");
        }
        
        try {
            logger.info("Sending V2X event: type={}, payload={}", eventType, payload);
            
            // TODO: Implement actual STEP SDK send logic
            // Example (when SDK is available):
            // V2XMessage message = new V2XMessage();
            // message.setType(eventType);
            // message.setPayload(payload.toBytes());
            // message.setTimestamp(System.currentTimeMillis());
            // 
            // SendResult result = stepClient.send(message);
            // return result.isSuccess();
            
            // Simulated send for demonstration
            logger.info("V2X event sent successfully: {}", eventType);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send V2X event: {}", eventType, e);
            throw new V2XException("Send failed for event type: " + eventType, e);
        }
    }
    
    /**
     * Sends a Cooperative Awareness Message (CAM).
     * CAM messages are broadcast periodically to inform other vehicles about the vehicle's presence,
     * position, speed, and basic status.
     * 
     * @param latitude Vehicle latitude in degrees
     * @param longitude Vehicle longitude in degrees
     * @param speed Vehicle speed in m/s
     * @param heading Vehicle heading in degrees (0-360)
     * @return true if the CAM was sent successfully
     * @throws V2XException if sending fails
     */
    public boolean sendCAM(double latitude, double longitude, double speed, double heading) 
            throws V2XException {
        logger.debug("Preparing CAM message: lat={}, lon={}, speed={} m/s, heading={} degrees",
                latitude, longitude, speed, heading);
        
        V2XEventPayload payload = V2XEventPayload.builder()
                .latitude(latitude)
                .longitude(longitude)
                .speed(speed)
                .heading(heading)
                .timestamp(System.currentTimeMillis())
                .build();
        
        return sendEvent("CAM", payload);
    }
    
    /**
     * Sends a Decentralized Environmental Notification Message (DENM).
     * DENM messages are event-triggered and used to alert other vehicles about hazardous
     * situations such as accidents, road works, or weather conditions.
     * 
     * @param latitude Event latitude in degrees
     * @param longitude Event longitude in degrees
     * @param eventType Type of hazard or event
     * @param severity Severity level (1-5, where 5 is most severe)
     * @return true if the DENM was sent successfully
     * @throws V2XException if sending fails
     */
    public boolean sendDENM(double latitude, double longitude, String eventType, int severity) 
            throws V2XException {
        logger.debug("Preparing DENM message: lat={}, lon={}, event={}, severity={}",
                latitude, longitude, eventType, severity);
        
        V2XEventPayload payload = V2XEventPayload.builder()
                .latitude(latitude)
                .longitude(longitude)
                .eventType(eventType)
                .severity(severity)
                .timestamp(System.currentTimeMillis())
                .build();
        
        return sendEvent("DENM", payload);
    }
    
    /**
     * Disconnects the sender from the V2X network.
     * Should be called when the application is shutting down.
     */
    public void disconnect() {
        if (isConnected) {
            logger.info("Disconnecting V2X Event Sender...");
            
            try {
                // TODO: Implement actual STEP SDK disconnect logic
                // Example (when SDK is available):
                // stepClient.disconnect();
                
                isConnected = false;
                logger.info("V2X Event Sender disconnected successfully");
                
            } catch (Exception e) {
                logger.error("Error during disconnect", e);
            }
        }
    }
    
    /**
     * Checks if the sender is currently connected.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }
}
