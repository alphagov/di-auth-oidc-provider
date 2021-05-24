package uk.gov.di.resources;

import io.dropwizard.views.View;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.services.AuthenticationService;
import uk.gov.di.services.ValidationService;
import uk.gov.di.validation.PasswordValidation;
import uk.gov.di.views.ConfirmRegistrationView;
import uk.gov.di.views.SetPasswordView;
import uk.gov.di.views.SuccessfulRegistrationView;
import uk.gov.di.views.VerificationResponseView;

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
import java.util.Set;

@Path("/registration")
public class RegistrationResource {

    private AuthenticationService authenticationService;
    private ValidationService validationService;

    public RegistrationResource(AuthenticationService authenticationService, ValidationService validationService) {
        this.authenticationService = authenticationService;
        this.validationService = validationService;
    }

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationResource.class);

    @POST
    @Produces(MediaType.TEXT_HTML)
    public View setPassword(
            @FormParam("authRequest") String authRequest,
            @FormParam("email") @NotNull String email) {
        return new SetPasswordView(email, authRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validate")
    public Response setPassword(
            @FormParam("authRequest") String authRequest,
            @FormParam("email") @NotNull String email,
            @FormParam("password") @NotNull String password,
            @FormParam("password-confirm") @NotNull String passwordConfirm) {
        Set<PasswordValidation> passwordValidationErrors = validationService.validatePassword(password, passwordConfirm);
        if (passwordValidationErrors.isEmpty()) {
            authenticationService.signUp(email, password);
            if (authenticationService.isEmailVerificationRequired()) {
                return Response.ok(new ConfirmRegistrationView(authRequest, email)).build();
            } else {
                return Response.ok(new SuccessfulRegistrationView(authRequest))
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
            }
        } else {
            return Response.status(HttpStatus.SC_BAD_REQUEST)
                    .entity(new SetPasswordView(email, authRequest, passwordValidationErrors))
                    .build();
        }
    }

    @POST
    @Path("/continue")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response validateLogin(@FormParam("authRequest") String authRequest) {
        return Response.status(Response.Status.FOUND)
                .location(URI.create("/authorize?" + authRequest))
                .build();
    }

    @POST
    @Path("/verifyAccessCode")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response verificationCode(
            @FormParam("email") String username, @FormParam("code") String code) {

        LOG.info("/verifyAccessCode: {} {}", username, code);

        if (authenticationService.verifyAccessCode(username, code)) {
            return Response.ok(new VerificationResponseView(username)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
