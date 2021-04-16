package uk.gov.di.services;

import org.jdbi.v3.core.Jdbi;

public class ClientConfigService {

    private final Jdbi database;

    public ClientConfigService(Jdbi database) {
        this.database = database;
    }
}
