package uk.gov.di.views;

import io.dropwizard.views.View;

public class SetPasswordView extends View {

    private String email;

    public SetPasswordView (String email) {
        super("set-password.mustache");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
