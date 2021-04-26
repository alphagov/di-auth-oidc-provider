package uk.gov.di.views;

import io.dropwizard.views.View;

public class SuccessfulRegistrationView extends View {

    private String authRequest;

    public SuccessfulRegistrationView(String authRequest) {
        super("successful-registration.mustache");
        this.authRequest = authRequest;
    }

    public String getAuthRequest() {
        return authRequest;
    }
}
