Feature: Login Journey
  User walks through a login journey

  Scenario: User is correctly prompted to login
    Given The services are running
    And has not signed into the IDP
    When the user visit the stub relying party
    And the user clicks "govuk-signin-button"
    Then The user is taken to the Identity Provider Login Page

