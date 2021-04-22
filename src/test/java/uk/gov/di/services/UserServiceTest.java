package uk.gov.di.services;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    public void shouldValidateUserWithCorrectCredentials() {
        assertTrue(
                userService.isValidUser(
                        "joe.bloggs@digital.cabinet-office.gov.uk", "password"));
    }

    @Test
    public void shouldValidateUserWithUnknownUsername() {
        assertFalse(userService.isValidUser("unknown@nowhere", "password"));
    }

    @Test
    public void shouldValidateUserWithWrongPassword() {
        assertFalse(
                userService.isValidUser(
                        "joe.bloggs@digital.cabinet-office.gov.uk", "badPassword"));
    }

    @Test
    public void shouldValidateThatUserExists() {
        assertTrue(userService.userExists("joe.bloggs@digital.cabinet-office.gov.uk"));
    }

    @Test
    public void shouldValidateThatUserDoesNotExists() {
        assertFalse(userService.userExists("unknown@nowhere"));
    }

    @Test
    public void shouldValidateANewUser() {
        userService.addUser("newuser@example.com", "1234");

        assertTrue(userService.isValidUser("newuser@example.com", "1234"));
    }

    @Test
    public void shouldRetrieveUserInfoForEmail() {
        var userInfo = userService.getInfoForEmail("joe.bloggs@digital.cabinet-office.gov.uk");

        assertEquals(userInfo.getFamilyName(), "Bloggs");
        assertEquals(userInfo.getGivenName(), "Joe");
        assertEquals(userInfo.getEmailAddress(), "joe.bloggs@digital.cabinet-office.gov.uk");
        assertEquals(userInfo.getGender(), Gender.MALE);
    }
}
