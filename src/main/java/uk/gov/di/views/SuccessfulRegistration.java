package uk.gov.di.views;

import io.dropwizard.views.View;

public class SuccessfulRegistration extends View {

    private String authRequest;

    public SuccessfulRegistration(String authRequest) {
        super("successful-registration.mustache");
        this.authRequest = authRequest;
    }

    public String getAuthRequest() {
        return authRequest;
    }
}
