Feature: Trainer profile management

  Scenario: Create trainer profile successfully
    Given a user provides valid trainer information
    When the user submits a trainer creation request
    Then the trainer profile is created
    And the system returns trainer credentials

  Scenario: Fail to create trainer profile when already exists
    Given a trainer already exists
    When the user submits a trainer creation request
    Then the system rejects trainer creation


  Scenario: Get trainer profile successfully
    Given a trainer exists in the system
    When the user requests the trainer profile
    Then the system returns the trainer profile

  Scenario: Fail to get trainer profile when not found
    Given a trainer does not exist
    When the user requests the trainer profile
    Then the system returns trainer not found error


  Scenario: Update trainer profile successfully
    Given a trainer with existing profile
    When the user updates the trainer profile
    Then the trainer profile is updated


  Scenario: Activate trainer
    Given a trainer is inactive
    When the user activates the trainer
    Then the trainer becomes active
