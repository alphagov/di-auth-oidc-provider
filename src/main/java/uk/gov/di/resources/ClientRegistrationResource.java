package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.entity.Client;
import uk.gov.di.services.ClientService;
import uk.gov.di.views.ClientRegistrationView;
import uk.gov.di.views.SuccessfulClientRegistrationView;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Path("/connect")
public class ClientRegistrationResource {

    private ClientService clientService;
    private static final Logger LOG = LoggerFactory.getLogger(ClientRegistrationResource.class);
    private static final String CLIENT_ID = "admin_client_id";
    private static final String CLIENT_SECRET = "admin_client_secret";

    public ClientRegistrationResource(ClientService clientService) {
        this.clientService = clientService;
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
                new ResponseType(ResponseType.Value.CODE), new ClientID(CLIENT_ID))
                .scope(new Scope("openid", "profile", "email"))
                .state(new State())
                .redirectionURI(URI.create("http://localhost:8080/connect/callback"))
                .endpointURI(URI.create("/authorize"))
                .build();

        return Response.temporaryRedirect(authorizationRequest.toURI()).build();
    }

    @GET
    @Path("/callback")
    public Response oidcCallback(@QueryParam("code") String code) throws URISyntaxException, ParseException, IOException {
        var accessToken = getToken(code);
        var userInfo = getUserInfo(accessToken);

//        if (!allowedEmails.contains(userInfo.getEmailAddress())) {
//            return Response.temporaryRedirect(URI.create("/connect/notauthorised")).build();
//        }
        return Response.temporaryRedirect(URI.create("/connect/register")).cookie(
                new NewCookie(
                        "clientRegistrationCookie",
                        userInfo.getEmailAddress(),
                        "/",
                        null,
                        Cookie.DEFAULT_VERSION,
                        null,
                        NewCookie.DEFAULT_MAX_AGE,
                        false)
        ).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public View clientRegistration(@FormParam("client_name") @NotEmpty String clientName,
                                       @FormParam("redirect_uris") @NotEmpty List<String> redirectUris,
                                       @FormParam("contacts") @NotEmpty List<String> contacts) {

        Client client = clientService.addClient(clientName, redirectUris, contacts);

        return new SuccessfulClientRegistrationView(client);
    }

    private static UserInfo getUserInfo(AccessToken accessToken) throws IOException, URISyntaxException, ParseException {
        var httpResponse = new UserInfoRequest(URI.create("http://localhost:8080/userinfo"), new BearerAccessToken(accessToken.toString()))
                .toHTTPRequest()
                .send();

        var userInfoResponse = UserInfoResponse.parse(httpResponse);

        if (! userInfoResponse.indicatesSuccess()) {
            LOG.error("Userinfo request failed:" + userInfoResponse.toErrorResponse().getErrorObject().toString());
            throw new RuntimeException();
        }

        return userInfoResponse.toSuccessResponse().getUserInfo();
    }

    private static AccessToken getToken(String authcode) throws ParseException, IOException {
        var codeGrant = new AuthorizationCodeGrant(new AuthorizationCode(authcode), URI.create("http://localhost:8080/connect/callback"));

        var clientSecretPost = new ClientSecretPost(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET));
        var request = new TokenRequest(URI.create("http://localhost:8080/token"), clientSecretPost, codeGrant, new Scope("openid"));
        var tokenResponse = OIDCTokenResponseParser.parse(request.toHTTPRequest().send());

        if (! tokenResponse.indicatesSuccess()) {
            LOG.error("Token endpoint request failed:" + tokenResponse.toErrorResponse().getErrorObject().toString());
            throw new RuntimeException();
        }

        var successResponse = (OIDCTokenResponse)tokenResponse.toSuccessResponse();
        return successResponse.getOIDCTokens().getAccessToken();
    }
}
