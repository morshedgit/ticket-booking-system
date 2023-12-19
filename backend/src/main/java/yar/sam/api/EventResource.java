package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.EventDao;
import yar.sam.dao.ReservationDao;
import yar.sam.models.Event;
import yar.sam.models.Reservation;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventDao eventDao;

    @GET
    public Uni<Response> getAllEvents() {
        return eventDao.getEvents()
                .onItem().transform(events -> Response.ok(events).build());
    }

    @POST
    public Uni<Response> createEvent(Event event) {
        return eventDao.addEvent(event)
                .onItem().transform(createdEvent -> Response.status(Response.Status.CREATED).entity(createdEvent).build())
                .onFailure().recoverWithItem(th -> {
                    if (th instanceof RuntimeException) {
                        if (th.getMessage().equals("No rows affected")) {
                            return Response.status(Response.Status.NO_CONTENT).entity("No rows affected").build();
                        } else if (th.getMessage().contains("Event duration overlaps with an existing event")) {
                            // Replace with the appropriate check for your application
                            return Response.status(Response.Status.CONFLICT).entity("Event duration overlaps with an existing event").build();
                        }
                    }
                    // Handle other exceptions
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(th.getMessage()).build();
                });
    }
    

    @GET
    @Path("/{id}")
    public Uni<Response> getEvent(@PathParam("id") Long id) {
        return eventDao.getEvent(id)
                .onItem().transform(event -> {
                    if (event == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    } else {
                        return Response.ok(event).build();
                    }
                });
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updateEvent(@PathParam("id") int id, Event event) {
        event.setId(id);
        return eventDao.updateEvent(event)
                .onItem().transform(updated -> Response.ok().build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteEvent(@PathParam("id") int id) {
        return eventDao.deleteEvent(id)
                .onItem().transform(deleted -> Response.noContent().build());
    }


    @Inject
    ReservationDao dao;

    @POST
    @Path("/{event_id}/seats/{seat_id}/reservation")
    public Uni<Response> addReservation( @PathParam("event_id") int eventId, @PathParam("seat_id") int seatId,Reservation reservation) {
        reservation.setEventId(eventId);
        reservation.setSeatId(seatId);
        return dao.addReservation(reservation)
                .onItem().transform(createdEvent -> Response.status(Response.Status.CREATED).entity(createdEvent).build())
                .onFailure().recoverWithItem(th -> {
                    if (th instanceof RuntimeException) {
                        if (th.getMessage().equals("No rows affected")) {
                            return Response.status(Response.Status.NO_CONTENT).entity("No rows affected").build();
                        } else if (th.getMessage().contains("Seat cannot be reserved")) {
                            // Replace with the appropriate check for your application
                            return Response.status(Response.Status.CONFLICT).entity("Seat cannot be reserved").build();
                        }
                    }
                    // Handle other exceptions
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(th.getMessage()).build();
                });
    }

    @PUT
    @Path("/{event_id}/seats/{seat_id}/reservation/{reservation_id}")
    public Uni<Void> cancelReservation(@PathParam("reservation_id") int reservationId) {
        return dao.cancelReservation(reservationId);
    }
}
