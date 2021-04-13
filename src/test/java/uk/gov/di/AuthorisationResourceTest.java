package uk.gov.di;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.configuration.OidcProviderConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthorisationResourceTest {

    private static DropwizardAppExtension<OidcProviderConfiguration> EXT = new DropwizardAppExtension<>(
            OidcProviderApplication.class,
            ResourceHelpers.resourceFilePath("oidc-provider.yml")
    );

    @Test
    public void shouldHandleCodeAuthenticationRequest() {
        Client client = EXT.client();

        Response response = client
                .property(ClientProperties.FOLLOW_REDIRECTS, false)
                .target(String.format("http://localhost:%d/authorize", EXT.getLocalPort()))
                .queryParam("client_id", "test")
                .queryParam("scope", "openid")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "http://example.com/login")
                .request()
                .get();

        assertEquals(HttpStatus.FOUND_302, response.getStatus());
        assertEquals("example.com", response.getLocation().getHost());
        assertEquals("/login", response.getLocation().getPath());
        assertTrue(response.getLocation().getQuery().startsWith("code="));
    }
}
