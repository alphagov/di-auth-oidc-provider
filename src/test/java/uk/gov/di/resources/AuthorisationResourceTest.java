package uk.gov.di.resources;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthorisationResourceTest {

    private static final ResourceExtension authorizationResource = ResourceExtension.builder()
            .addResource(new AuthorisationResource())
            .setClientConfigurator(clientConfig -> {
                clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
            })
            .addProvider(new ViewMessageBodyWriter(new MetricRegistry(), Collections.singleton(new MustacheViewRenderer())))
            .build();

    @Test
    public void shouldProvideCodeAuthenticationRequestWhenLoggedIn() {
        Response response = authorisationRequestBuilder()
                .cookie("userCookie", "dummy-value")
                .get();

        assertEquals(HttpStatus.FOUND_302, response.getStatus());
        assertEquals("example.com", response.getLocation().getHost());
        assertEquals("/login-code", response.getLocation().getPath());
        assertTrue(response.getLocation().getQuery().startsWith("code="));
    }

    @Test
    public void shouldRedirectAuthenticationRequestToLoginPageIfNotLoggedIn() {
        Response response = authorisationRequestBuilder().get();

        assertEquals(HttpStatus.FOUND_302, response.getStatus());
        assertEquals("localhost", response.getLocation().getHost());
        assertEquals("/login", response.getLocation().getPath());
    }

    private Invocation.Builder authorisationRequestBuilder() {
        return authorizationResource
                .target("/authorize")
                .queryParam("client_id", "test")
                .queryParam("scope", "openid")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "http://example.com/login-code")
                .request();
    }
}
