package yar.sam.models;


import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Pricing {
    private int id;

    @JsonProperty("event_id")
    private int eventId;

    private Event event;

    @JsonProperty("venue_id")
    private int venueId;

    private Venue venue;

    @JsonProperty("venue_section_id")
    private int venueSectionId;

    private VenueSection venueSection;

    private BigDecimal price;

    public int getVenueSectionId() {
        return venueSectionId;
    }
    public void setVenueSectionId(int venueSectionId) {
        this.venueSectionId = venueSectionId;
    }

    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getVenueId() {
        return venueId;
    }
    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }
    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    public VenueSection getVenueSection() {
        return venueSection;
    }
    public void setVenueSection(VenueSection venueSection) {
        this.venueSection = venueSection;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

