package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.OIDCError;
import uk.gov.di.entity.Client;
import uk.gov.di.helpers.AuthenticationResponseHelper;

import java.util.List;
import java.util.Optional;

public class ClientService {

    private List<Client> clients;

    public ClientService(List<Client> clients) {
        this.clients = clients;
    }

    public AuthenticationResponse validateAuthorizationRequest(AuthorizationRequest authRequest) {
        Optional<Client> clientMaybe = getClient(authRequest.getClientID().toString());

        if (clientMaybe.isEmpty()) {
            return AuthenticationResponseHelper.generateErrorAuthnResponse(
                    authRequest, OIDCError.UNMET_AUTHENTICATION_REQUIREMENTS);
        }

        var client = clientMaybe.get();

        if (!client.getRedirectUris().contains(authRequest.getRedirectionURI().toString())) {
            return AuthenticationResponseHelper.generateErrorAuthnResponse(
                    authRequest, OAuth2Error.INVALID_REQUEST_URI);
        }

        if (!client.getAllowedResponseTypes().contains(authRequest.getResponseType().toString())) {
            return AuthenticationResponseHelper.generateErrorAuthnResponse(
                    authRequest, OAuth2Error.UNSUPPORTED_RESPONSE_TYPE);
        }

        if (!client.getScopes().containsAll(authRequest.getScope().toStringList())) {
            return AuthenticationResponseHelper.generateErrorAuthnResponse(
                    authRequest, OAuth2Error.INVALID_SCOPE);
        }

        return AuthenticationResponseHelper.generateSuccessfulAuthResponse(authRequest);
    }

    public boolean isValidClient(String clientId, String clientSecret) {
        Optional<Client> client = getClient(clientId);
        return client.isPresent() && client.get().getClientSecret().equals(clientSecret);
    }

    private Optional<Client> getClient(String clientId) {
        return clients.stream().filter(t -> t.getClientId().equals(clientId)).findFirst();
    }
}
