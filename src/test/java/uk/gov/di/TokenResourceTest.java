package uk.gov.di;

import com.nimbusds.jwt.SignedJWT;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.di.resources.TokenResource;
import uk.gov.di.services.TokenService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TokenResourceTest {

    private static final TokenService tokenService = mock(TokenService.class);
    private static final SignedJWT signedJWT = mock(SignedJWT.class);
    private static final ResourceExtension tokenResourceExtension = ResourceExtension.builder().addResource(new TokenResource(tokenService)).build();

    @Test
    public void testTokenResource() {
        when(tokenService.generateIDToken(anyString())).thenReturn(signedJWT);

        MultivaluedMap<String, String> tokenResourceFormParams = new MultivaluedHashMap<>();
        tokenResourceFormParams.add("code", "123");
        tokenResourceFormParams.add("client_id",  "123");

        final Response response = tokenResourceExtension.target("/token").request()
                .post(Entity.form(tokenResourceFormParams));

        assertEquals(HttpStatus.OK_200, response.getStatus());
    }
}
