package uk.gov.di.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.configuration.OidcProviderConfiguration;

import static java.text.MessageFormat.format;

public class PostgresService {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresService.class);
    private OidcProviderConfiguration config;
    private String uri;

    public PostgresService(OidcProviderConfiguration config) {
        this.config = config;
        startup();
    }

    private void startup() {
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
                    String uri =
                            format(
                                    "jdbc:postgresql://{0}:{1}/{2}",
                                    credentials.get("host").asText(),
                                    credentials.get("port").asText(),
                                    credentials.get("name").asText());
                    config.getDatabase().setUrl(uri);

                    config.getDatabase().setUser(credentials.get("username").asText());
                    LOG.info("Database username: " + credentials.get("username").asText());

                    config.getDatabase().setPassword(credentials.get("password").asText());
                    LOG.info("Database URI: " + uri);
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
