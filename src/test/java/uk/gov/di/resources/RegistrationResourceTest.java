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
import org.mockito.Mock;
import uk.gov.di.services.UserService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(DropwizardExtensionsSupport.class)
class RegistrationResourceTest {

    private static final UserService USER_SERVICE = mock(UserService.class);

    private static final ResourceExtension REGISTRATION_RESOURCE =
            ResourceExtension.builder()
                    .addResource(new RegistrationResource(USER_SERVICE))
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
    void shouldSucceedIfPasswordsMatch() {
        Response response = setPasswordRequest("newuser@example.com", "reallysecure1234", "reallysecure1234");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("newuser@example.com", response.getCookies().get("userCookie").getValue());
        verify(USER_SERVICE).addUser(eq("newuser@example.com"), eq("reallysecure1234"));
    }

    @Test
    void shouldDisplayErrorIfPasswordsDoNotMatch() {
        Response response = setPasswordRequest("", "reallysecure1234", "notmatchingpassword");

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }

    private Response setPasswordRequest(String email, String password, String passwordConfirm) {
        MultivaluedMap<String, String> setPasswordResourceFormParams = new MultivaluedHashMap<>();
        setPasswordResourceFormParams.add("authRequest", "whatever");
        setPasswordResourceFormParams.add("email", email);
        setPasswordResourceFormParams.add("password", password);
        setPasswordResourceFormParams.add("password-confirm", passwordConfirm);
        return REGISTRATION_RESOURCE
                .target("/registration/validate")
                .request()
                .post(Entity.form(setPasswordResourceFormParams));
    }
}