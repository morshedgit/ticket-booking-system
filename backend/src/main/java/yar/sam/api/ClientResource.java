package yar.sam.api;

import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import yar.sam.dao.ClientDao;
import yar.sam.models.Client;
import yar.sam.util.PaginationParams;
import io.smallrye.mutiny.Uni;

@Path("/clients")
public class ClientResource {

    @Inject
    ClientDao clientDao;

    @GET
    public Uni<List<Client>> getClients(@BeanParam PaginationParams paginationParams) {
        return clientDao.getClients(paginationParams);
    }

    @POST
    public Uni<Client> addClient(Client client) {
        return clientDao.addClient(client);
    }

    @PUT
    @Path("/{id}")
    public Uni<Void> updateClient(@PathParam("id") int id, Client client) {
        client.setId(id);
        return clientDao.updateClient(client);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteClient(@PathParam("id") int id) {
        return clientDao.deleteClient(id);
    }
}
