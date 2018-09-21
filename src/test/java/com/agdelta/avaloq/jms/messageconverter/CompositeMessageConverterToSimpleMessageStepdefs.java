package com.agdelta.avaloq.jms.messageconverter;

import com.ibm.msg.client.jms.internal.JmsTextMessageImpl;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.Message;
import javax.jms.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompositeMessageConverterToSimpleMessageStepdefs {

    private String input;
    private MessageConverter messageConverter;
    private Session session;
    private JmsTextMessageImpl jmsTextMessage;
    private Message result;

    @Given("^IBM MQ return 'avaloq'$")
    public void IBM_MQ_return_avaloq_and_MessageConverter_is_SimpleMessageConverter() throws Exception {
        input = "avaloq";
        messageConverter = mock(SimpleMessageConverter.class);
        session = mock(Session.class);
        jmsTextMessage = mock(JmsTextMessageImpl.class);
    }

    @When("^MessageConverter converts 'avaloq'$")
    public void tomessage_is_invoked() throws Exception {
        when(messageConverter.toMessage(any(String.class), any(Session.class))).thenReturn(jmsTextMessage);
        when(jmsTextMessage.getBody(String.class)).thenReturn(input);
        CompositeMessageConverter compositeMessageConverter =
                CompositeMessageConverter.builder().addMessageConverter(messageConverter).build();
        result = compositeMessageConverter.toMessage(input, session);
    }

    @Then("^JMS Message with content 'avaloq' returned$")
    public void jms_Message_with_content_avaloq_returned() throws Exception {
        verify(messageConverter, times(1)).toMessage(any(String.class), any(Session.class));
        verifyNoMoreInteractions(messageConverter);
        assertThat(result).isNotNull();
        assertThat(result.getBody(String.class)).isEqualTo(input);
    }
}
