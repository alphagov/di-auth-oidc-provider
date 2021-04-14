package uk.gov.di.resources;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.apache.http.HttpStatus;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/userinfo")
public class UserInfoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userinfo(@HeaderParam("Authorization") String authorizationHeader) {

        try {
            AccessToken accessToken = AccessToken.parse(authorizationHeader);

            UserInfo userInfo = new UserInfo(new Subject());
            userInfo.setFamilyName("Smith");
            userInfo.setGivenName("John");
            userInfo.setEmailAddress("john.smith@example.com");
            userInfo.setGender(Gender.MALE);

            return Response.ok(userInfo.toJSONObject()).build();
        } catch (ParseException e) {
            return Response.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
    }
}
