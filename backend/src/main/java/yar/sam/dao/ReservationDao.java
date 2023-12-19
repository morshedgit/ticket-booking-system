package yar.sam.dao;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import yar.sam.models.Reservation;

@Singleton
public class ReservationDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(ReservationDao.class);

    public Uni<Void> addReservation(Reservation reservation) {
        return client.preparedQuery("SELECT * FROM reserve_seat($1, $2, $3)")
            .execute(Tuple.of(reservation.getEventId(), reservation.getSeatId(), reservation.getAccountId()))
            .onItem().transformToUni(ignored -> Uni.createFrom().voidItem());
    }

    public Uni<Void> cancelReservation(int reservationId) {
        // Assuming that canceling a reservation means deleting it from the database
        return client.preparedQuery("UPDATE reservation SET status = 'CANCELED'::reservation_status WHERE id = $1")
            .execute(Tuple.of(reservationId))
            .onItem().transformToUni(ignored -> Uni.createFrom().voidItem());
    }
}
