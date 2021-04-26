package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.OIDCError;
import uk.gov.di.entity.Client;
import uk.gov.di.helpers.AuthenticationResponseHelper;

import java.util.List;
import java.util.Optional;

public class ClientService {

    private List<Client> clients;
    private AuthorizationCodeService authorizationCodeService;

    public ClientService(List<Client> clients, AuthorizationCodeService authorizationCodeService) {
        this.clients = clients;
        this.authorizationCodeService = authorizationCodeService;
    }

    public Optional<ErrorObject> getErrorForAuthorizationRequest(AuthorizationRequest authRequest) {
        Optional<Client> clientMaybe = getClient(authRequest.getClientID().toString());

        if (clientMaybe.isEmpty()) {
            return Optional.of(OIDCError.UNMET_AUTHENTICATION_REQUIREMENTS);
        }

        var client = clientMaybe.get();

        if (!client.redirectUris().contains(authRequest.getRedirectionURI().toString())) {
            return Optional.of(OAuth2Error.INVALID_REQUEST_URI);
        }

        if (!client.allowedResponseTypes().contains(authRequest.getResponseType().toString())) {
            return Optional.of(OAuth2Error.UNSUPPORTED_RESPONSE_TYPE);
        }

        if (!client.scopes().containsAll(authRequest.getScope().toStringList())) {
            return Optional.of(OAuth2Error.INVALID_SCOPE);
        }

        return Optional.empty();
    }

    public AuthenticationResponse getSuccessfulResponse(
            AuthenticationRequest authRequest, String email) {
        AuthorizationCode code = authorizationCodeService.issueCodeForUser(email);
        return AuthenticationResponseHelper.generateSuccessfulAuthResponse(authRequest, code);
    }

    public boolean isValidClient(String clientId, String clientSecret) {
        Optional<Client> client = getClient(clientId);
        return client.map(c -> c.clientSecret().equals(clientSecret))
                .orElse(false);
    }

    private Optional<Client> getClient(String clientId) {
        return clients.stream().filter(t -> t.clientId().equals(clientId)).findFirst();
    }
}
