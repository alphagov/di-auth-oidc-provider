package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.AuthorizationCode;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationCodeService {
    private Map<AuthorizationCode, String> issuedCodes = new HashMap<>();

    public AuthorizationCode issueCodeForUser(String email) {
        AuthorizationCode authorizationCode = new AuthorizationCode();
        issuedCodes.put(authorizationCode, email);

        return authorizationCode;
    }

    public String getEmailForCode(AuthorizationCode authorizationCode) {
        String email = issuedCodes.get(authorizationCode);
        issuedCodes.remove(authorizationCode);

        return email;
    }
}
