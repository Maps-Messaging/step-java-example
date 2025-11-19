package com.vodafone.v2x.example.handlers;

import com.vodafone.v2xsdk4javav2.facade.events.BaseEvent;
import com.vodafone.v2xsdk4javav2.facade.events.EventListener;
import com.vodafone.v2xsdk4javav2.facade.events.EventType;
import com.vodafone.v2xsdk4javav2.facade.events.EventCamListChanged;
import com.vodafone.v2xsdk4javav2.facade.records.cam.CAMRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CAMHandler implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(CAMHandler.class);
    private long ownStationId = -1;  // Will be set from first received message

    @Override
    public void onMessageBusEvent(BaseEvent baseEvent) {
        if (baseEvent.getEventType() == EventType.CAM_LIST_CHANGED) {
            EventCamListChanged event = (EventCamListChanged) baseEvent;

            for (CAMRecord cam : event.getList()) {
                // Detect own station ID from first message
                if (ownStationId == -1) {
                    ownStationId = cam.getStationID();
                    logger.info("Detected own Station ID: {}", ownStationId);
                }

                // Check if this is our own message (echo)
                boolean isOwnMessage = (cam.getStationID() == ownStationId);
                String origin = isOwnMessage ? "[OWN]" : "[OTHER]";

                logger.info("{} CAM - StationID: {}, Position: ({}, {}), Speed: {} km/h, Heading: {}°",
                    origin,
                    cam.getStationID(),
                    cam.getLatitude(),
                    cam.getLongitude(),
                    cam.getSpeedInKmH(),
                    cam.getHeadingInDegree());
            }
        }
    }
}
