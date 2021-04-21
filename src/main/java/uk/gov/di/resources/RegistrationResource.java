package uk.gov.di.resources;

import io.dropwizard.views.View;
import org.apache.http.HttpStatus;
import uk.gov.di.views.SetPasswordView;
import uk.gov.di.views.SuccessfulRegistration;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/registration")
public class RegistrationResource {

    @POST
    @Produces(MediaType.TEXT_HTML)
    public View setPassword(@FormParam("email") @NotNull String email) {
        return new SetPasswordView(email);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validate")
    public Response setPassword(@FormParam("email") @NotNull String email,
                                @FormParam("password") @NotNull String password,
                                @FormParam("password-confirm") @NotNull String passwordConfirm) {
        if (!password.isBlank() && password.equals(passwordConfirm)) {
            return Response.ok(new SuccessfulRegistration()).build();
        } else {
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new SetPasswordView(email, true)).build();
        }
    }
}
