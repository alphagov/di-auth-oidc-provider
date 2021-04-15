package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import uk.gov.di.entity.Client;

import java.util.List;
import java.util.Optional;

public class ClientService {

    private List<Client> clients;

    public ClientService(List<Client> clients) {
        this.clients = clients;
    }

    public boolean isAuthorizationRequestValid(AuthorizationRequest authRequest) {
        Optional<Client> client = getClient(authRequest.getClientID().toString());

        if (client.isEmpty()) {
            return false;
        }

        return client.get().getAllowedResponseTypes().contains(authRequest.getResponseType().toString()) &&
                client.get().getScopes().containsAll(authRequest.getScope().toStringList()) &&
                client.get().getRedirectUris().contains(authRequest.getRedirectionURI().toString());
    }

    public boolean isValidClient(String clientId, String clientSecret) {
        Optional<Client> client = getClient(clientId);
        return client.isPresent() && client.get().getClientSecret().equals(clientSecret);
    }

    private Optional<Client> getClient(String clientId) {
        return clients.stream().filter(t -> t.getClientId().equals(clientId)).findFirst();
    }
}
