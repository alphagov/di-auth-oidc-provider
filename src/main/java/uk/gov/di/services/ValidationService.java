package uk.gov.di.services;

import uk.gov.di.validation.EmailValidation;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ValidationService {

    private static final Pattern EMAIL_REGEX = Pattern.compile("[^@]+@[^@]+\\.[^@]*");

    public Set<EmailValidation> validateEmailAddress(String email) {
        Set<EmailValidation> emailErrors = EnumSet.noneOf(EmailValidation.class);
        if (email.isBlank()) {
            emailErrors.add(EmailValidation.EMPTY_EMAIL);
        }
        if (!email.isBlank() && !EMAIL_REGEX.matcher(email).matches()) {
            emailErrors.add(EmailValidation.INCORRECT_FORMAT);
        }
        return emailErrors;
    }
}
