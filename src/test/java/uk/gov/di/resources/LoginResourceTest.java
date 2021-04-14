package uk.gov.di.resources;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.OidcProviderApplication;
import uk.gov.di.configuration.OidcProviderConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
public class LoginResourceTest {

    private static DropwizardAppExtension<OidcProviderConfiguration> EXT = new DropwizardAppExtension<>(
            OidcProviderApplication.class,
            ResourceHelpers.resourceFilePath("oidc-provider.yml")
    );

    @Test
    void shouldDisplaySuccessfulViewIfSuccessfulLogin() {
        final Response response = loginRequest("test@digital.cabinet-office.gov.uk", "password");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    void shouldRedirectBackToLoginPageIfFailedLogin() {
        final Response response = loginRequest("noone@nowhere.digical.cabinet-office.gov.uk", "blah");

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("/login", response.getLocation().getPath());
    }

    private Response loginRequest(String email, String password) {
        MultivaluedMap<String, String> loginResourceFormParams = new MultivaluedHashMap<>();
        loginResourceFormParams.add("authRequest", "whatever");
        loginResourceFormParams.add("email", email);
        loginResourceFormParams.add("password", password);

        Client client = EXT.client();

        return client
                .property(ClientProperties.FOLLOW_REDIRECTS, false)
                .target(String.format("http://localhost:%d/login/validate", EXT.getLocalPort()))
                .request()
                .post(Entity.form(loginResourceFormParams));
    }

}
