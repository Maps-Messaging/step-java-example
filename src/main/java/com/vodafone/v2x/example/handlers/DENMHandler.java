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
    
    @Override
    public void onMessageBusEvent(BaseEvent baseEvent) {
        if (baseEvent.getEventType() == EventType.DENM_LIST_CHANGED) {
            EventDenmListChanged event = (EventDenmListChanged) baseEvent;
            logger.info("Received {} DENM messages", event.getList().size());
            
            for (DENMRecord denm : event.getList()) {
                logger.info("  DENM from StationID: {}, CauseCode: {}, SubCauseCode: {}",
                    denm.getOriginatorID(),
                    denm.getCauseCode(),
                    denm.getSubCauseCode());
            }
        }
    }
}
