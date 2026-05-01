Feature: Training workload processing integration flow

  Scenario: Successfully consume workload event and update trainer summary
    Given a valid workload event is sent to the workload queue "John.Trainer"

    When training-report-service processes the queue message "John.Trainer"

    Then trainer workload should be updated in the database "John.Trainer"

  Scenario: Ignore invalid workload event
    Given an invalid workload event is sent to the workload queue

    When training-report-service processes the queue message

    Then trainer workload for "john.trainer" should not be updated
