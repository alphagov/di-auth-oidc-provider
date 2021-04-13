package uk.gov.di.views;

import io.dropwizard.views.View;

public class SuccessfulLoginView extends View {
    public SuccessfulLoginView(String authRequest) {
        super("successful-login.mustache");
        this.authRequest = authRequest;
    }

    private String authRequest;

    public String getAuthRequest() {
        return authRequest;
    }
}
