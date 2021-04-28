package uk.gov.di.resources;

import io.dropwizard.views.View;
import uk.gov.di.entity.Client;
import uk.gov.di.services.ClientService;
import uk.gov.di.views.ClientRegistrationView;
import uk.gov.di.views.SuccessfulClientRegistrationView;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/connect")
public class ClientRegistrationResource {

    private ClientService clientService;

    public ClientRegistrationResource(ClientService clientService) {
        this.clientService = clientService;
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public View clientRegistration() {
        return new ClientRegistrationView();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public View clientRegistration(@FormParam("client_name") @NotEmpty String clientName,
                                       @FormParam("redirect_uris") @NotEmpty List<String> redirectUris,
                                       @FormParam("contacts") @NotEmpty List<String> contacts) {

        Client client = clientService.addClient(clientName, redirectUris, contacts);

        return new SuccessfulClientRegistrationView(client);
    }
}
