Feature: Convert to simple JMS message
  Input an object and convert it to JMS Message

  Scenario: Convert 'abc' to JMS Message
    Given Input is 'abc' and MessageConverter is SimpleMessageConverter
    When toMessage is invoked
    Then JMS Message with content 'abc' returned