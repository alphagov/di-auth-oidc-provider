package uk.gov.di.entity;

import java.util.List;

public record Client(
        String clientName,
        String clientId,
        String clientSecret,
        List<String> scopes,
        List<String> allowedResponseTypes,
        List<String> redirectUris,
        List<String> contacts) {}
