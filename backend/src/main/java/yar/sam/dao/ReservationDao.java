package yar.sam.dao;
import java.util.List;
import java.util.function.Function;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import yar.sam.models.Reservation;

@Singleton
public class ReservationDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(ReservationDao.class);

    Function<Row, Reservation> reservationMapper = row -> {
        Reservation reservation = new Reservation();
        reservation.setId(row.getInteger("id"));
        reservation.setEventId(row.getInteger("event_id"));
        reservation.setSeatId(row.getInteger("seat_id"));
        reservation.setAccountId(row.getInteger("account_id"));

        return reservation;
    };

    public Uni<Reservation> addReservation(Reservation reservation) {
        String query = """
                    SELECT * FROM reserve_seat($1, $2, $3)
                """;
        return this.create(query, List.of(reservation.getEventId(), reservation.getSeatId(), reservation.getAccountId()), reservationMapper);
    }

    public Uni<Void> cancelReservation(int reservationId) {
        // Assuming that canceling a reservation means deleting it from the database
        return client.preparedQuery("UPDATE reservation SET status = 'CANCELED'::reservation_status WHERE id = $1")
            .execute(Tuple.of(reservationId))
            .onItem().transformToUni(ignored -> Uni.createFrom().voidItem());
    }
}
