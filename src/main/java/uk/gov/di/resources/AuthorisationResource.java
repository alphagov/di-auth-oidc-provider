package uk.gov.di.resources;

import io.dropwizard.views.View;
import uk.gov.di.views.LoginView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/authorize")
public class AuthorisationResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public View authorize() {
        return new LoginView();
    }
}
