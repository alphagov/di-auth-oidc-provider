package uk.gov.di.views;

import io.dropwizard.views.View;
import uk.gov.di.validation.PasswordValidation;

import java.util.EnumSet;
import java.util.Set;

public class SetPasswordView extends View {

    private String email;
    private String authRequest;
    private boolean invalidPassword;
    private Set<PasswordValidation> passwordErrors = EnumSet.noneOf(PasswordValidation.class);

    public SetPasswordView(String email, String authRequest) {
        super("set-password.mustache");
        this.email = email;
        this.authRequest = authRequest;
    }

    public SetPasswordView(String email, String authRequest, Set<PasswordValidation> passwordErrors) {
        super("set-password.mustache");
        this.email = email;
        this.authRequest = authRequest;
        this.passwordErrors = passwordErrors;
    }

    public String getEmail() {
        return email;
    }

    public boolean isInvalidPassword() {
        return invalidPassword;
    }

    public String getAuthRequest() {
        return authRequest;
    }

    public boolean isPasswordEmpty() {
        return passwordErrors.contains(PasswordValidation.EMPTY_PASSWORD_FIELD);
    }

    public boolean isRetypePasswordEmpty() {
        return passwordErrors.contains(PasswordValidation.EMPTY_RETYPE_PASSWORD_FIELD);
    }

    public boolean isPasswordTooShort() {
        return passwordErrors.contains(PasswordValidation.PASSWORD_TOO_SHORT);
    }

    public boolean isPasswordNotContainingNumber() {
        return passwordErrors.contains(PasswordValidation.NO_NUMBER_INCLUDED);
    }

    public boolean isPasswordFieldsNotMatching() {
        return passwordErrors.contains(PasswordValidation.PASSWORDS_DO_NOT_MATCH);
    }

    public boolean isPasswordInvalid() {
        return !passwordErrors.isEmpty();
    }
}
