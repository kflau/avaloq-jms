package com.agdelta.avaloq.jms.messageconverter;

import com.ibm.msg.client.jms.internal.JmsTextMessageImpl;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.Message;
import javax.jms.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompositeMessageConverterToJSONMessageStepDefs {

    private String input;
    private MessageConverter messageConverter;
    private Session session;
    private JmsTextMessageImpl jmsTextMessage;
    private Message result;

    @Given("^IBM MQ return \\{'name': 'avaloq'}$")
    public void ibm_MQ_return_name_avaloq_and_MessageConverter_is_SimpleMessageConverter() throws Exception {
        input = "{'name': 'avaloq'}";
        messageConverter = mock(MappingJackson2MessageConverter.class);
        session = mock(Session.class);
        jmsTextMessage = mock(JmsTextMessageImpl.class);
    }

    @When("^MessageConverter converts \\{'name': 'avaloq'}$")
    public void messageconverter_converts_name_avaloq() throws Exception {
        when(messageConverter.toMessage(any(String.class), any(Session.class))).thenReturn(jmsTextMessage);
        when(jmsTextMessage.getBody(String.class)).thenReturn(input);
        CompositeMessageConverter compositeMessageConverter =
                CompositeMessageConverter.builder().addMessageConverter(messageConverter).build();
        result = compositeMessageConverter.toMessage(input, session);
    }

    @Then("^JMS Message with content \\{'name': 'avaloq'} returned$")
    public void jms_Message_with_content_name_avaloq_returned() throws Exception {
        verify(messageConverter, times(1)).toMessage(any(String.class), any(Session.class));
        verifyNoMoreInteractions(messageConverter);
        assertThat(result).isNotNull();
        assertThat(result.getBody(String.class)).isEqualTo(input);
    }
}
