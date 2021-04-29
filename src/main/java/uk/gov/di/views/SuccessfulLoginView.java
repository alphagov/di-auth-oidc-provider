package uk.gov.di.views;

import io.dropwizard.views.View;

public class SuccessfulLoginView extends View {
    public SuccessfulLoginView(String authRequest, String clientName) {
        super("successful-login.mustache");
        this.authRequest = authRequest;
        this.clientName = clientName;
    }

    private String authRequest;
    private String clientName;

    public String getAuthRequest() {
        return authRequest;
    }

    public String getClientName() {
        return clientName;
    }
}
