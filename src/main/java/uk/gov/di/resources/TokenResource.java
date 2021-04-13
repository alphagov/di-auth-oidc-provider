package uk.gov.di.resources;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import uk.gov.di.services.TokenService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/token")
public class TokenResource {

    private TokenService tokenService;

    public TokenResource(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokens(@FormParam("code") @NotNull AuthorizationCode code, @FormParam("client_id") @NotNull String clientId) throws ParseException {

        AccessToken accessToken = new BearerAccessToken();
        SignedJWT idToken = tokenService.generateIDToken(clientId);

        OIDCTokens oidcTokens = new OIDCTokens(idToken, accessToken, null);
        OIDCTokenResponse tokenResponse = new OIDCTokenResponse(oidcTokens);

        return Response.ok(tokenResponse.toJSONObject()).build();
    }
}
