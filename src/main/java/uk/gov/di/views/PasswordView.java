package uk.gov.di.views;

import io.dropwizard.views.View;

public class PasswordView extends View {
    public PasswordView(String authRequest, String email) {
        super("password.mustache");
        this.authRequest = authRequest;
        this.email = email;
    }

    private String authRequest;
    private String email;

    public String getAuthRequest() {
        return authRequest;
    }

    public String getEmail() {
        return email;
    }
}
