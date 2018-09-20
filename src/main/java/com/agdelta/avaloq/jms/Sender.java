package com.agdelta.avaloq.jms;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Sender implements ApplicationRunner {

    private final JmsTemplate jmsTemplate;

    public Sender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = now.format(formatter);

        jmsTemplate.convertAndSend("AMI_IN", String.format("AMI_IN %s", formatDateTime));
        jmsTemplate.convertAndSend("AMI_SYNC_IN", String.format("AMI_SYNC_IN %s", formatDateTime));
    }
}
