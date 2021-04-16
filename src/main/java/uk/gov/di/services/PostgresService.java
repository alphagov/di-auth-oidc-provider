package uk.gov.di.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.di.configuration.OidcProviderConfiguration;

public class PostgresService {

    private OidcProviderConfiguration config;
    private String uri;

    public PostgresService(OidcProviderConfiguration config) {
        this.config = config;
        startup(config);
    }

    public void startup(OidcProviderConfiguration config) {
        String vcap = System.getenv("VCAP_SERVICES");
        if (vcap != null && vcap.length() > 0) {
            setPostgresCredentialsFromVcap(vcap);
        }
    }

    private void setPostgresCredentialsFromVcap(String vcap) {

        try {
            var objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(vcap);
            JsonNode postgresJsonNode = jsonNode.get("postgres");
            if (postgresJsonNode.isArray()) {
                for (JsonNode node : postgresJsonNode) {
                    JsonNode credentials = node.get("credentials");
                    uri = credentials.get("jdbcuri").toString();
                    config.getDatabase().setUrl(uri);
                    config.getDatabase().setUser(credentials.get("username").toString());
                    config.getDatabase().setPassword(credentials.get("password").toString());
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUri() {
        return uri;
    }
}
