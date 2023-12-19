package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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
public class VenueResource {

    @Inject
    VenueDao venueDao;

    @Inject
    VenueSectionDao venueSectionDao;

    @Inject
    SeatDao seatDao;

    @GET
    public Uni<List<Venue>> getVenues() {
        return venueDao.getVenues();
    }

    @POST
    public Uni<Venue> createVenue(Venue venue) {
        return venueDao.addVenue(venue);
    }

    @PUT
    @Path("/{id}")
    public Uni<Void> updateVenue(@PathParam("id") int id, Venue venue) {
        venue.setId(id);
        return venueDao.updateVenue(venue);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteVenue(@PathParam("id") int id) {
        return venueDao.deleteVenue(id);
    }

    @GET
    @Path("/{id}/venue-sections")
    public Uni<List<VenueSection>> getVenueSections(@PathParam("id") int venueId) {
        return venueSectionDao.getVenueSections(venueId);
    }

    @GET
    @Path("/{id}/venue-sections/{venue_section_id}")
    public Uni<VenueSection> getAVenueSection(@PathParam("id") int id, @PathParam("venue_section_id") int venueSectionId) {
        return venueSectionDao.getVenueSection(id, venueSectionId);
    }

    @POST
    @Path("/{id}/venue-sections")
    public Uni<VenueSection> createVenueSection(VenueSection venueSection) {
        return venueSectionDao.addVenueSection(venueSection);
    }

    @PUT
    @Path("/{id}/venue-sections/{venue_section_id}")
    public Uni<Void> updateVenueSection(@PathParam("id") int id, @PathParam("venue_section_id") int venueSectionId, VenueSection venueSection) {
        venueSection.setId(venueSectionId);
        venueSection.setVenueId(id);
        return venueSectionDao.updateVenueSection(venueSection);
    }

    @DELETE
    @Path("/{id}/venue-sections/{venue_section_id}")
    public Uni<Void> deleteVenueSection(@PathParam("id") int id,@PathParam("venue_section_id") int venueSectionId) {
        return venueSectionDao.deleteVenueSection(venueSectionId);
    }

    @GET
    @Path("/{id}/venue-sections/{venue_section_id}/seats")
    public Uni<List<Seat>> getSeats(@PathParam("venue_section_id") int venueSectionId) {
        return seatDao.getSeats(venueSectionId);
    }
    
    @GET
    @Path("/{id}/venue-sections/{venue_section_id}/seats/{seat_id}")
    public Uni<Seat> getSeat(@PathParam("seat_id") int seatId) {
        return seatDao.getSeat(seatId);
    }

    @POST
    @Path("/{id}/venue-sections/{venue_section_id}/seats")
    public Uni<Seat> createSeat(@PathParam("venue_section_id") int venueSectionId,Seat seat) {
        seat.setVenueSectionId(venueSectionId);
        return seatDao.addSeat(seat);
    }

    @POST
    @Path("/{id}/venue-sections/{venue_section_id}/seats/multi")
    public Uni<List<Seat>> createSeats(@PathParam("venue_section_id") int venueSectionId,List<Seat> seats) {
        List<Seat> updatedSeats = seats.stream().map(seat->{
            seat.setVenueSectionId(venueSectionId);
            return seat;
        }).collect(Collectors.toList());

        return seatDao.addSeats(updatedSeats);
    }

    @PUT
    @Path("/{id}/venue-sections/{venue_section_id}/seats/{seat_id}")
    public Uni<Void> updateSeat(@PathParam("venue_section_id") int venueSectionId, @PathParam("seat_id") int seatId, Seat seat) {
        seat.setVenueSectionId(venueSectionId);
        seat.setId(seatId);
        return seatDao.updateSeat(seat);
    }

    @DELETE
    @Path("/{id}/venue-sections/{venue_section_id}/seats/{seat_id}")
    public Uni<Void> deleteSeat(@PathParam("venue_section_id") int venueSectionId, @PathParam("seat_id") int seatId ) {
        return seatDao.deleteSeat(seatId);
    }

}
