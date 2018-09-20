package com.agdelta.avaloq.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    @JmsListener(destination = "AMI_OUT", containerFactory = "myFactory")
    public void receiveMessage(String content) {
        LOGGER.info("Result from AMI_OUT: {}", content);
    }

    @JmsListener(destination = "AMI_SYNC_OUT", containerFactory = "myFactory")
    public void receiveMessageSync(String content) {
        LOGGER.info("Result from AMI_SYNC_OUT: {}", content);
    }
}