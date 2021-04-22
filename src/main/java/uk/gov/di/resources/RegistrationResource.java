package uk.gov.di.resources;

import io.dropwizard.views.View;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.services.CognitoService;
import uk.gov.di.services.UserService;
import uk.gov.di.views.SetPasswordView;
import uk.gov.di.views.SuccessfulRegistration;
import uk.gov.di.views.VerificationResponseView;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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

@Path("/registration")
public class RegistrationResource {

    private UserService userService;

    public RegistrationResource(UserService userService) {
        this.userService = userService;
    }

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationResource.class);

    private CognitoService cognitoService;

    public RegistrationResource(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
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
            cognitoService.signUp(email, password);
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
                .cookie(
                        new NewCookie(
                                "userCookie",
                                "dummy",
                                "/",
                                null,
                                Cookie.DEFAULT_VERSION,
                                null,
                                NewCookie.DEFAULT_MAX_AGE,
                                false))
                .build();
    }

    @GET
    @Path("/verifyAccessCode")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response verificationCode(
            @QueryParam("username") String username,
            @QueryParam("code") String code) {

        LOG.info("/verifyAccessCode: {} {}", username, code);

        if (cognitoService.VerifyAccessCode(username, code)) {
            return Response.ok(new VerificationResponseView(username)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
