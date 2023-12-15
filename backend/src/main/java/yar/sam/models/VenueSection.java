package yar.sam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VenueSection {
    private int id;
    private String name;

    @JsonProperty("venue_id")
    private Venue venue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}

