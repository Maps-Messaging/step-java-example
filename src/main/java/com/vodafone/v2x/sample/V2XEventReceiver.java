package com.vodafone.v2x.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * V2XEventReceiver demonstrates how to receive V2X events using the Vodafone STEP SDK.
 * 
 * This class listens for incoming V2X messages from other vehicles and infrastructure,
 * processes them, and makes them available to the application.
 */
public class V2XEventReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(V2XEventReceiver.class);
    
    private final Object stepClient;
    private final AtomicBoolean isConnected;
    private final AtomicBoolean isListening;
    private final ConcurrentLinkedQueue<V2XEventPayload> receivedEvents;
    private final List<V2XEventListener> listeners;
    private Thread receiverThread;
    
    /**
     * Creates a new V2X Event Receiver.
     * 
     * @param stepClient The initialized STEP SDK client instance
     */
    public V2XEventReceiver(Object stepClient) {
        this.stepClient = stepClient;
        this.isConnected = new AtomicBoolean(false);
        this.isListening = new AtomicBoolean(false);
        this.receivedEvents = new ConcurrentLinkedQueue<>();
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Connects the receiver to the V2X network.
     * In a real implementation, this would establish a connection to the STEP platform.
     * 
     * @throws V2XException if connection fails
     */
    public void connect() throws V2XException {
        try {
            logger.info("Connecting V2X Event Receiver to STEP platform...");
            
            // TODO: Implement actual STEP SDK connection logic
            // Example (when SDK is available):
            // stepClient.connect();
            // stepClient.authenticate(credentials);
            
            isConnected.set(true);
            logger.info("V2X Event Receiver connected successfully");
            
        } catch (Exception e) {
            logger.error("Failed to connect V2X Event Receiver", e);
            throw new V2XException("Connection failed", e);
        }
    }
    
    /**
     * Starts listening for V2X events.
     * This method subscribes to the V2X message stream and begins processing incoming events.
     * 
     * @throws V2XException if starting the listener fails
     */
    public void startListening() throws V2XException {
        if (!isConnected.get()) {
            throw new V2XException("Receiver is not connected. Call connect() first.");
        }
        
        if (isListening.get()) {
            logger.warn("Receiver is already listening");
            return;
        }
        
        try {
            logger.info("Starting V2X event listener...");
            
            isListening.set(true);
            
            // Start the receiver thread
            receiverThread = new Thread(this::receiveLoop, "V2X-Receiver-Thread");
            receiverThread.setDaemon(true);
            receiverThread.start();
            
            // TODO: Implement actual STEP SDK subscription logic
            // Example (when SDK is available):
            // stepClient.subscribe(V2XMessageType.ALL, this::handleIncomingMessage);
            
            logger.info("V2X event listener started successfully");
            
        } catch (Exception e) {
            isListening.set(false);
            logger.error("Failed to start V2X event listener", e);
            throw new V2XException("Failed to start listening", e);
        }
    }
    
    /**
     * Main receive loop that processes incoming V2X events.
     * In a real implementation, this would receive events from the STEP SDK.
     */
    private void receiveLoop() {
        logger.debug("Receive loop started");
        
        while (isListening.get()) {
            try {
                // TODO: Implement actual STEP SDK receive logic
                // Example (when SDK is available):
                // V2XMessage message = stepClient.receive(timeout);
                // if (message != null) {
                //     V2XEventPayload payload = V2XEventPayload.fromBytes(message.getPayload());
                //     handleReceivedEvent(message.getType(), payload);
                // }
                
                // Simulated receive for demonstration
                Thread.sleep(1000);
                
            } catch (InterruptedException e) {
                logger.debug("Receiver thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in receive loop", e);
            }
        }
        
        logger.debug("Receive loop stopped");
    }
    
    /**
     * Handles a received V2X event.
     * 
     * @param eventType The type of V2X event received
     * @param payload The event payload
     */
    private void handleReceivedEvent(String eventType, V2XEventPayload payload) {
        logger.info("Received V2X event: type={}, payload={}", eventType, payload);
        
        receivedEvents.offer(payload);
        
        notifyListeners(eventType, payload);
    }
    
    /**
     * Simulates receiving a V2X event.
     * This method is for demonstration purposes only and should be replaced with actual
     * SDK integration.
     * 
     * @param eventType The type of event
     * @param payload The event payload
     */
    public void simulateReceivedEvent(String eventType, V2XEventPayload payload) {
        handleReceivedEvent(eventType, payload);
    }
    
    /**
     * Registers a listener for V2X events.
     * The listener will be notified whenever a new event is received.
     * 
     * @param listener The event listener to register
     */
    public void addListener(V2XEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        logger.debug("Event listener added: {}", listener.getClass().getSimpleName());
    }
    
    /**
     * Removes a previously registered listener.
     * 
     * @param listener The event listener to remove
     */
    public void removeListener(V2XEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
        logger.debug("Event listener removed: {}", listener.getClass().getSimpleName());
    }
    
    /**
     * Notifies all registered listeners about a received event.
     * 
     * @param eventType The type of event
     * @param payload The event payload
     */
    private void notifyListeners(String eventType, V2XEventPayload payload) {
        synchronized (listeners) {
            for (V2XEventListener listener : listeners) {
                try {
                    listener.onV2XEvent(eventType, payload);
                } catch (Exception e) {
                    logger.error("Error notifying listener: {}", listener.getClass().getSimpleName(), e);
                }
            }
        }
    }
    
    /**
     * Retrieves the next received event from the queue.
     * 
     * @return The next event payload, or null if no events are available
     */
    public V2XEventPayload pollEvent() {
        return receivedEvents.poll();
    }
    
    /**
     * Gets the number of events currently in the receive queue.
     * 
     * @return The number of pending events
     */
    public int getQueueSize() {
        return receivedEvents.size();
    }
    
    /**
     * Stops listening for V2X events.
     */
    public void stopListening() {
        if (isListening.get()) {
            logger.info("Stopping V2X event listener...");
            
            isListening.set(false);
            
            if (receiverThread != null) {
                receiverThread.interrupt();
                try {
                    receiverThread.join(5000);
                } catch (InterruptedException e) {
                    logger.warn("Interrupted while waiting for receiver thread to stop");
                    Thread.currentThread().interrupt();
                }
            }
            
            // TODO: Implement actual STEP SDK unsubscribe logic
            // Example (when SDK is available):
            // stepClient.unsubscribe();
            
            logger.info("V2X event listener stopped");
        }
    }
    
    /**
     * Disconnects the receiver from the V2X network.
     * Should be called when the application is shutting down.
     */
    public void disconnect() {
        stopListening();
        
        if (isConnected.get()) {
            logger.info("Disconnecting V2X Event Receiver...");
            
            try {
                // TODO: Implement actual STEP SDK disconnect logic
                // Example (when SDK is available):
                // stepClient.disconnect();
                
                isConnected.set(false);
                receivedEvents.clear();
                
                logger.info("V2X Event Receiver disconnected successfully");
                
            } catch (Exception e) {
                logger.error("Error during disconnect", e);
            }
        }
    }
    
    /**
     * Checks if the receiver is currently connected.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected.get();
    }
    
    /**
     * Checks if the receiver is currently listening for events.
     * 
     * @return true if listening, false otherwise
     */
    public boolean isListening() {
        return isListening.get();
    }
}
