package uk.gov.di.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.Jdbi;
import org.postgresql.util.PGobject;
import uk.gov.di.entity.Client;

import java.sql.SQLException;
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
                                                        "Test Clients",
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
                                                        List.of());
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
                    "VALUES(:clientName, :clientId, :clientSecret, :scopes, :allowedResponseTypes, :redirectUrls, :contacts)")
                    .bind("clientName", client.clientName())
                    .bind("clientId", client.clientId())
                    .bind("clientSecret", client.clientSecret())
                    .bind("scopes", asJsonB(client.scopes()))
                    .bind("allowedResponseTypes", asJsonB(client.allowedResponseTypes()))
                    .bind("redirectUrls", asJsonB(client.redirectUris()))
                    .bind("contacts", asJsonB(client.contacts()))
                    .execute()
        );
    }

    private PGobject asJsonB(List<String> list) {
        var object = new PGobject();
        object.setType("jsonb");

        try {
            object.setValue(MAPPER.writeValueAsString(list));
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return object;
    }
}
