package uk.gov.di.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.entity.VcapServices;

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
        var credentials = VcapServices.readPostgresConfiguration(vcap).orElseThrow();

        String uri = format("jdbc:postgresql://{0}:{1}/{2}",
                        credentials.host(), credentials.port(), credentials.name());

        config.getDatabase().setUrl(uri);
        config.getDatabase().setUser(credentials.username());
        config.getDatabase().setPassword(credentials.password());
    }

    public String getUri() {
        return uri;
    }
}
