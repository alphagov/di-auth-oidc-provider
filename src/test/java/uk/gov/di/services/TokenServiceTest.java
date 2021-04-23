package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.junit.jupiter.api.Test;
import uk.gov.di.configuration.OidcProviderConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenServiceTest {

    private final TokenService tokenService = new TokenService(new OidcProviderConfiguration());

    @Test
    public void shouldAssociateCreatedTokenWithEmailAddress() {
        AccessToken token = tokenService.issueToken("test@digital.cabinet-office.gov.uk");

        assertEquals("test@digital.cabinet-office.gov.uk", tokenService.getEmailForToken(token));
    }
}
