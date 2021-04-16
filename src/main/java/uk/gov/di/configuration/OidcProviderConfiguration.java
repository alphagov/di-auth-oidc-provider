package uk.gov.di.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class OidcProviderConfiguration extends Configuration {

    @JsonProperty @NotNull private String issuer;
    @Valid private DataSourceFactory database;

    public String getIssuer() {
        return issuer;
    }

    @JsonProperty("database")
    public DataSourceFactory getDatabase() {
        return database;
    }
}
