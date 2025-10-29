package com.vodafone.v2x.sample;

/**
 * Listener interface for receiving V2X events.
 * 
 * Implement this interface to be notified when V2X events are received.
 */
public interface V2XEventListener {
    
    /**
     * Called when a V2X event is received.
     * 
     * @param eventType The type of V2X event (e.g., "CAM", "DENM")
     * @param payload The event payload containing event data
     */
    void onV2XEvent(String eventType, V2XEventPayload payload);
}
