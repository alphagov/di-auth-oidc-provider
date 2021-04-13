package uk.gov.di.views;

import io.dropwizard.views.View;

public class PasswordView extends View {
    public PasswordView(String authRequest) {
        super("password.mustache");
        this.authRequest = authRequest;
    }

    private String authRequest;

    public String getAuthRequest() {
        return authRequest;
    }
}
