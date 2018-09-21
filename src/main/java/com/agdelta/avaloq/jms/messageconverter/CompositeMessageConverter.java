package com.agdelta.avaloq.jms.messageconverter;

import org.springframework.jms.JmsException;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class CompositeMessageConverter implements MessageConverter {

    private Set<MessageConverter> messageConverters;

    private CompositeMessageConverter(Set<MessageConverter> messageConverters) {
        this.messageConverters = messageConverters;
    }

    // ---------------------------------------------------------------------------------------
    //        org.springframework.jms.support.converter.MessageConverter Implementation
    // ---------------------------------------------------------------------------------------

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        Message message = null;
        LinkedList<MessageConversionException> exceptions = new LinkedList<>();

        for (Iterator<MessageConverter> iterator = messageConverters.iterator(); iterator.hasNext() && message == null;) {
            try {
                message = iterator.next().toMessage(object, session);
            } catch (MessageConversionException ex) {
                exceptions.add(ex);
            }
        }
        if (message == null)
            throw new MessageConversionException(
                    String.format("All attempts to convert %s to message failed due to %n%s", object,
                            exceptions.stream()
                                    .map(JmsException::getMessage)
                                    .reduce("", (s, s2) -> String.format("%s%n%s", s, s2))));
        return message;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        Object obj = null;
        LinkedList<MessageConversionException> exceptions = new LinkedList<>();
        for (Iterator<MessageConverter> iterator = messageConverters.iterator(); iterator.hasNext() && obj == null;) {
            try {
                obj = iterator.next().fromMessage(message);
            } catch (MessageConversionException ex) {
                exceptions.add(ex);
            }
        }
        if (obj == null)
            throw new MessageConversionException(
                    String.format("All attempts to convert from message %s failed due to %n%s", message,
                            exceptions.stream()
                                    .map(JmsException::getMessage)
                                    .reduce("", (s, s2) -> String.format("%s%n%s", s, s2))));
        return obj;
    }

    // ---------------------------------------------------------------------------------------
    //                                     Builder Section
    // ---------------------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final HashSet<MessageConverter> messageConverters = new HashSet<>();

        public Builder addMessageConverter(MessageConverter messageConverter) {
            messageConverters.add(messageConverter);
            return this;
        }

        public CompositeMessageConverter build() {
            return new CompositeMessageConverter(messageConverters);
        }
    }
}
