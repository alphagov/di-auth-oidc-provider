package uk.gov.di.services;

import java.util.Map;

public class UserValidationService {

    public boolean isValidUser(String email, String password) {
        var credentialsMap = Map.of("test@digital.cabinet-office.gov.uk", "password");

        return credentialsMap.containsKey(email) && credentialsMap.get(email).equals(password);
    }

}
