package yar.sam.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Seat {
    private Long id;
    private int row;
    private int col;
    private Boolean booked;
    private Boolean reserved;
    @JsonIgnore
    private Long clientId;
    public Boolean getReserved() {
        return reserved;
    }
    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }
    public Long getClientId() {
        return clientId;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public Boolean getBooked() {
        return booked;
    }
    public void setBooked(Boolean booked) {
        this.booked = booked;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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
    
}
