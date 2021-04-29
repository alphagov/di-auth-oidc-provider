package uk.gov.di.views;

import io.dropwizard.views.View;

public class ClientRegistrationView extends View {
    public ClientRegistrationView() {
        super("client-registration.mustache");
    }
}
