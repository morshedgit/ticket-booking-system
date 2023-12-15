package yar.sam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation {
    private int id;

    @JsonProperty("event_id")
    private Event event;

    @JsonProperty("seat_id")
    private Seat seat;
    private ReservationStatus status;

    @JsonProperty("account_id")
    private Account account;

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

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    // Getters and setters
}
