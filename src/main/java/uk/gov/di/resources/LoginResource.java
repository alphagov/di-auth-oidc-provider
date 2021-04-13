package uk.gov.di.resources;

import io.dropwizard.views.View;
import uk.gov.di.views.LoginView;
import uk.gov.di.views.PasswordView;
import uk.gov.di.views.SuccessfulLoginView;

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
import java.net.URI;

@Path("/login")
public class LoginResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public View login(@QueryParam("authRequest") String authRequest) {
        return new LoginView(authRequest);
    }

    @POST
    public View login(@FormParam("authRequest") String authRequest, @FormParam("email") String email) {
        return new PasswordView(authRequest);
    }

    @POST
    @Path("/validate")
    public View validateLogin(@FormParam("authRequest") String authRequest, @FormParam("password")String password) {
        return new SuccessfulLoginView(authRequest);
    }

    @POST
    @Path("/continue")
    public Response continueToAuthorize(@FormParam("authRequest") String authRequest) {
        return Response.status(Response.Status.FOUND)
                .location(URI.create("/authorize?" + authRequest))
                .cookie(new NewCookie("userCookie", "dummy", "/", null, Cookie.DEFAULT_VERSION, null, NewCookie.DEFAULT_MAX_AGE, false)
        ).build();
    }
}
