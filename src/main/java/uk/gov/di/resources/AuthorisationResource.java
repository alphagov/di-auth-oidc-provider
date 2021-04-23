package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import org.eclipse.jetty.http.HttpStatus;
import uk.gov.di.helpers.AuthenticationResponseHelper;
import uk.gov.di.services.ClientService;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Optional;

@Path("/authorize")
public class AuthorisationResource {

    private ClientService clientService;

    public AuthorisationResource(ClientService clientService) {
        this.clientService = clientService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response authorize(
            @Context UriInfo uriInfo, @CookieParam("userCookie") Optional<String> email)
            throws ParseException, RuntimeException {
        boolean loggedIn = email.isPresent();

        var authRequest = AuthenticationRequest.parse(uriInfo.getRequestUri());

        Optional<ErrorObject> error = clientService.getErrorForAuthorizationRequest(authRequest);

        if (error.isPresent()) {
            AuthenticationResponse response =
                    AuthenticationResponseHelper.generateErrorAuthnResponse(
                            authRequest, error.get());
            return Response.status(HttpStatus.MOVED_TEMPORARILY_302)
                    .location(response.toErrorResponse().toURI())
                    .build();
        }

        if (loggedIn) {
            AuthenticationResponse response =
                    clientService.getSuccessfulResponse(authRequest, email.get());
            return Response.status(HttpStatus.MOVED_TEMPORARILY_302)
                    .location(response.toSuccessResponse().toURI())
                    .build();
        } else {
            return Response.status(HttpStatus.MOVED_TEMPORARILY_302)
                    .location(
                            UriBuilder.fromUri(URI.create("/login"))
                                    .queryParam("authRequest", authRequest.toQueryString())
                                    .build())
                    .build();
        }
    }
}
