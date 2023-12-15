package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.VenueDao;
import yar.sam.models.Venue;
import io.smallrye.mutiny.Uni;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {

    @Inject
    VenueDao venueDao;

    @GET
    public Uni<Response> getAllVenues() {
        return venueDao.getVenues()
                .onItem().transform(venues -> Response.ok(venues).build());
    }

    @POST
    public Uni<Response> createVenue(Venue venue) {
        return venueDao.addVenue(venue)
                .onItem().transform(createdVenue -> Response.status(Response.Status.CREATED).entity(createdVenue).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updateVenue(@PathParam("id") int id, Venue venue) {
        venue.setId(id);
        return venueDao.updateVenue(venue)
                .onItem().transform(updated -> Response.ok().build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteVenue(@PathParam("id") int id) {
        return venueDao.deleteVenue(id)
                .onItem().transform(deleted -> Response.noContent().build());
    }
}
