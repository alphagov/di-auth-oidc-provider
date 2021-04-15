package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import org.junit.jupiter.api.Test;
import uk.gov.di.entity.Client;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {
    private static final ClientService CLIENT_SERVICE =
            new ClientService(
                    List.of(
                            new Client(
                                    "test-id",
                                    "test-secret",
                                    List.of("email"),
                                    List.of("code"),
                                    List.of("http://localhost:8080"))));

    @Test
    void validatesRegisteredClientSuccessfully() {
        boolean isValid =
                CLIENT_SERVICE.isAuthorizationRequestValid(
                        new AuthorizationRequest(
                                URI.create("http://localhost:8080"),
                                new ResponseType("code"),
                                ResponseMode.FORM_POST,
                                new ClientID("test-id"),
                                URI.create("http://localhost:8080"),
                                new Scope("email"),
                                new State()));

        assertTrue(isValid);
    }

    @Test
    void authorizationRequestInvalidIfClientNotRegistered() {
        var clientService = new ClientService(Collections.emptyList());

        boolean isValid =
                clientService.isAuthorizationRequestValid(
                        new AuthorizationRequest(
                                URI.create("test"),
                                new ResponseType(),
                                new ClientID("not-a-client")));

        assertFalse(isValid);
    }

    @Test
    void authorizationRequestInvalidIfClientRequestsForbiddenScope() {
        boolean isValid =
                CLIENT_SERVICE.isAuthorizationRequestValid(
                        new AuthorizationRequest(
                                URI.create("http://localhost:8080"),
                                new ResponseType("code"),
                                ResponseMode.FORM_POST,
                                new ClientID("test-id"),
                                URI.create("http://localhost:8080"),
                                new Scope("phone"),
                                new State()));

        assertFalse(isValid);
    }

    @Test
    void authorizationRequestInvalidIfClientRequestsForbiddenResponseType() {
        boolean isValid =
                CLIENT_SERVICE.isAuthorizationRequestValid(
                        new AuthorizationRequest(
                                URI.create("http://localhost:8080"),
                                new ResponseType("token"),
                                ResponseMode.FORM_POST,
                                new ClientID("test-id"),
                                URI.create("http://localhost:8080"),
                                new Scope("email"),
                                new State()));

        assertFalse(isValid);
    }

    @Test
    void authorizationRequestInvalidIfClientRequestsUnlistedRedirectUri() {
        boolean isValid =
                CLIENT_SERVICE.isAuthorizationRequestValid(
                        new AuthorizationRequest(
                                URI.create("http://localhost:8080"),
                                new ResponseType("code"),
                                ResponseMode.FORM_POST,
                                new ClientID("test-id"),
                                URI.create("http://localhost:8080/wrong"),
                                new Scope("email"),
                                new State()));

        assertFalse(isValid);
    }
}
