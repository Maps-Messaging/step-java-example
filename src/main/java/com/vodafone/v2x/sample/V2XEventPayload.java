package com.vodafone.v2x.sample;

/**
 * V2XEventPayload represents the data payload of a V2X event message.
 * 
 * This class encapsulates the common data elements found in V2X messages,
 * including position, speed, heading, and event-specific information.
 */
public class V2XEventPayload {
    
    private final double latitude;
    private final double longitude;
    private final double speed;
    private final double heading;
    private final String eventType;
    private final int severity;
    private final long timestamp;
    private final String additionalData;
    
    private V2XEventPayload(Builder builder) {
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.speed = builder.speed;
        this.heading = builder.heading;
        this.eventType = builder.eventType;
        this.severity = builder.severity;
        this.timestamp = builder.timestamp;
        this.additionalData = builder.additionalData;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getHeading() {
        return heading;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public int getSeverity() {
        return severity;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getAdditionalData() {
        return additionalData;
    }
    
    @Override
    public String toString() {
        return "V2XEventPayload{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", heading=" + heading +
                ", eventType='" + eventType + '\'' +
                ", severity=" + severity +
                ", timestamp=" + timestamp +
                ", additionalData='" + additionalData + '\'' +
                '}';
    }
    
    /**
     * Creates a new Builder for constructing V2XEventPayload instances.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for creating V2XEventPayload instances.
     */
    public static class Builder {
        private double latitude = 0.0;
        private double longitude = 0.0;
        private double speed = 0.0;
        private double heading = 0.0;
        private String eventType = "";
        private int severity = 0;
        private long timestamp = 0L;
        private String additionalData = "";
        
        /**
         * Sets the latitude in degrees.
         * 
         * @param latitude The latitude (-90 to 90)
         * @return This builder instance
         */
        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }
        
        /**
         * Sets the longitude in degrees.
         * 
         * @param longitude The longitude (-180 to 180)
         * @return This builder instance
         */
        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }
        
        /**
         * Sets the speed in meters per second.
         * 
         * @param speed The speed (>= 0)
         * @return This builder instance
         */
        public Builder speed(double speed) {
            this.speed = speed;
            return this;
        }
        
        /**
         * Sets the heading in degrees.
         * 
         * @param heading The heading (0-360, where 0 is North)
         * @return This builder instance
         */
        public Builder heading(double heading) {
            this.heading = heading;
            return this;
        }
        
        /**
         * Sets the event type (for DENM messages).
         * 
         * @param eventType The event type (e.g., "ACCIDENT", "ROAD_WORKS", "WEATHER")
         * @return This builder instance
         */
        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }
        
        /**
         * Sets the severity level.
         * 
         * @param severity The severity (1-5, where 5 is most severe)
         * @return This builder instance
         */
        public Builder severity(int severity) {
            this.severity = severity;
            return this;
        }
        
        /**
         * Sets the timestamp in milliseconds since epoch.
         * 
         * @param timestamp The timestamp
         * @return This builder instance
         */
        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets additional data.
         * 
         * @param additionalData Additional event data
         * @return This builder instance
         */
        public Builder additionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }
        
        /**
         * Builds the V2XEventPayload instance.
         * 
         * @return A new V2XEventPayload instance
         */
        public V2XEventPayload build() {
            return new V2XEventPayload(this);
        }
    }
}
