package yar.sam.api;

import java.util.List;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.Dao;
import yar.sam.models.Seat;
import yar.sam.models.SeatDTO;

@Path("/seats")
public class SeatResource {

    private static final Logger LOGGER = Logger.getLogger(SeatResource.class);

    @Inject
    Dao dao;

    @GET
    public Uni<List<Seat>> getAllSeats() {       
        return dao.getAllSeats();
    }
    @GET
    @Path("/{id}")
    public Uni<Seat> getSeat(@PathParam("id") Long id) {
        return dao.getSeat(id)
            .onFailure()
            .transform(throwable -> {
                LOGGER.error("Error getting seat: " + throwable.getMessage());
                return new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Seat not available or does not exist")
                    .build());
            });
    }

    @POST
    public Uni<Seat> addSeat(SeatDTO seat) {
        return dao.addSeat(seat);
        
    }
}
