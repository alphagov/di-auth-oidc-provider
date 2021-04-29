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
import uk.gov.di.entity.Client;
import uk.gov.di.services.ClientService;
import uk.gov.di.services.UserService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class LoginResourceTest {

    private static final UserService USER_SERVICE = mock(UserService.class);
    private static final ClientService CLIENT_SERVICE = mock(ClientService.class);

    private static final ResourceExtension loginResource =
            ResourceExtension.builder()
                    .addResource(new LoginResource(USER_SERVICE, CLIENT_SERVICE))
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
    static void setUp() {
        when(USER_SERVICE.login(anyString(), anyString())).thenReturn(false);
        when(USER_SERVICE.login(eq("joe.bloggs@digital.cabinet-office.gov.uk"), eq("password")))
                .thenReturn(true);
        when(USER_SERVICE.userExists(anyString())).thenReturn(false);
        when(USER_SERVICE.userExists(eq("joe.bloggs@digital.cabinet-office.gov.uk")))
                .thenReturn(true);
        when(CLIENT_SERVICE.getClient(anyString())).thenReturn(
                Optional.of(new Client("Dummy Service",
                        "anything",
                        "anything",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of())

                ));
    }

    @Test
    void shouldDisplaySuccessfulViewIfSuccessfulLogin() {
        final Response response =
                loginRequest("joe.bloggs@digital.cabinet-office.gov.uk", "password");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(
                "joe.bloggs@digital.cabinet-office.gov.uk",
                response.getCookies().get("userCookie").getValue());
    }

    @Test
    void shouldRedirectBackToLoginPageIfFailedLogin() {
        final Response response =
                loginRequest("noone@nowhere.digical.cabinet-office.gov.uk", "blah");

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("/login", response.getLocation().getPath());
    }

    @Test
    void shouldDisplayPasswordScreenIfUserExists() {
        MultivaluedMap<String, String> loginResourceFormParams = new MultivaluedHashMap<>();
        loginResourceFormParams.add("authRequest", "whatever");
        loginResourceFormParams.add("email", "joe.bloggs@digital.cabinet-office.gov.uk");
        loginResourceFormParams.add("submit", "sign-in");

        final Response response =
                loginResource.target("/login").request().post(Entity.form(loginResourceFormParams));

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    void shouldRedirectToRegistrationScreenIfNonExistentUser() {
        MultivaluedMap<String, String> loginResourceFormParams = new MultivaluedHashMap<>();
        loginResourceFormParams.add("authRequest", "whatever");
        loginResourceFormParams.add("email", "notexists@digital.cabinet-office.gov.uk");

        final Response response =
                loginResource.target("/login").request().post(Entity.form(loginResourceFormParams));

        assertEquals(HttpStatus.SC_TEMPORARY_REDIRECT, response.getStatus());
        assertEquals("/registration", response.getLocation().getPath());
    }

    private Response loginRequest(String email, String password) {
        MultivaluedMap<String, String> loginResourceFormParams = new MultivaluedHashMap<>();
        loginResourceFormParams.add("authRequest", "client_id=whatever&response_type=code&redirect_uri=http://localhost&scope=openid&state=123456");
        loginResourceFormParams.add("email", email);
        loginResourceFormParams.add("password", password);
        return loginResource
                .target("/login/validate")
                .request()
                .post(Entity.form(loginResourceFormParams));
    }
}
