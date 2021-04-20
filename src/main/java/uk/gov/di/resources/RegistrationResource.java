package uk.gov.di.resources;

import io.dropwizard.views.View;
import uk.gov.di.views.RegisterView;
import uk.gov.di.views.SetPasswordView;
import uk.gov.di.views.SuccessfulLoginView;
import uk.gov.di.views.SuccessfulRegistration;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/registration")
public class RegistrationResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public View register() {
        return new RegisterView();
    }

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
            URI destination =
                    UriBuilder.fromUri(URI.create("/registration"))
                            .queryParam("failedRegistration", true)
                            .build();

            return Response.status(302).location(destination).build();
        }
    }

}
