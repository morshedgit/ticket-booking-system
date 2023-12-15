package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.EventDao;
import yar.sam.models.Event;
import io.smallrye.mutiny.Uni;

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
                .onItem().transform(createdEvent -> Response.status(Response.Status.CREATED).entity(createdEvent).build());
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
}
