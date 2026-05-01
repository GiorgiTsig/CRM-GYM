Feature: Training creation integration flow

  Scenario: Successfully create training and publish workload event
    Given a trainer with username "john.trainer" exists
    And a trainee with username "john.trainee" exists

    When I send POST request to "/training" with valid training data

    Then the response status should be 200
    And the training should be saved in gym-crm database

  Scenario: Fail to create training with invalid trainee
    Given a trainer with username "john.trainer" exists

    When I send POST request to "/training" with non-existing trainee username

    Then the response status should be 400
    And the training should not be saved in the database