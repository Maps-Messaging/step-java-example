package com.vodafone.v2x.example.handlers;

import com.vodafone.v2xsdk4javav2.facade.events.BaseEvent;
import com.vodafone.v2xsdk4javav2.facade.events.EventListener;
import com.vodafone.v2xsdk4javav2.facade.events.EventType;
import com.vodafone.v2xsdk4javav2.facade.events.EventDenmListChanged;
import com.vodafone.v2xsdk4javav2.facade.records.denm.DENMRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DENMHandler implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(DENMHandler.class);
    private long ownStationId = -1;  // Will be set from first received message

    @Override
    public void onMessageBusEvent(BaseEvent baseEvent) {
        if (baseEvent.getEventType() == EventType.DENM_LIST_CHANGED) {
            EventDenmListChanged event = (EventDenmListChanged) baseEvent;

            for (DENMRecord denm : event.getList()) {
                // Detect own station ID from first message
                if (ownStationId == -1) {
                    ownStationId = denm.getOriginatorID();
                    logger.info("Detected own Station ID: {}", ownStationId);
                }

                // Check if this is our own message (echo)
                boolean isOwnMessage = (denm.getOriginatorID() == ownStationId);
                String origin = isOwnMessage ? "[OWN]" : "[OTHER]";

                logger.info("{} DENM - StationID: {}, SeqNum: {}, CauseCode: {}, SubCauseCode: {}",
                    origin,
                    denm.getOriginatorID(),
                    denm.getSequenceNumber(),
                    denm.getCauseCode(),
                    denm.getSubCauseCode());
            }
        }
    }
}
