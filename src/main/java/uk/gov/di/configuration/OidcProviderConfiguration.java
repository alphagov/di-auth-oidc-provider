package uk.gov.di.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class OidcProviderConfiguration extends Configuration {

    @JsonProperty
    @NotNull
    private String issuer;

    public String getIssuer() {
        return issuer;
    }
}
