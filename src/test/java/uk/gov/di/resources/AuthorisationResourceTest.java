package uk.gov.di.resources;

import com.codahale.metrics.MetricRegistry;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCError;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.helpers.AuthenticationResponseHelper;
import uk.gov.di.services.ClientService;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthorisationResourceTest {

    private static final ClientService clientService = mock(ClientService.class);

    private static final ResourceExtension authorizationResource =
            ResourceExtension.builder()
                    .addResource(new AuthorisationResource(clientService))
                    .setClientConfigurator(
                            clientConfig -> {
                                clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
                            })
                    .addProvider(
                            new ViewMessageBodyWriter(
                                    new MetricRegistry(),
                                    Collections.singleton(new MustacheViewRenderer())))
                    .build();

    @BeforeAll
    public static void setUp() {
        when(clientService.validateAuthorizationRequest(any()))
                .thenReturn(
                        AuthenticationResponseHelper.generateSuccessfulAuthResponse(
                                new AuthenticationRequest.Builder(
                                                new ResponseType("code"),
                                                new Scope("openid"),
                                                new ClientID("test"),
                                                URI.create("http://example.com/login-code"))
                                        .build()));
    }

    @Test
    public void shouldProvideCodeAuthenticationRequestWhenLoggedIn() {
        Response response = authorisationRequestBuilder().cookie("userCookie", "dummy-value").get();

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

    @Test
    public void shouldReturnErrorResponseWhenReceivingInvalidAuthRequest() {
        when(clientService.validateAuthorizationRequest(any()))
                .thenReturn(
                        AuthenticationResponseHelper.generateErrorAuthnResponse(
                                new AuthenticationRequest.Builder(
                                                new ResponseType("code"),
                                                new Scope("openid"),
                                                new ClientID("test"),
                                                URI.create("http://example.com/login-code"))
                                        .build(),
                                OIDCError.UNMET_AUTHENTICATION_REQUIREMENTS));
        Response response = authorisationRequestBuilder().get();

        assertEquals(HttpStatus.FOUND_302, response.getStatus());
        assertEquals("example.com", response.getLocation().getHost());
        assertEquals("/login-code", response.getLocation().getPath());
        assertTrue(
                response.getLocation()
                        .getQuery()
                        .contains("error=unmet_authentication_requirements"));
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
