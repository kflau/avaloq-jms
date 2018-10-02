package com.agdelta.avaloq.jms.messageconverter;

import com.ibm.msg.client.jms.internal.JmsTextMessageImpl;
import cucumber.api.java8.En;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.Message;
import javax.jms.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompositeMessageConverterToJSONMessageSteps implements En {

    private String input;
    private MessageConverter messageConverter;
    private Session session;
    private JmsTextMessageImpl jmsTextMessage;
    private Message result;

    public CompositeMessageConverterToJSONMessageSteps() {
        Given("IBM MQ return JSON {string}", (String string) -> {
            input = string;
            messageConverter = mock(MappingJackson2MessageConverter.class);
            session = mock(Session.class);
            jmsTextMessage = mock(JmsTextMessageImpl.class);
        });

        When("MessageConverter converts JSON message", () -> {
            when(messageConverter.toMessage(any(String.class), any(Session.class))).thenReturn(jmsTextMessage);
            when(jmsTextMessage.getBody(String.class)).thenReturn(input);
            CompositeMessageConverter compositeMessageConverter =
                    CompositeMessageConverter.builder().addMessageConverter(messageConverter).build();
            result = compositeMessageConverter.toMessage(input, session);
        });

        Then("JMS Message with content JSON message returned", () -> {
            verify(messageConverter, times(1)).toMessage(any(String.class), any(Session.class));
            verifyNoMoreInteractions(messageConverter);
            assertThat(result).isNotNull();
            assertThat(result.getBody(String.class)).isEqualTo(input);
        });
    }
}
