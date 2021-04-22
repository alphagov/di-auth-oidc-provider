package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.http.HttpStatus;
import uk.gov.di.services.TokenService;
import uk.gov.di.services.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/userinfo")
public class UserInfoResource {

    private final TokenService tokenService;
    private final UserService userService;

    public UserInfoResource(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userinfo(@HeaderParam("Authorization") String authorizationHeader) {

        try {
            AccessToken accessToken = AccessToken.parse(authorizationHeader);

            var email = tokenService.getEmailForToken(accessToken);
            var userInfo = userService.getInfoForEmail(email);

            return Response.ok(userInfo.toJSONObject()).build();
        } catch (ParseException e) {
            return Response.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
    }
}
