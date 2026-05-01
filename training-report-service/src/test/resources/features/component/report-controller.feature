Feature: Report Controller component tests

  Scenario: Successfully handle trainer workload event
    When I send a valid trainer workload event
    Then the response status should be 200

  Scenario: Return trainer workload by username
    Given trainer workload exists for username "john.doe"
    When I request workload for username "john.doe"
    Then the response status should be 200
    And the response should contain trainer username "john.doe"

  Scenario: Return not found when trainer workload does not exist
    Given trainer workload does not exist for username "unknown"
    When I request workload for username "unknown"
    Then the response status should be 404

  Scenario: Return bad request when username is blank
    When I request workload with blank username
    Then the response status should be 400