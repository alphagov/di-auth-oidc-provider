package uk.gov.di.resources;

import com.nimbusds.jose.jwk.JWKSet;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.services.TokenService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static java.text.MessageFormat.format;

@Path("/.well-known/")
public class WellKnownResource {

    private TokenService tokenService;
    private OidcProviderConfiguration configuration;

    public WellKnownResource(TokenService tokenService, OidcProviderConfiguration configuration) {
        this.tokenService = tokenService;
        this.configuration = configuration;
    }

    @GET
    @Path("/openid-configuration")
    @Produces("application/json")
    public String openIdConfiguration() {
        
        return """
        {
            "issuer": "<baseUrl>",
            "authorization_endpoint": "<baseUrl>authorize",
            "token_endpoint": "<baseUrl>token",
            "token_endpoint_auth_methods_supported": [
                "client_secret_basic"
            ],
            "token_endpoint_auth_signing_alg_values_supported": [
                "RS256",
                "ES256"
            ],
            "userinfo_endpoint": "<baseUrl>userinfo",
            "jwks_uri": "<baseUrl>.well-known/jwks.json",
            "registration_endpoint": "<baseUrl>register",
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
                "zoneinfo"
            ],
            "claims_parameter_supported": true,
            "service_documentation": "http://di-auth-oidc-provider.london.cloudapps.digitalservice/_documentation.html",
            "ui_locales_supported": [
                "en-US",
                "en-GB"
            ]
        }
        """.replace("<baseUrl>", configuration.getBaseUrl().toString());
        
        
    }

    @GET
    @Path("/jwks.json")
    @Produces("application/json")
    public Response jwks() {
        JWKSet jwkSet = new JWKSet(tokenService.getSigningKey());

        return Response.ok(jwkSet.toJSONObject(true)).build();
    }
}
