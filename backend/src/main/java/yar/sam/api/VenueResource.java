package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.SeatDao;
import yar.sam.dao.VenueDao;
import yar.sam.dao.VenueSectionDao;
import yar.sam.models.Seat;
import yar.sam.models.Venue;
import yar.sam.models.VenueSection;

import java.util.List;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Uni;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {

    @Inject
    VenueDao venueDao;

    @Inject
    VenueSectionDao venueSectionDao;

    @Inject
    SeatDao seatDao;

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

    @GET
    @Path("/{id}/venue-sections")
    public Uni<Response> getAllVenueSections(@PathParam("id") int venueId) {
        return venueSectionDao.getVenueSections(venueId)
                .onItem().transform(sections -> Response.ok(sections).build());
    }

    @GET
    @Path("/{id}/venue-sections/{venue_section_id}")
    public Uni<Response> getAVenueSection(@PathParam("id") int id, @PathParam("venue_section_id") int venueSectionId) {
        return venueSectionDao.getVenueSection(id, venueSectionId)
                .onItem().transform(sections -> {
                    if (sections == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    } else {
                        return Response.ok(sections).build();
                    }
                });
    }

    @POST
    @Path("/{id}/venue-sections")
    public Uni<Response> createVenueSection(VenueSection venueSection) {
        return venueSectionDao.addVenueSection(venueSection)
                .onItem().transform(createdSection -> Response.status(Response.Status.CREATED).entity(createdSection).build());
    }

    @PUT
    @Path("/{id}/venue-sections/{venue_section_id}")
    public Uni<Response> updateVenueSection(@PathParam("id") int id, @PathParam("venue_section_id") int venueSectionId, VenueSection venueSection) {
        venueSection.setId(venueSectionId);
        venueSection.setVenueId(id);
        return venueSectionDao.updateVenueSection(venueSection)
                .onItem().transform(updated -> Response.ok().build());
    }

    @DELETE
    @Path("/{id}/venue-sections/{venue_section_id}")
    public Uni<Response> deleteVenueSection(@PathParam("id") int id,@PathParam("venue_section_id") int venueSectionId) {
        return venueSectionDao.deleteVenueSection(venueSectionId)
                .onItem().transform(deleted -> Response.noContent().build());
    }

    @GET
    @Path("/{id}/venue-sections/{venue_section_id}/seats")
    public Uni<Response> getAllSeats(@PathParam("venue_section_id") int venueSectionId) {
        return seatDao.getSeats(venueSectionId)
                .onItem().transform(seats -> Response.ok(seats).build());
    }
    
    @GET
    @Path("/{id}/venue-sections/{venue_section_id}/seats/{seat_id}")
    public Uni<Response> getAllSeat(@PathParam("seat_id") int seatId) {
        return seatDao.getSeat(seatId)
                .onItem().transform(seat -> Response.ok(seat).build());
    }

    @POST
    @Path("/{id}/venue-sections/{venue_section_id}/seats")
    public Uni<Response> createSeat(@PathParam("venue_section_id") int venueSectionId,Seat seat) {
        seat.setVenueSectionId(venueSectionId);
        return seatDao.addSeat(seat)
                .onItem().transform(createdSeat -> Response.status(Response.Status.CREATED).entity(createdSeat).build());
    }

    @POST
    @Path("/{id}/venue-sections/{venue_section_id}/seats/multi")
    public Uni<Response> createSeats(@PathParam("venue_section_id") int venueSectionId,List<Seat> seats) {
        List<Seat> updatedSeats = seats.stream().map(seat->{
            seat.setVenueSectionId(venueSectionId);
            return seat;
        }).collect(Collectors.toList());

        return seatDao.addSeats(updatedSeats)
                .onItem().transform(createdSeat -> Response.status(Response.Status.CREATED).entity(createdSeat).build());
    }

    @PUT
    @Path("/{id}/venue-sections/{venue_section_id}/seats/{seat_id}")
    public Uni<Response> updateSeat(@PathParam("venue_section_id") int venueSectionId, @PathParam("seat_id") int seatId, Seat seat) {
        seat.setVenueSectionId(venueSectionId);
        seat.setId(seatId);
        return seatDao.updateSeat(seat)
                .onItem().transform(updated -> Response.ok().build());
    }

    @DELETE
    @Path("/{id}/venue-sections/{venue_section_id}/seats/{seat_id}")
    public Uni<Response> deleteSeat(@PathParam("venue_section_id") int venueSectionId, @PathParam("seat_id") int seatId ) {
        return seatDao.deleteSeat(seatId)
                .onItem().transform(deleted -> Response.noContent().build());
    }

}
