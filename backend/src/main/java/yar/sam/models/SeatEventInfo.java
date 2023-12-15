package yar.sam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeatEventInfo {
    private Seat seat;
    private Float price;
    @JsonProperty("reserve_status")
    private ReservationStatus reservationStatus;
    @JsonProperty("booking_status")
    private BookingStatus bookingStatus;
    
    public Seat getSeat() {
        return seat;
    }
    public void setSeat(Seat seat) {
        this.seat = seat;
    }
    public Float getPrice() {
        return price;
    }
    public void setPrice(Float price) {
        this.price = price;
    }
    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }
    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }
    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
    
}
