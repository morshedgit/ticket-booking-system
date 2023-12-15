package yar.sam.api;

import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.ClientDao;
import yar.sam.models.Client;
import io.smallrye.mutiny.Uni;

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    @Inject
    ClientDao clientDao;

    @GET
    public Uni<List<Client>> getClients() {
        return clientDao.getClients();
    }

    @POST
    public Uni<Response> addClient(Client client) {
        return clientDao.addClient(client)
                .onItem().transform(id -> Response.status(Response.Status.CREATED).entity(id).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updateClient(@PathParam("id") int id, Client client) {
        client.setId(id);
        return clientDao.updateClient(client)
                .onItem().transform(updated -> Response.ok().build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteClient(@PathParam("id") int id) {
        return clientDao.deleteClient(id)
                .onItem().transform(deleted -> Response.noContent().build());
    }
}
