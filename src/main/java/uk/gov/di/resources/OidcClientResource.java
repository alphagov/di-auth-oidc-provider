package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.services.ClientConfigService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

@Path("/client")
public class OidcClientResource {

    private OidcProviderConfiguration config;
    private static final Logger LOG = LoggerFactory.getLogger(OidcClientResource.class);
    private ClientConfigService clientConfigService;

    public OidcClientResource(OidcProviderConfiguration config, ClientConfigService clientConfigService) {
        this.config = config;
        this.clientConfigService = clientConfigService;
    }

    @GET
    @Path("/callback")
    public Response oidcCallback(@QueryParam("code") String code) throws ParseException, IOException {
        var accessToken = getToken(code);
        var userInfo = getUserInfo(accessToken);

        if (!clientConfigService.isAuthorisedToRegisterClients(userInfo.getEmailAddress())) {
            return Response.temporaryRedirect(URI.create("/connect/notauthorised")).build();
        }
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


    private UserInfo getUserInfo(AccessToken accessToken) throws IOException, ParseException {
        URI userInfoUri = config.getBaseUrl().resolve("/userinfo");
        var httpResponse = new UserInfoRequest(userInfoUri, new BearerAccessToken(accessToken.toString()))
                .toHTTPRequest()
                .send();

        var userInfoResponse = UserInfoResponse.parse(httpResponse);

        if (! userInfoResponse.indicatesSuccess()) {
            LOG.error("Userinfo request failed:" + userInfoResponse.toErrorResponse().getErrorObject().toString());
            throw new RuntimeException();
        }

        return userInfoResponse.toSuccessResponse().getUserInfo();
    }

    private AccessToken getToken(String authcode) throws ParseException, IOException {
        URI redirectUri = config.getBaseUrl().resolve("/client/callback");
        URI tokenUri = config.getBaseUrl().resolve("/token");
        var codeGrant = new AuthorizationCodeGrant(new AuthorizationCode(authcode), redirectUri);

        var clientSecretPost = new ClientSecretPost(new ClientID(config.getClientId()), new Secret(config.getClientSecret()));
        var request = new TokenRequest(tokenUri, clientSecretPost, codeGrant, new Scope("openid"));
        var tokenResponse = OIDCTokenResponseParser.parse(request.toHTTPRequest().send());

        if (! tokenResponse.indicatesSuccess()) {
            LOG.error("Token endpoint request failed:" + tokenResponse.toErrorResponse().getErrorObject().toString());
            throw new RuntimeException();
        }

        var successResponse = (OIDCTokenResponse)tokenResponse.toSuccessResponse();
        return successResponse.getOIDCTokens().getAccessToken();
    }
}
