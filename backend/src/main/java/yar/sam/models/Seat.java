package yar.sam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Seat {
    private int id;
    private int row;
    private int col;

    @JsonProperty("venue_section_id")
    private VenueSection venueSection;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public VenueSection getVenueSection() {
        return venueSection;
    }

    public void setVenueSection(VenueSection venueSection) {
        this.venueSection = venueSection;
    }
}
