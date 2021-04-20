package uk.gov.di.views;

import io.dropwizard.views.View;

public class SetPasswordView extends View {

    private String email;
    private boolean invalidPassword;

    public SetPasswordView (String email) {
        this(email, false);
    }

    public SetPasswordView (String email, boolean invalidPassword) {
        super("set-password.mustache");
        this.email = email;
        this.invalidPassword = invalidPassword;
    }

    public String getEmail() {
        return email;
    }

    public boolean isInvalidPassword() {
        return invalidPassword;
    }
}
