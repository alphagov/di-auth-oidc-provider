Feature: Login Journey
  User walks through a login journey

  Scenario: User is correctly prompted to login
    Given the services are running
    And the user has valid credentials
    When the user visit the stub relying party
    And the user clicks "govuk-signin-button"
    Then the user is taken to the Identity Provider Login Page
    When the user enters their email address
    Then the user is prompted for password

