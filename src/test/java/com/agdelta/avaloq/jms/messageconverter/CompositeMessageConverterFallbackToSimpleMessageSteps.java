package com.agdelta.avaloq.jms.messageconverter;

import com.ibm.msg.client.jms.internal.JmsTextMessageImpl;
import cucumber.api.java8.En;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.Message;
import javax.jms.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompositeMessageConverterFallbackToSimpleMessageSteps implements En {

    private String input;
    private SimpleMessageConverter simpleMessageConverter;
    private MappingJackson2MessageConverter mappingJackson2MessageConverter;
    private Session session;
    private JmsTextMessageImpl jmsTextMessage;
    private Message result;

    public CompositeMessageConverterFallbackToSimpleMessageSteps() {
        Given("IBM MQ return invalid message {string}", (String string) -> {
            input = string;
            simpleMessageConverter = mock(SimpleMessageConverter.class);
            mappingJackson2MessageConverter = mock(MappingJackson2MessageConverter.class);
            session = mock(Session.class);
            jmsTextMessage = mock(JmsTextMessageImpl.class);
        });

        When("MessageConverter converts invalid message", () -> {
            when(mappingJackson2MessageConverter.toMessage(any(String.class), any(Session.class))).thenThrow(MessageConversionException.class);
            when(simpleMessageConverter.toMessage(any(String.class), any(Session.class))).thenReturn(jmsTextMessage);
            when(jmsTextMessage.getBody(String.class)).thenReturn(input);
            CompositeMessageConverter compositeMessageConverter =
                    CompositeMessageConverter.builder()
                            .addMessageConverter(mappingJackson2MessageConverter)
                            .addMessageConverter(simpleMessageConverter)
                            .build();
            result = compositeMessageConverter.toMessage(input, session);
        });

        Then("JMS Message with content invalid message returned", () -> {
            verify(mappingJackson2MessageConverter, times(1)).toMessage(any(String.class), any(Session.class));
            verify(simpleMessageConverter, times(1)).toMessage(any(String.class), any(Session.class));
            verifyNoMoreInteractions(mappingJackson2MessageConverter, simpleMessageConverter);
            assertThat(result).isNotNull();
            assertThat(result.getBody(String.class)).isEqualTo(input);
        });
    }
}
