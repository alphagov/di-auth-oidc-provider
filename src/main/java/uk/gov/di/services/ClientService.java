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
        try {
            Client client = getClient(authRequest.getClientID().toString());
            return client.getAllowedResponseTypes().contains(authRequest.getResponseType()) &&
                    client.getScopes().containsAll(authRequest.getScope().toStringList()) &&
                    client.getRedirectUris().contains(authRequest.getRedirectionURI().toString());
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Client getClient(String clientId) {
        Optional<Client> client = clients.stream().filter(t -> t.getClientId().equals(clientId)).findFirst();

        if (client.isEmpty()) {
            throw new RuntimeException("Client doesn't exist");
        }
        return client.get();
    }
}
