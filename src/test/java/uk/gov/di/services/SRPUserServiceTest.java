package uk.gov.di.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SRPUserServiceTest {

    private static final SRPUserService SRP_USER_SERVICE = new SRPUserService();

    @BeforeAll
    static void setup() {
        SRP_USER_SERVICE.signUp("joe.bloggs@digital.cabinet-office.gov.uk", "password");
    }

    @Test
    void shouldAuthenticateWithValidCredentials() {
        assertTrue(SRP_USER_SERVICE.login("joe.bloggs@digital.cabinet-office.gov.uk", "password"));
    }

    @Test
    void shouldFailWithIncorrectPassword() {
        assertFalse(SRP_USER_SERVICE.login("joe.bloggs@digital.cabinet-office.gov.uk", "not-correct"));
    }

}