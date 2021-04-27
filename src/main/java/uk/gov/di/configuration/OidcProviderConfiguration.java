package uk.gov.di.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class OidcProviderConfiguration extends Configuration {

    public enum AuthenticationServiceProvider {
        COGNITO("cognito"),
        USER("user"),
        SRP("srp"),
        COGNITOSRP("cognitosrp");

        private String providerName;

        AuthenticationServiceProvider(String providerName) {
            this.providerName = providerName;
        }
    }

    @JsonProperty @NotNull private String issuer;
    @JsonProperty @NotNull private AuthenticationServiceProvider authenticationServiceProvider;

    @Valid private DataSourceFactory database;

    public String getIssuer() {
        return issuer;
    }

    public AuthenticationServiceProvider getAuthenticationServiceProvider() {
        return authenticationServiceProvider;
    }

    @JsonProperty("database")
    public DataSourceFactory getDatabase() {
        return database;
    }
}
