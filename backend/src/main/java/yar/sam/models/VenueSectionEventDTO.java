package yar.sam.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VenueSectionEventDTO extends VenueSection {
    @JsonProperty("event_id")
    private Long eventId;
    private List<SeatEventInfo> seats;
    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    public List<SeatEventInfo> getSeats() {
        return seats;
    }
    public void setSeats(List<SeatEventInfo> seats) {
        this.seats = seats;
    }
}
