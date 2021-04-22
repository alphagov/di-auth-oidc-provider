package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final Map<String, String> credentialsMap = new HashMap<>(Map.of("joe.bloggs@digital.cabinet-office.gov.uk", "password"));
    private final Map<String, UserInfo> userInfoMap = new HashMap<>();


    public UserService() {
        UserInfo userInfo = new UserInfo(new Subject());
        userInfo.setFamilyName("Bloggs");
        userInfo.setGivenName("Joe");
        userInfo.setEmailAddress("joe.bloggs@digital.cabinet-office.gov.uk");
        userInfo.setGender(Gender.MALE);

        userInfoMap.put("joe.bloggs@digital.cabinet-office.gov.uk", userInfo);
    }


    public boolean userExists(String email) {
        return credentialsMap.containsKey(email);
    }

    public boolean isValidUser(String email, String password) {
        return credentialsMap.containsKey(email) && credentialsMap.get(email).equals(password);
    }
    
    public void addUser(String email, String password) {
        credentialsMap.put(email, password);

        UserInfo userInfo = new UserInfo(new Subject());
        userInfo.setEmailAddress(email);

        userInfoMap.put(email, userInfo);
    }
}
