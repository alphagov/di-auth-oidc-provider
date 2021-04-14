package uk.gov.di.resources;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.services.UserValidationService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class LoginResourceTest {

    private static final UserValidationService userValidationService = mock(UserValidationService.class);
    private static final ResourceExtension loginResource = ResourceExtension.builder()
            .addResource(new LoginResource(userValidationService))
            .setClientConfigurator(clientConfig -> {
               clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
            })
            .addProvider(new ViewMessageBodyWriter(new MetricRegistry(), Collections.singleton(new MustacheViewRenderer())))
            .build();

    @BeforeAll
    static void setUp() {
        when(userValidationService.isValidUser(anyString(), anyString())).thenReturn(false);
        when(userValidationService.isValidUser(eq("joe.bloggs@digital.cabinet-office.gov.uk"), eq("password"))).thenReturn(true);
    }

    @Test
    void shouldDisplaySuccessfulViewIfSuccessfulLogin() {
        final Response response = loginRequest("joe.bloggs@digital.cabinet-office.gov.uk", "password");

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
        return loginResource
                .target("/login/validate")
                .request()
                .post(Entity.form(loginResourceFormParams));
    }

}
