Feature: Convert to simple JMS message
  IBM MQ return 'abc' and CompositeMessageConverter converts it to JMS Message

  Scenario: IBM MQ return normal String message
    Given IBM MQ return 'avaloq'
    When MessageConverter converts 'avaloq'
    Then JMS Message with content 'avaloq' returned

  Scenario: IBM MQ return valid JSON message
    Given IBM MQ return {'name': 'avaloq'}
    When MessageConverter converts {'name': 'avaloq'}
    Then JMS Message with content {'name': 'avaloq'} returned

  Scenario: IBM MQ return invalid JSON message then fallbacks as normal String message
    Given IBM MQ return {'name': }
    When MessageConverter converts {'name': }
    Then JMS Message with content {'name': } returned