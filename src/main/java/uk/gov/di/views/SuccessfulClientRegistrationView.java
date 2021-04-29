package uk.gov.di.views;

import io.dropwizard.views.View;
import uk.gov.di.entity.Client;

public class SuccessfulClientRegistrationView extends View {
    private final Client client;

    public SuccessfulClientRegistrationView(Client client) {
        super("successful-client-registration.mustache");
        this.client = client;
    }

    public String getClientId() {
        return client.clientId();
    }

    public String getClientSecret() {
        return client.clientSecret();
    }
}
