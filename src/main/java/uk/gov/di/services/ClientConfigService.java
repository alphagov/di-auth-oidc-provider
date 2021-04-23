package uk.gov.di.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.Jdbi;
import uk.gov.di.entity.Client;

import java.util.List;

public class ClientConfigService {

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
                                            ObjectMapper mapper = new ObjectMapper();

                                            try {
                                                return new Client(
                                                        rs.getString("client_id"),
                                                        rs.getString("client_secret"),
                                                        mapper.readValue(
                                                                rs.getString("scopes"),
                                                                new TypeReference<
                                                                        List<String>>() {}),
                                                        mapper.readValue(
                                                                rs.getString(
                                                                        "allowed_response_types"),
                                                                new TypeReference<
                                                                        List<String>>() {}),
                                                        mapper.readValue(
                                                                rs.getString("redirect_urls"),
                                                                new TypeReference<
                                                                        List<String>>() {}));
                                            } catch (JsonProcessingException e) {
                                                e.printStackTrace();
                                                return null;
                                            }
                                        })
                                .list());
    }
}
