package uk.gov.di.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.json.Json;

import java.util.List;

public record Client(
        @JsonProperty("client_name") String clientName,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("scopes") @Json List<String> scopes,
        @JsonIgnore @Json List<String> allowedResponseTypes,
        @JsonProperty("redirect_uris") @Json List<String> redirectUrls,
        @JsonProperty("contacts") @Json List<String> contacts) {}
