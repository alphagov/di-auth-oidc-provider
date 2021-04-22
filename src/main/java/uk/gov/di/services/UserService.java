package uk.gov.di.services;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final Map<String, String> credentialsMap = new HashMap<>(Map.of("joe.bloggs@digital.cabinet-office.gov.uk", "password"));

    public boolean userExists(String email) {
        return credentialsMap.containsKey(email);
    }

    public boolean isValidUser(String email, String password) {
        return credentialsMap.containsKey(email) && credentialsMap.get(email).equals(password);
    }
    
    public void addUser(String email, String password) {
        credentialsMap.put(email, password);
    }
}
