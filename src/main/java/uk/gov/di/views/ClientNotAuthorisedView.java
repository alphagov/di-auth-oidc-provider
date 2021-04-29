package uk.gov.di.views;

import io.dropwizard.views.View;

public class ClientNotAuthorisedView extends View {
    public ClientNotAuthorisedView() {
        super("client-not-authorised.mustache");
    }
}
