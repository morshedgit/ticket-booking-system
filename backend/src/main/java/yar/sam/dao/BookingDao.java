package yar.sam.dao;
import java.util.List;
import java.util.function.Function;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import yar.sam.models.Booking;

@Singleton
public class BookingDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(BookingDao.class);

    Function<Row, Booking> bookingMapper = row -> {
        Booking booking = new Booking();
        booking.setId(row.getInteger("id"));
        booking.setEventId(row.getInteger("event_id"));
        booking.setSeatId(row.getInteger("seat_id"));
        booking.setAccountId(row.getInteger("account_id"));

        return booking;
    };

    public Uni<Booking> addBooking(Booking Booking) {
        String query = """
                    SELECT * FROM book_seat($1, $2, $3)
                """;
        return this.create(query, List.of(Booking.getEventId(), Booking.getSeatId(), Booking.getAccountId()), bookingMapper);
    }

    public Uni<Void> cancelBooking(int BookingId) {
        // Assuming that canceling a Booking means deleting it from the database
        return client.preparedQuery("UPDATE Booking SET status = 'CANCELED'::Booking_status WHERE id = $1")
            .execute(Tuple.of(BookingId))
            .onItem().transformToUni(ignored -> Uni.createFrom().voidItem());
    }
}
