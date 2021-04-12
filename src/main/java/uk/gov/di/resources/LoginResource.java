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
import javax.ws.rs.core.MediaType;

@Path("/login")
public class LoginResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public View login() {
        return new LoginView();
    }

    @POST
    public View login(@FormParam("email") String email) {
        return new PasswordView();
    }

    @POST
    @Path("/validate")
    public View validateLogin(@FormParam("password")String password) {
        return new SuccessfulLoginView();
    }
}
