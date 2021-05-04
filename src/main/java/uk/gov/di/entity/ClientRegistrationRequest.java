package uk.gov.di.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.json.Json;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientRegistrationRequest(
        @JsonProperty("client_name") String clientName,
        @JsonProperty("redirect_uris") List<String> redirectUris,
        @JsonProperty("contacts") List<String> contacts
) {
}
