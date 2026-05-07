Feature: Trainee profile management
  Scenario: Create trainee profile successfully
    Given a user with valid trainee information
    When the user sends a request to create a trainee profile
    Then the trainee profile is created
    And the response contains the trainee username

  Scenario: Fail to create trainee profile due to invalid data
    Given a user with invalid trainee information
    When the user sends a request to create a trainee profile
    Then the trainee profile is not created
    And the response contains a validation error

  Scenario: Get trainee profile successfully
    Given a trainee exists in the system
    When the user requests the trainee profile
    Then the system returns the trainee profile

  Scenario: Fail to get trainee profile when trainee does not exist
    Given a trainee does not exist in the system
    When the user requests the trainee profile
    Then the system returns not found error


  Scenario: Update trainee profile successfully
    Given a trainee with existing profile
    When the user updates the trainee profile with valid data
    Then the system updates the trainee profile


  Scenario: Delete trainee profile successfully
    Given a trainee exists in the system
    When the user deletes the trainee profile
    Then the trainee profile is removed


  Scenario: Get unassigned trainers successfully
    Given a trainee exists in the system
    When the user requests unassigned trainers
    Then the system returns list of trainers


  Scenario: Update trainee status to active
    Given a trainee is inactive
    When the user activates the trainee
    Then the trainee becomes active