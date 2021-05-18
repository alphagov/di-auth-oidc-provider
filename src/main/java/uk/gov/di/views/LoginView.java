package uk.gov.di.views;

import io.dropwizard.views.View;
import uk.gov.di.validation.EmailValidation;

import java.util.EnumSet;
import java.util.Set;

public class LoginView extends View {
    public LoginView(String authRequest) {
        super("login.mustache");
        this.authRequest = authRequest;
    }

    public LoginView(String authRequest, boolean failedLogin) {
        super("login.mustache");
        this.authRequest = authRequest;
        this.failedLogin = failedLogin;
    }

    public LoginView(String authRequest, boolean failedLogin, Set<EmailValidation> errorSet) {
        super("login.mustache");
        this.authRequest = authRequest;
        this.failedLogin = failedLogin;
        this.errorSet = errorSet;
    }

    private boolean failedLogin;
    private String authRequest;
    private Set<EmailValidation> errorSet = EnumSet.noneOf(EmailValidation.class);

    public boolean isFailedLogin() {
        return failedLogin;
    }

    public String getAuthRequest() {
        return authRequest;
    }

    public boolean isEmailEmpty() {
        return errorSet.contains(EmailValidation.EMPTY_EMAIL);
    }

    public boolean isIncorrectEmailFormat() {
        return errorSet.contains(EmailValidation.INCORRECT_FORMAT);
    }

    public boolean isEmailInvalid() {
        return !errorSet.isEmpty();
    }

}
