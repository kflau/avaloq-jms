package com.agdelta.avaloq.jms.messageconverter;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

public class CompositeMessageConverterStepdefs {

    private String input;
    private MessageConverter messageConverter;

    @Given("^Input is 'abc' and MessageConverter is SimpleMessageConverter$")
    public void input_is_abc_and_MessageConverter_is_SimpleMessageConverter() throws Exception {
        input = "abc";
        messageConverter = new SimpleMessageConverter();
    }

    @When("^toMessage is invoked$")
    public void tomessage_is_invoked() throws Exception {
        CompositeMessageConverter compositeMessageConverter =
                CompositeMessageConverter.builder().addMessageConverter(messageConverter).build();
        
    }

    @Then("^JMS Message with content 'abc' returned$")
    public void jms_Message_with_content_abc_returned() throws Exception {

    }
}
