package uk.gov.di.views;

import io.dropwizard.views.View;

public class LoginView extends View {
    public LoginView(String authRequest) {
        super("login.mustache");
        this.authRequest = authRequest;
    }

    public LoginView(String authRequest, boolean failedLogin) {
        super("login.mustache");
        this.authRequest = authRequest;
        this.failedLogin = failedLogin;
    }

    private boolean failedLogin;
    private String authRequest;

    public boolean isFailedLogin() { return failedLogin; }
    public String getAuthRequest() {
        return authRequest;
    }
}
