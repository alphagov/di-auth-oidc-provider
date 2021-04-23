package uk.gov.di.views;

import io.dropwizard.views.View;

public class SetPasswordView extends View {

    private String email;
    private String authRequest;
    private boolean invalidPassword;

    public SetPasswordView(String email, String authRequest) {
        this(email, authRequest, false);
    }

    public SetPasswordView(String email, String authRequest, boolean invalidPassword) {
        super("set-password.mustache");
        this.email = email;
        this.authRequest = authRequest;
        this.invalidPassword = invalidPassword;
    }

    public String getEmail() {
        return email;
    }

    public boolean isInvalidPassword() {
        return invalidPassword;
    }

    public String getAuthRequest() {
        return authRequest;
    }
}
