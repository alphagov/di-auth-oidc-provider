INSERT INTO client (client_name, client_id, client_secret, scopes, allowed_response_types, redirect_urls, contacts )
VALUES
    ('Dummy Service', 'some_client_id', 'password', '["openid", "profile", "email"]', '["code"]', '["https://di-auth-stub-relying-party.london.cloudapps.digital/oidc/callback", "http://localhost:8081/oidc/callback"]', '["test@test.digital.cabinet-office.gov.uk"]'),
    ('Client Registration Service', 'admin_client_id', 'admin_client_secret', '["openid", "profile", "email"]', '["code"]', '["https://di-auth-oidc-provider.london.cloudapps.digital/client/callback", "http://localhost:8080/client/callback"]', '["test@test.digital.cabinet-office.gov.uk"]')
ON CONFLICT (client_id) DO UPDATE SET client_name = EXCLUDED.client_name, client_secret = EXCLUDED.client_secret, scopes = EXCLUDED.scopes, allowed_response_types = EXCLUDED.allowed_response_types, redirect_urls = EXCLUDED.redirect_urls, contacts = EXCLUDED.contacts
;

INSERT INTO registration_whitelist (email)
VALUES
    ('joe.bloggs@digital.cabinet-office.gov.uk')
ON CONFLICT DO NOTHING;
