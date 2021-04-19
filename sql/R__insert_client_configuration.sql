INSERT INTO client ( client_id, client_secret, scopes, allowed_response_types, redirect_urls )
VALUES ('some_client_id', 'password', '["openid", "profile", "email"]', '["code"]', '["https://di-auth-stub-relying-party.london.cloudapps.digital/oidc/callback", "http://localhost:8081/oidc/callback"]')
ON CONFLICT (client_id) DO UPDATE SET client_secret = EXCLUDED.client_secret, scopes = EXCLUDED.scopes, allowed_response_types = EXCLUDED.allowed_response_types, redirect_urls = EXCLUDED.redirect_urls
;