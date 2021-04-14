package uk.gov.di.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationServiceTest {

    private final UserValidationService userValidationService = new UserValidationService();

    @Test
    public void shouldValidateUserWithCorrectCredentials() {
        assertTrue(userValidationService.isValidUser("test@digital.cabinet-office.gov.uk", "password"));
    }

    @Test
    public void shouldValidateUserWithUnknownUsername() {
        assertFalse(userValidationService.isValidUser("unknown@nowhere", "password"));
    }

    @Test
    public void shouldValidateUserWithWrongPassword() {
        assertFalse(userValidationService.isValidUser("test@digital.cabinet-office.gov.uk", "badPassword"));
    }

}