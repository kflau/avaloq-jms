package com.agdelta.avaloq.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class Receiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    @JmsListener(destination = "AMI_OUT", containerFactory = "myFactory")
    public void receiveMessage(byte[] content) throws UnsupportedEncodingException {
        LOGGER.info("Result from AMI_OUT: {}", new String(content, "UTF-8"));
    }

    @JmsListener(destination = "AMI_SYNC_OUT", containerFactory = "myFactory")
    public void receiveMessageSync(byte[] content) throws UnsupportedEncodingException {
        LOGGER.info("Result from AMI_SYNC_OUT: {}", new String(content, "UTF-8"));
    }
}