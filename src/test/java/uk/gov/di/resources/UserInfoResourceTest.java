package uk.gov.di.resources;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.services.TokenService;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(DropwizardExtensionsSupport.class)
class UserInfoResourceTest {

    private static final TokenService tokenService = mock(TokenService.class);
    private static final ResourceExtension userInfoExtension =
            ResourceExtension.builder().addResource(new UserInfoResource(tokenService)).build();

    @Test
    void shouldReturnUnauthorisedIfNoHeaderPresent() {
        final Response response = userInfoExtension.target("/userinfo").request().get();

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void shouldReturnUserDataIfAuthorisationHeaderPresent() {
        final Response response =
                userInfoExtension
                        .target("/userinfo")
                        .request()
                        .header("Authorization", "Bearer sometoken")
                        .get();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.hasEntity());
    }
}
