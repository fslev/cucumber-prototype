Feature: Polling for results

  @Ignore
  Scenario: Guess the number
    Given interval limits 3 and 12
    Then poll until random generated number 8 is found