package uk.gov.di.views;

import io.dropwizard.views.View;

public class VerificationResponseView extends View {

    private String username;

    public VerificationResponseView(String username) {
        super("verification-response.mustache");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
