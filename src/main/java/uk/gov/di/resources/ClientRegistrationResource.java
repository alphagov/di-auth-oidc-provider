package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import io.dropwizard.views.View;
import org.apache.http.HttpStatus;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.entity.Client;
import uk.gov.di.services.ClientService;
import uk.gov.di.views.ClientNotAuthorisedView;
import uk.gov.di.views.ClientRegistrationView;
import uk.gov.di.views.SuccessfulClientRegistrationView;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/connect")
public class ClientRegistrationResource {

    private ClientService clientService;
    private OidcProviderConfiguration config;

    public ClientRegistrationResource(ClientService clientService, OidcProviderConfiguration config) {
        this.clientService = clientService;
        this.config = config;
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public Response clientRegistration(@CookieParam("clientRegistrationCookie") Optional<String> user) {
        boolean loggedIn = user.isPresent();
        if (loggedIn) {
            return Response.ok(new ClientRegistrationView()).build();
        }
        var authorizationRequest = new AuthorizationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE), new ClientID(config.getClientId()))
                .scope(new Scope("openid", "profile", "email"))
                .state(new State())
                .redirectionURI(URI.create("http://localhost:8080/client/callback"))
                .endpointURI(URI.create("/authorize"))
                .build();

        return Response.status(HttpStatus.SC_MOVED_TEMPORARILY).location(authorizationRequest.toURI()).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public View clientRegistration(@FormParam("client_name") @NotEmpty String clientName,
                                       @FormParam("redirect_uris") @NotEmpty List<String> redirectUris,
                                       @FormParam("contacts") @NotEmpty List<String> contacts,
                                       @CookieParam("clientRegistrationCookie") Optional<String> user) {

        boolean loggedIn = user.isPresent();
        if (loggedIn) {
            Client client = clientService.addClient(clientName, redirectUris, contacts);

            return new SuccessfulClientRegistrationView(client);
        }
        return new ClientNotAuthorisedView();
    }

    @GET
    @Path("/notauthorised")
    @Produces(MediaType.TEXT_HTML)
    public View clientRegistration() {

        return new ClientNotAuthorisedView();
    }


    @POST
    @Path("/logout")
    public Response logout() {
        return Response.status(HttpStatus.SC_MOVED_TEMPORARILY).location(URI.create("/logout?redirectUri=/connect/register")).cookie(new NewCookie(
                "clientRegistrationCookie",
                null,
                "/",
                null,
                Cookie.DEFAULT_VERSION,
                null,
                0,
                false))
                .build();
    }
}
