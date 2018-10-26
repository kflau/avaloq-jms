package com.agdelta.avaloq.jms;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class Receiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    private final String avaloqAcknowledgeCode;

    public Receiver(@Value("${com.agdelta.avaloq.jms.acknowledge.code:/acknowledge/msg/key_list/key/val}") String avaloqAcknowledgeCode) {
        this.avaloqAcknowledgeCode = avaloqAcknowledgeCode;
    }

    @JmsListener(destination = "AMI_OUT", containerFactory = "myFactory")
    public void receiveMessage(byte[] content) throws UnsupportedEncodingException, XPathExpressionException {
        String xml = StringEscapeUtils.unescapeXml(new String(content, StandardCharsets.UTF_8));
        LOGGER.debug("Result from AMI_OUT: {}", xml);

        XPath xPath = XPathFactory.newInstance().newXPath();
        String code = xPath.evaluate(avaloqAcknowledgeCode, new InputSource(new StringReader(xml)));
        if (code == null || code.isEmpty()) {
            LOGGER.debug("Instrument code from XPath [{}] sent by Avaloq is not found", avaloqAcknowledgeCode);
            return;
        }
        LOGGER.debug("Found instrument with code [{}]", code);
    }

    @JmsListener(destination = "AMI_SYNC_OUT", containerFactory = "myFactory")
    public void receiveMessageSync(byte[] content) throws UnsupportedEncodingException {
        LOGGER.info("Result from AMI_SYNC_OUT: {}", new String(content, StandardCharsets.UTF_8));
    }
}