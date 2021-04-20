package uk.gov.di.views;

import io.dropwizard.views.View;

public class RegisterView extends View {
    public RegisterView() {
        super("register-emailaddress.mustache");
    }
}
