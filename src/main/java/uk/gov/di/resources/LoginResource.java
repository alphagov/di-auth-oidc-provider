package uk.gov.di.resources;

import io.dropwizard.views.View;
import uk.gov.di.services.AuthenticationService;
import uk.gov.di.services.SRPUserService;
import uk.gov.di.views.LoginView;
import uk.gov.di.views.PasswordView;
import uk.gov.di.views.SuccessfulLoginView;

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
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/login")
public class LoginResource {

    private final SRPUserService authenticationService;

    public LoginResource(SRPUserService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public View login(
            @QueryParam("authRequest") String authRequest,
            @QueryParam("failedLogin") boolean failedLogin) {
        return new LoginView(authRequest, failedLogin);
    }

    @POST
    public Response login(@FormParam("authRequest") String authRequest, @FormParam("email") String email) {
        if (authenticationService.userExists(email)) {
            return Response.ok(new PasswordView(authRequest, email)).build();
        }
        else {
            URI destination =
                    UriBuilder.fromUri(URI.create("/registration"))
                            .build();

            return Response.status(Response.Status.TEMPORARY_REDIRECT).location(destination).build();
        }
    }

    @POST
    @Path("/validate")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response validateLogin(
            @FormParam("authRequest") String authRequest,
            @FormParam("email") String email,
            @FormParam("password") String password) {
        boolean isValid = authenticationService.login(email, password);

        if (isValid) {
            return Response.ok(new SuccessfulLoginView(authRequest))
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
            URI destination =
                    UriBuilder.fromUri(URI.create("/login"))
                            .queryParam("authRequest", authRequest)
                            .queryParam("failedLogin", true)
                            .build();

            return Response.status(302).location(destination).build();
        }
    }

    @POST
    @Path("/continue")
    public Response continueToAuthorize(@FormParam("authRequest") String authRequest) {
        return Response.status(Response.Status.FOUND)
                .location(URI.create("/authorize?" + authRequest))
                .build();
    }

    @POST
    @Path("/srpstep1")
    public Response srpStep1(@FormParam("email") String email) {
        return Response.ok(authenticationService.step1(email)).build();
    }
}
