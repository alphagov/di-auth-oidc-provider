package uk.gov.di.views;

import io.dropwizard.views.View;

public class SuccessfulLoginView extends View {
    public SuccessfulLoginView() {
        super("successful-login.mustache");
    }
}
