package uk.gov.di.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import uk.gov.di.entity.Client;

import java.util.List;

public class ClientConfigService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Jdbi database;

    public ClientConfigService(Jdbi database) {
        this.database = database;
    }

    public List<Client> getClients() {
        return database.withHandle(
                handle ->
                        handle.registerRowMapper(ConstructorMapper.factory(Client.class))
                                .createQuery("SELECT * FROM client;")
                                .mapTo(Client.class).list());
    }

    public void addClient(Client client) {
        database.useHandle(handle ->
            handle.createUpdate("INSERT INTO client (client_name, client_id, client_secret, scopes, allowed_response_types, redirect_urls, contacts )" +
                    "VALUES(:clientName, :clientId, :clientSecret, :scopes, :allowedResponseTypes, :redirectUrls, :contacts)")
                    .bindMethods(client)
                    .execute()
        );
    }

    public boolean isAuthorisedToRegisterClients(String email) {
        return database.withHandle(handle ->
                handle.createQuery("SELECT COUNT(email) FROM registration_whitelist WHERE email = :email")
                .bind("email", email).mapTo(Integer.class).one() == 1
                );
    }
}
