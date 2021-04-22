package uk.gov.di.resources;

import io.dropwizard.views.View;
import org.apache.http.HttpStatus;
import uk.gov.di.services.UserService;
import uk.gov.di.views.SetPasswordView;
import uk.gov.di.views.SuccessfulRegistration;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/registration")
public class RegistrationResource {

    private UserService userService;

    public RegistrationResource(UserService userService) {
        this.userService = userService;
    }

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
            userService.addUser(email, password);
            return Response.ok(new SuccessfulRegistration(authRequest))
                    .cookie(
                            new NewCookie(
                                    "userCookie",
                                    email,
                                    "/",
                                    null,
                                    Cookie.DEFAULT_VERSION,
                                    null,
                                    NewCookie.DEFAULT_MAX_AGE,
                                    false))
                    .build();
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
        return Response.status(Response.Status.FOUND)
                .location(URI.create("/authorize?" + authRequest))
                .build();
    }

}
