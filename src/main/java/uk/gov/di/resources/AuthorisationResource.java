package uk.gov.di.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/authorize")
public class AuthorisationResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response authorize() {
            return Response
                    .status(302)
                    .location(URI.create("/login"))
                    .build();
    }
}
