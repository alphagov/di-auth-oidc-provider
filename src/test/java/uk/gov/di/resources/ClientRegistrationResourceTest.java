package uk.gov.di.resources;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.services.ClientConfigService;
import uk.gov.di.services.ClientService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
class ClientRegistrationResourceTest {

    private static final ClientConfigService CLIENT_CONFIG_SERVICE = mock(ClientConfigService.class);
    private static final OidcProviderConfiguration configuration = mock(OidcProviderConfiguration.class);
    private static final ClientService CLIENT_SERVICE = new ClientService(new ArrayList<>(), null, CLIENT_CONFIG_SERVICE);
    private static final ResourceExtension CLIENT_REGISTRATION_RESOURCE =
            ResourceExtension.builder()
                    .addResource(new ClientRegistrationResource(CLIENT_SERVICE, configuration))
                    .setClientConfigurator(
                            clientConfig -> {
                                clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
                            })
                    .addProvider(
                            new ViewMessageBodyWriter(
                                    new MetricRegistry(),
                                    Collections.singleton(new MustacheViewRenderer())))
                    .build();

    @Test
    void shouldSucceedInRegisteringService() {
        MultivaluedMap<String, String> registrationResourceFormParams = new MultivaluedHashMap<>();
        registrationResourceFormParams.add("client_name", "whatever");
        registrationResourceFormParams.add("redirect_uris", "http://localhost");
        registrationResourceFormParams.add("contacts", "contact@example.com");
        final Response response = CLIENT_REGISTRATION_RESOURCE
                .target("/connect/register")
                .request()
                .cookie("clientRegistrationCookie", "dummy-value")
                .post(Entity.form(registrationResourceFormParams));

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    void shouldBeUnauthorizedToRegisterService() {
        MultivaluedMap<String, String> registrationResourceFormParams = new MultivaluedHashMap<>();
        registrationResourceFormParams.add("client_name", "whatever");
        registrationResourceFormParams.add("redirect_uris", "http://localhost");
        registrationResourceFormParams.add("contacts", "contact@example.com");
        final Response response = CLIENT_REGISTRATION_RESOURCE
                .target("/connect/register")
                .request()
                .post(Entity.form(registrationResourceFormParams));

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void shouldGenerateAuthRequestIfNotLoggedIn() {
        when(configuration.getClientId()).thenReturn("client-id");
        when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8080"));
        final Response response = CLIENT_REGISTRATION_RESOURCE
                .target("/connect/register")
                .request()
                .get();

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("/authorize", response.getLocation().getPath());
    }

    @Test
    void shouldReturn200IfUserIsLoggedIn() {
        final Response response = CLIENT_REGISTRATION_RESOURCE
                .target("/connect/register")
                .request()
                .cookie("clientRegistrationCookie", "dummy-value")
                .get();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }
}