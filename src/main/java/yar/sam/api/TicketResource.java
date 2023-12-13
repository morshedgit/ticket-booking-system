package yar.sam.api;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.Dao;
import yar.sam.models.Seat;

@Path("/seats")
public class TicketResource {

    private static final Logger LOGGER = Logger.getLogger(TicketResource.class);

    @Inject
    Dao dao;

    @PATCH
    @Path("/{id}/book/{client_id}")
    public Uni<Seat> bookSeat(@PathParam("id") Long id, @PathParam("client_id") Long clientId) {
        return dao.bookSeat(id, clientId)
            .onFailure()
            .transform(throwable -> {
                LOGGER.error("Error booking seat: " + throwable.getMessage());
                return new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Seat not available or does not exist")
                    .build());
            });
    }
    @PATCH
    @Path("/{id}/unbook/{client_id}")
    public Uni<Seat> unbookSeat(@PathParam("id") Long id, @PathParam("client_id") Long clientId) {
        return dao.unbookSeat(id)
            .onFailure()
            .transform(throwable -> {
                LOGGER.error("Error unbooking seat: " + throwable.getMessage());
                return new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Seat cannot be unbooked")
                    .build());
            });
    }

    @PATCH
    @Path("/{id}/reserve/{client_id}")
    public Uni<Seat> reserveSeat(@PathParam("id") Long id, @PathParam("client_id") Long clientId) {
        return dao.reserveSeat(id, clientId)
            .onFailure()
            .transform(throwable -> {
                LOGGER.error("Error reserving seat: " + throwable.getMessage());
                return new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Seat not available or does not exist")
                    .build());
            });
    }
    @PATCH
    @Path("/{id}/unreserve/{client_id}")
    public Uni<Seat> unreserveSeat(@PathParam("id") Long id, @PathParam("client_id") Long clientId) {
        return dao.unreserveSeat(id)
            .onFailure()
            .transform(throwable -> {
                LOGGER.error("Error unreserving seat: " + throwable.getMessage());
                return new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Seat cannot be unreserved")
                    .build());
            });
    }
}
