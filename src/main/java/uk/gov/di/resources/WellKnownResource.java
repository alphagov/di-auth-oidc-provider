package uk.gov.di.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/.well-known/")
public class WellKnownResource {
    @GET
    @Path("/openid-configuration")
    @Produces("application/json")
    public String openIdConfiguration() {
        
        return """
        {
            "issuer": "https://di-auth-oidc-provider.london.cloudapps.digital",
            "authorization_endpoint": "https://di-auth-oidc-provider.london.cloudapps.digital/authorize",
            "token_endpoint": "https://di-auth-oidc-provider.london.cloudapps.digital/token",
            "token_endpoint_auth_methods_supported": [
                "client_secret_basic"
            ],
            "token_endpoint_auth_signing_alg_values_supported": [
                "RS256",
                "ES256"
            ],
            "userinfo_endpoint": "https://di-auth-oidc-provider.london.cloudapps.digital/userinfo",
            "jwks_uri": "https://di-auth-oidc-provider.london.cloudapps.digital/jwks.json",
            "registration_endpoint": "https://di-auth-oidc-provider.london.cloudapps.digital/register",
            "scopes_supported": [
                "openid",
                "profile",
                "email"
            ],
            "response_types_supported": [
                "code",
                "code id_token",
                "id_token",
                "token id_token"
            ],
            "acr_values_supported": [],
            "subject_types_supported": [
                "public",
                "pairwise"
            ],
            "userinfo_signing_alg_values_supported": [
                "RS256",
                "ES256",
                "HS256"
            ],
            "userinfo_encryption_alg_values_supported": [
                "RSA1_5",
                "A128KW"
            ],
            "userinfo_encryption_enc_values_supported": [
                "A128CBC-HS256",
                "A128GCM"
            ],
            "id_token_signing_alg_values_supported": [
                "RS256",
                "ES256",
                "HS256"
            ],
            "id_token_encryption_alg_values_supported": [
                "RSA1_5",
                "A128KW"
            ],
            "id_token_encryption_enc_values_supported": [
                "A128CBC-HS256",
                "A128GCM"
            ],
            "request_object_signing_alg_values_supported": [
                "none",
                "RS256",
                "ES256"
            ],
            "display_values_supported": [
                "page",
                "popup"
            ],
            "claim_types_supported": [
                "normal",
                "distributed"
            ],
            "claims_supported": [
                "sub",
                "iss",
                "auth_time",
                "acr",
                "name",
                "given_name",
                "family_name",
                "nickname",
                "profile",
                "picture",
                "website",
                "email",
                "email_verified",
                "locale",
                "zoneinfo",
            ],
            "claims_parameter_supported": true,
            "service_documentation": "http://di-auth-oidc-provider.london.cloudapps.digitalservice/_documentation.html",
            "ui_locales_supported": [
                "en-US",
                "en-GB"
            ]
        }
        """;
        
        
    }



}
