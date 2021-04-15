package uk.gov.di.entity;

import java.util.List;

public class Client {

    private String clientId;
    private String clientSecret;
    private List<String> scopes;
    private List<String> allowedResponseTypes;
    private List<String> redirectUris;

    public Client(
            String clientId,
            String clientSecret,
            List<String> scopes,
            List<String> allowedResponseTypes,
            List<String> redirectUris) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scopes = scopes;
        this.allowedResponseTypes = allowedResponseTypes;
        this.redirectUris = redirectUris;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public List<String> getAllowedResponseTypes() {
        return allowedResponseTypes;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }
}
