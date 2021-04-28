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
import uk.gov.di.services.ClientService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
class ClientRegistrationResourceTest {

    private static final ClientService CLIENT_SERVICE = new ClientService(new ArrayList<>(), null);
    private static final ResourceExtension CLIENT_REGISTRATION_RESOURCE =
            ResourceExtension.builder()
                    .addResource(new ClientRegistrationResource(CLIENT_SERVICE))
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
    void shouldReturnGeneratedClientIdAndSecret() {
        MultivaluedMap<String, String> registrationResourceFormParams = new MultivaluedHashMap<>();
        registrationResourceFormParams.add("client_name", "whatever");
        registrationResourceFormParams.add("redirect_uris", "http://localhost");
        registrationResourceFormParams.add("contacts", "contact@example.com");
        final Response response = CLIENT_REGISTRATION_RESOURCE
                .target("/connect/register")
                .request()
                .post(Entity.form(registrationResourceFormParams));

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}