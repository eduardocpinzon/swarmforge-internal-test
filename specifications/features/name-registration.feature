Feature: Local name registration
  A person can maintain a locally saved list of names from the registration screen.

  Background:
    Given the name registration screen is open

  # Add a name 1: saves a submitted name in the visible list
  Scenario Outline: Add a name 1: saves a submitted name in the visible list
    When the person enters "<name>" as a new name
    And submits the new name
    Then "<name>" is shown in the registered-name list
    And the new-name input is empty

    Examples:
      | name  |
      | Ana   |
      | Bruno |

  # Edit a name 2: replaces the selected saved name
  Scenario Outline: Edit a name 2: replaces the selected saved name
    Given "<existing name>" is registered
    When the person edits "<existing name>" to "<updated name>"
    And saves the edit
    Then "<updated name>" is shown in the registered-name list
    And "<existing name>" is not shown in the registered-name list

    Examples:
      | existing name | updated name |
      | Ana           | Beatriz      |
      | Bruno         | Caio         |

  # Delete a name 3: removes the selected saved name
  Scenario: Delete a name 3: removes the selected saved name
    Given "Ana" is registered
    And "Bruno" is registered
    When the person deletes "Ana"
    Then "Ana" is not shown in the registered-name list
    And "Bruno" is shown in the registered-name list

  # Retain a name 4: shows saved names after reopening the screen
  Scenario: Retain a name 4: shows saved names after reopening the screen
    Given "Ana" is registered
    When the person reopens the name registration screen
    Then "Ana" is shown in the registered-name list

  # Reject an empty name 5: keeps the list unchanged
  Scenario: Reject an empty name 5: keeps the list unchanged
    Given "Ana" is registered
    When the person submits an empty new name
    Then a validation message explains that a name is required
    And the registered-name list contains only "Ana"
