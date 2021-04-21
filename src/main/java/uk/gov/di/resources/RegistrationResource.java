package uk.gov.di.resources;

import io.dropwizard.views.View;
import org.apache.http.HttpStatus;
import uk.gov.di.views.SetPasswordView;
import uk.gov.di.views.SuccessfulLoginView;
import uk.gov.di.views.SuccessfulRegistration;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/registration")
public class RegistrationResource {

    @POST
    @Produces(MediaType.TEXT_HTML)
    public View setPassword(@FormParam("authRequest") String authRequest, @FormParam("email") @NotNull String email) {
        return new SetPasswordView(email, authRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validate")
    public Response setPassword(@FormParam("authRequest") String authRequest,
                                @FormParam("email") @NotNull String email,
                                @FormParam("password") @NotNull String password,
                                @FormParam("password-confirm") @NotNull String passwordConfirm) {
        if (!password.isBlank() && password.equals(passwordConfirm)) {
            return Response.ok(new SuccessfulRegistration(authRequest)).build();
        } else {
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new SetPasswordView(email, authRequest, true)).build();
        }
    }

    @POST
    @Path("/continue")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response validateLogin(
            @FormParam("authRequest") String authRequest) {
        return Response.ok(new SuccessfulLoginView(authRequest)).build();
    }

}
