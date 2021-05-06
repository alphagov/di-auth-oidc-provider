package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.services.AuthenticationService;
import uk.gov.di.services.DynamoService;
import uk.gov.di.services.TokenService;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/userinfo")
public class UserInfoResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoResource.class);

    private final TokenService tokenService;
    private final AuthenticationService authenticationService;
    private Optional<DynamoService> dynamoService;

    public UserInfoResource(
            TokenService tokenService, AuthenticationService authenticationService) {
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
        this.dynamoService = Optional.empty();
    }

    public UserInfoResource(TokenService tokenService, AuthenticationService authenticationService, Optional<DynamoService> dynamoService) {
        this(tokenService, authenticationService);
        this.dynamoService = dynamoService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userinfo(@HeaderParam("Authorization") String authorizationHeader) {

        try {
            AccessToken accessToken = AccessToken.parse(authorizationHeader);

            var email = tokenService.getEmailForToken(accessToken);
            LOG.info("UserInfoResource.userinfo: {} {}", email, accessToken.toJSONString());
            var userInfo = authenticationService.getInfoForEmail(email);

            return dynamoService.map(x ->
                    Response.ok(x.getUserInfo(email).toJSONObject()).build()
            ).orElse(Response.ok(userInfo.toJSONObject()).build());
        } catch (ParseException e) {
            LOG.info("UserInfoResource.userinfo ParseException {}", e);
            return Response.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
    }
}
