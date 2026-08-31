Feature: Name registration visual feedback
  A person receives clear visual feedback when changing the locally registered-name list.

  Background:
    Given the name registration screen is open

  # Animate a new name 1: gives the created list item a visible arrival transition
  Scenario Outline: Animate a new name 1: gives the created list item a visible arrival transition
    When the person enters "<name>" as a new name
    And submits the new name
    Then "<name>" is shown in the registered-name list
    And the newly created "<name>" list item has a visible arrival transition

    Examples:
      | name  |
      | Ana   |
      | Bruno |

  # Highlight an edited name 2: draws attention to the saved replacement
  Scenario Outline: Highlight an edited name 2: draws attention to the saved replacement
    Given "<existing name>" is registered
    When the person edits "<existing name>" to "<updated name>"
    And saves the edit
    Then "<updated name>" is shown in the registered-name list
    And the updated "<updated name>" list item has a visible transient highlight

    Examples:
      | existing name | updated name |
      | Ana           | Beatriz      |
      | Bruno         | Caio         |

  # Transition a deleted name 3: visibly removes only the selected list item
  Scenario: Transition a deleted name 3: visibly removes only the selected list item
    Given "Ana" is registered
    And "Bruno" is registered
    When the person deletes "Ana"
    Then the "Ana" list item has a visible removal transition
    And "Ana" is not shown in the registered-name list after the transition
    And "Bruno" is shown in the registered-name list

  # Reduce motion 4: keeps CRUD feedback clear without nonessential motion
  Scenario: Reduce motion 4: keeps created, edited, and deleted names understandable
    Given the person has enabled reduced motion in their user environment
    And "Ana" is registered
    When the person edits "Ana" to "Beatriz"
    And saves the edit
    Then "Beatriz" is shown in the registered-name list
    And the update feedback uses no nonessential movement
