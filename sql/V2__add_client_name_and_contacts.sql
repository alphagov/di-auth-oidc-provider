ALTER TABLE client
    ADD COLUMN IF NOT EXISTS client_name VARCHAR(64) NOT NULL,
    ADD COLUMN IF NOT EXISTS contacts JSONB NOT NULL;
