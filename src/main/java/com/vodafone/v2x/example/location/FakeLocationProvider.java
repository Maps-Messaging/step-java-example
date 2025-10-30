package com.vodafone.v2x.example.location;

import com.vodafone.v2xsdk4javav2.facade.location.GnssLocation;
import com.vodafone.v2xsdk4javav2.facade.location.LocationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeLocationProvider extends LocationProvider implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FakeLocationProvider.class);
    
    private final double latitude;
    private final double longitude;
    private Thread locationThread;
    private volatile boolean running;
    
    public FakeLocationProvider(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.running = false;
    }
    
    @Override
    public void turnOn() {
        if (!running) {
            running = true;
            locationThread = new Thread(this, "FakeLocationProvider");
            locationThread.start();
            logger.info("Fake location provider started");
        }
    }
    
    @Override
    public void turnOff() {
        if (running) {
            running = false;
            if (locationThread != null) {
                try {
                    locationThread.interrupt();
                    locationThread.join(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Interrupted while stopping location provider");
                }
            }
            logger.info("Fake location provider stopped");
        }
    }
    
    @Override
    public void run() {
        logger.info("Location provider thread started - providing fixed coordinates: ({}, {})", 
                latitude, longitude);
        
        while (running) {
            try {
                long timestamp = System.currentTimeMillis();
                
                GnssLocation location = new GnssLocation(
                    latitude,
                    longitude,
                    null,
                    null,
                    null,
                    null,
                    timestamp
                );
                
                updateLocation(location);
                
                Thread.sleep(1000);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.debug("Location provider thread interrupted");
                break;
            } catch (Exception e) {
                logger.error("Error updating location", e);
            }
        }
        
        logger.info("Location provider thread stopped");
    }
}
