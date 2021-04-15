CREATE TABLE client (
    client_id VARCHAR(64) NOT NULL,
    client_secret VARCHAR(64) NOT NULL,
    scopes JSONB NOT NULL,
    allowed_response_types JSONB NOT NULL,
    redirect_urls JSONB NOT NULL,
    PRIMARY KEY (client_id)
);
