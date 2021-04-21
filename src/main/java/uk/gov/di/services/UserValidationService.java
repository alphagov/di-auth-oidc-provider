package uk.gov.di.services;

import java.util.Map;

public class UserValidationService {

    private final Map<String, String> credentialsMap = Map.of("joe.bloggs@digital.cabinet-office.gov.uk", "password");

    public boolean userExists(String email) {
        return credentialsMap.containsKey(email);
    }

    public boolean isValidUser(String email, String password) {
        return credentialsMap.containsKey(email) && credentialsMap.get(email).equals(password);
    }
}
