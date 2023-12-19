package yar.sam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {
    private int id;

    @JsonProperty("event_id")
    private int eventId;

    @JsonProperty("seat_id")
    private int seatId;

    private BookingStatus status;
    
    @JsonProperty("account_id")
    private int accountId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }    

}