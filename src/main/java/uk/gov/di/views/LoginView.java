package uk.gov.di.views;

import io.dropwizard.views.View;

public class LoginView extends View {
    public LoginView(String authRequest) {
        super("login.mustache");
        this.authRequest = authRequest;
    }

    private String authRequest;

    public String getAuthRequest() {
        return authRequest;
    }
}
