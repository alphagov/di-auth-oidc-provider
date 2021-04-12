package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/authorize")
public class AuthorisationResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response authorize(@Context UriInfo uriInfo) throws ParseException {
        boolean loggedIn = true;

        var authenticationRequest = AuthenticationRequest.parse(uriInfo.getRequestUri());

        if (loggedIn) {
            AuthenticationResponse response = handleAuthenticationRequest(authenticationRequest);
            return Response
                    .status(302)
                    .location(response.toSuccessResponse().toURI()).build();
        } else {
            return Response
                    .status(302)
                    .location(URI.create("/login"))
                    .build();
        }
    }

    public AuthenticationResponse handleAuthenticationRequest(AuthenticationRequest authenticationRequest) {
        return new AuthenticationSuccessResponse(
                authenticationRequest.getRedirectionURI(),
                new AuthorizationCode(),
                null,
                null,
                authenticationRequest.getState(),
                null,
                null
        );
    }
}
