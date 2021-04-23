package uk.gov.di.services;

import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.entity.VcapServices;

import static java.text.MessageFormat.format;

public class PostgresService {

    public static void setPostgresCredentialsFromVcap(OidcProviderConfiguration config) {
        String vcap = System.getenv("VCAP_SERVICES");
        if (vcap != null && vcap.length() > 0) {

            var credentials = VcapServices.readPostgresConfiguration(vcap).orElseThrow();

            String uri = format("jdbc:postgresql://{0}:{1}/{2}",
                    credentials.host(), credentials.port(), credentials.name());

            config.getDatabase().setUrl(uri);
            config.getDatabase().setUser(credentials.username());
            config.getDatabase().setPassword(credentials.password());
        }
    }
}
