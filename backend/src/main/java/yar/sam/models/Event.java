package yar.sam.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
    private int id;

    @JsonProperty("event_start_time")
    private LocalDateTime eventStartTime;

    @JsonProperty("event_end_time")
    private LocalDateTime eventEndTime;

    @JsonProperty("venue_id")
    private int venueId;
    private Venue venue;
    private EventStatus status;

    @JsonProperty("client_id")
    private int clientId;

    private Client client;

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    @JsonProperty("sections")
    private List<VenueSectionEventDTO> venueSections;


    public List<VenueSectionEventDTO> getVenueSections() {
        return venueSections;
    }
    public void setVenueSections(List<VenueSectionEventDTO> venueSections) {
        this.venueSections = venueSections;
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
    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }
    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }
    public LocalDateTime getEventEndTime() {
        return eventEndTime;
    }
    public void setEventEndTime(LocalDateTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    public EventStatus getStatus() {
        return status;
    }
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    // Getters and setters
}
