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
    
    @Override
    public void onMessageBusEvent(BaseEvent baseEvent) {
        if (baseEvent.getEventType() == EventType.CAM_LIST_CHANGED) {
            EventCamListChanged event = (EventCamListChanged) baseEvent;
            logger.info("Received {} CAM messages", event.getList().size());
            
            for (CAMRecord cam : event.getList()) {
                logger.info("  CAM from StationID: {}, Position: ({}, {}), Speed: {} km/h",
                    cam.getStationID(),
                    cam.getLatitude(),
                    cam.getLongitude(),
                    cam.getSpeedInKmH());
            }
        }
    }
}
