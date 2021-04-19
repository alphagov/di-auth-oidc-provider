package uk.gov.di.services;

import org.jdbi.v3.core.Jdbi;
import uk.gov.di.entity.Client;
import java.util.List;

public class ClientConfigService {

    private final Jdbi database;

    public ClientConfigService(Jdbi database) {
        this.database = database;
    }

    public List<Client> getClients() {
        return List.of(
                new Client(
                        "some_client_id",
                        "password",
                        List.of("openid", "profile", "email"),
                        List.of("code"),
                        List.of(
                                "https://di-auth-stub-relying-party.london.cloudapps.digital/oidc/callback",
                                "http://localhost:8081/oidc/callback")));
    }
}
