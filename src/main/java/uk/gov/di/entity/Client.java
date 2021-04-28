package uk.gov.di.entity;

import org.jdbi.v3.json.Json;

import java.util.List;

public record Client(
        String clientName,
        String clientId,
        String clientSecret,
        @Json List<String> scopes,
        @Json List<String> allowedResponseTypes,
        @Json List<String> redirectUris,
        @Json List<String> contacts) {}
