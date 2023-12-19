package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import yar.sam.dao.BookingDao;
import yar.sam.dao.EventDao;
import yar.sam.dao.PricingDao;
import yar.sam.dao.ReservationDao;
import yar.sam.models.Booking;
import yar.sam.models.Event;
import yar.sam.models.Pricing;
import yar.sam.models.Reservation;

import java.util.List;

import io.smallrye.mutiny.Uni;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventDao eventDao;

    @GET
    public Uni<List<Event>> getAllEvents() {
        return eventDao.getEvents();
    }

    @POST
    public Uni<Event> createEvent(Event event) {
        return eventDao.addEvent(event);
    }
    

    @GET
    @Path("/{id}")
    public Uni<Event> getEvent(@PathParam("id") Long id) {
        return eventDao.getEvent(id);
    }

    @PUT
    @Path("/{id}")
    public Uni<Void> updateEvent(@PathParam("id") int id, Event event) {
        event.setId(id);
        return eventDao.updateEvent(event);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteEvent(@PathParam("id") int id) {
        return eventDao.deleteEvent(id);
    }


    @Inject
    ReservationDao reservationDao;

    @POST
    @Path("/{event_id}/seats/{seat_id}/reservations")
    public Uni<Reservation> addReservation( @PathParam("event_id") int eventId, @PathParam("seat_id") int seatId,Reservation reservation) {
        reservation.setEventId(eventId);
        reservation.setSeatId(seatId);
        return reservationDao.addReservation(reservation);
    }

    @PUT
    @Path("/{event_id}/seats/{seat_id}/reservations/{reservation_id}")
    public Uni<Void> cancelReservation(@PathParam("reservation_id") int reservationId) {
        return reservationDao.cancelReservation(reservationId);
    }


    @Inject
    BookingDao bookingDao;

    @POST
    @Path("/{event_id}/seats/{seat_id}/bookings")
    public Uni<Booking> addBooking( @PathParam("event_id") int eventId, @PathParam("seat_id") int seatId,Booking booking) {
        booking.setEventId(eventId);
        booking.setSeatId(seatId);
        return bookingDao.addBooking(booking);
    }

    @PUT
    @Path("/{event_id}/seats/{seat_id}/bookings/{booking_id}")
    public Uni<Void> cancelBooking(@PathParam("booking_id") int bookingId) {
        return bookingDao.cancelBooking(bookingId);
    }



    @Inject
    PricingDao pricingDao;

    @GET
    @Path("/{event_id}/venues/{venue_id}/pricings")
    public Uni<List<Pricing>> getPricings(@PathParam("venue_id") int venueId, @PathParam("event_id") int eventId) {
        return pricingDao.getAllPricings(venueId,eventId);
    }

    @POST
    @Path("/{event_id}/venues/{venue_id}/pricings")
    public Uni<Pricing> createPricing(@PathParam("venue_id") int venueId, @PathParam("event_id") int eventId,Pricing pricing) {
        pricing.setVenueId(venueId);
        pricing.setEventId(eventId);
        return pricingDao.addPricing(pricing);
    }

    @PUT
    @Path("/{event_id}/venues/{venue_id}/pricings/{pricing_id}")
    public Uni<Void> updatePricing(@PathParam("venue_id") int venueId, @PathParam("event_id") int eventId, @PathParam("pricing_id") int pricingId, Pricing pricing) {
        pricing.setId(pricingId);
        pricing.setVenueId(venueId);
        pricing.setEventId(eventId);
        return pricingDao.updatePricing(pricing);
    }

    @DELETE
    @Path("/{event_id}/pricings/{pricing_id}")
    public Uni<Void> deletePricing(@PathParam("pricing_id") int id) {
        return pricingDao.deletePricing(id);
    }
}
