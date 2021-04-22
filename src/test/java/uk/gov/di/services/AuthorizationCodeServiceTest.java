package uk.gov.di.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthorizationCodeServiceTest {

    private static AuthorizationCodeService authorizationCodeService = new AuthorizationCodeService();

    @Test
    void shouldIssueAndStoreCodeForUser() {
        var code = authorizationCodeService.issueCodeForUser("user@example.com");

        assertEquals("user@example.com", authorizationCodeService.getEmailForCode(code));
    }

    @Test
    void shouldOnlyAllowRetrievalOfCodeOnce() {
        var code = authorizationCodeService.issueCodeForUser("user@example.com");

        assertEquals("user@example.com", authorizationCodeService.getEmailForCode(code));
        assertNull(authorizationCodeService.getEmailForCode(code));
    }
}