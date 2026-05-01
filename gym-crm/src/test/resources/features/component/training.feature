Feature: Training management
  Scenario: Add training successfully
    Given a valid training request
    When the user submits a training creation request
    Then the training is added

  Scenario: Fail to add training due to invalid data
    Given an invalid training request
    When the user submits a training creation request
    Then the system rejects training creation


  Scenario: Get training types successfully
    Given training types exist in the system
    When the user requests training types
    Then the system returns training types list


  Scenario: Fail to delete training when not found
    Given training does not exist
    When the user deletes the training
    Then the system returns training not found error
