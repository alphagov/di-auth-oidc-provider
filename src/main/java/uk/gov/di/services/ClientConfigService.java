package uk.gov.di.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.generic.GenericType;
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
                        handle.createQuery("SELECT * FROM client;")
                                .map(
                                        (rs, ctx) -> {
                                            try {
                                                return new Client(
                                                        rs.getString("client_name"),
                                                        rs.getString("client_id"),
                                                        rs.getString("client_secret"),
                                                        MAPPER.readValue(
                                                                rs.getString("scopes"),
                                                                new TypeReference<
                                                                        List<String>>() {}),
                                                        MAPPER.readValue(
                                                                rs.getString(
                                                                        "allowed_response_types"),
                                                                new TypeReference<
                                                                        List<String>>() {}),
                                                        MAPPER.readValue(
                                                                rs.getString("redirect_urls"),
                                                                new TypeReference<
                                                                        List<String>>() {}),
                                                        MAPPER.readValue(
                                                                rs.getString("contacts"),
                                                                new TypeReference<
                                                                        List<String>>() {}));
                                            } catch (JsonProcessingException e) {
                                                e.printStackTrace();
                                                return null;
                                            }
                                        })
                                .list());
    }

    public void addClient(Client client) {
        database.useHandle(handle ->
            handle.createUpdate("INSERT INTO client (client_name, client_id, client_secret, scopes, allowed_response_types, redirect_urls, contacts )" +
                    "VALUES(:clientName, :clientId, :clientSecret, :scopes, :allowedResponseTypes, :redirectUris, :contacts)")
                    .bindMethods(client)
                    .execute()
        );
    }

    public boolean isAuthorisedToRegisterClients(String email) {
        return database.withHandle(handle ->
                !handle.createQuery("SELECT email FROM registration_whitelist WHERE email = :email")
                .bind("email", email).collectInto(new GenericType<List<String>>() {}).isEmpty()
                );
    }
}
