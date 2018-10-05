package com.agdelta.avaloq.jms.messageconverter;

import com.prowidesoftware.swift.model.mt.AbstractMT;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;

public class SwiftMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        if (object instanceof AbstractMT)
            return session.createTextMessage(((AbstractMT) object).message());
        throw new MessageConversionException("Input is not SWIFT message");
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if (message instanceof TextMessage) {
            String text = ((TextMessage) message).getText();
            try {
                return AbstractMT.parse(text);
            } catch (IOException e) {
                throw new MessageConversionException(e.getMessage(), e);
            }
        }
        throw new MessageConversionException("JMS message content is not Text based");
    }
}
