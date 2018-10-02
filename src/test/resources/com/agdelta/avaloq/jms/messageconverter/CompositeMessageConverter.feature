Feature: Convert to simple JMS message
  IBM MQ return 'abc' and CompositeMessageConverter converts it to JMS Message

  Scenario Outline: IBM MQ return normal String message
    Given IBM MQ return <responsePayload>
    When MessageConverter converts message
    Then JMS Message with content message returned
    Examples:
    | responsePayload |
    | "avaloq"        |

  Scenario Outline: IBM MQ return valid JSON message
    Given IBM MQ return JSON <responsePayload>
    When MessageConverter converts JSON message
    Then JMS Message with content JSON message returned
    Examples:
    | responsePayload       |
    | "{'name': 'avaloq'}"  |

  Scenario Outline: IBM MQ return invalid JSON message then fallbacks as normal String message
    Given IBM MQ return invalid message <responsePayload>
    When MessageConverter converts invalid message
    Then JMS Message with content invalid message returned
    Examples:
      | responsePayload |
      | "{'name': }"    |