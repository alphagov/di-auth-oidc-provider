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
                        "client_secret_post"
                    ],
                    "userinfo_endpoint": "https://di-auth-oidc-provider.london.cloudapps.digital/userinfo",
                    "jwks_uri": "https://di-auth-oidc-provider.london.cloudapps.digital/jwks.json",
                    "registration_endpoint": "https://di-auth-oidc-provider.london.cloudapps.digital/connect/register",
                    "scopes_supported": [
                        "openid",
                        "profile",
                        "email"
                    ],
                    "response_types_supported": [
                        "code"
                    ],
                    "subject_types_supported": [
                        "public"
                    ],
                    "grant_types_supported": ["authorization_code"],
                    "display_values_supported": [
                        "page"
                    ],
                    "claim_types_supported": [
                        "normal"
                    ],
                    "claims_supported": [
                        "sub",
                        "name",
                        "family_name",
                        "given_name",
                        "email"
                      ]
                }
                """;
        
        
    }
}
