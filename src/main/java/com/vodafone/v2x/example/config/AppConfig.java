package com.vodafone.v2x.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private String applicationId;
    private String applicationToken;
    private double testLatitude;
    private double testLongitude;
    
    public AppConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            props.load(input);
            this.applicationId = props.getProperty("app.id");
            this.applicationToken = props.getProperty("app.token");
            this.testLatitude = Double.parseDouble(props.getProperty("test.latitude", "48.866667"));
            this.testLongitude = Double.parseDouble(props.getProperty("test.longitude", "2.333333"));
        }
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public String getApplicationToken() {
        return applicationToken;
    }
    
    public double getTestLatitude() {
        return testLatitude;
    }
    
    public double getTestLongitude() {
        return testLongitude;
    }
}
