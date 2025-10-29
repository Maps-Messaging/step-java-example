package com.vodafone.v2x.sample;

/**
 * Exception thrown when V2X operations fail.
 */
public class V2XException extends Exception {
    
    /**
     * Creates a new V2XException with the specified message.
     * 
     * @param message The error message
     */
    public V2XException(String message) {
        super(message);
    }
    
    /**
     * Creates a new V2XException with the specified message and cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public V2XException(String message, Throwable cause) {
        super(message, cause);
    }
}
