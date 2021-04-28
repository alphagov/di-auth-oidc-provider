package uk.gov.di.resources;

import uk.gov.di.entity.Client;
import uk.gov.di.services.ClientService;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/connect")
public class ClientRegistrationResource {

    private ClientService clientService;

    public ClientRegistrationResource(ClientService clientService) {
        this.clientService = clientService;
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clientRegistration(@FormParam("client_name") @NotEmpty String clientName,
                                       @FormParam("redirect_uris") @NotEmpty List<String> redirectUris,
                                       @FormParam("contacts") @NotEmpty List<String> contacts) {

        Client client = clientService.addClient(clientName, redirectUris, contacts);

        return Response.ok().entity(client).build();
    }
}
