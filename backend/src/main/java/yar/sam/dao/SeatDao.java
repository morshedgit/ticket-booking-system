package yar.sam.dao;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import yar.sam.models.Seat;

@Singleton
public class SeatDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(SeatDao.class);

    Function<Row, Seat> seatMapper = row -> {
        Seat seat = new Seat();
        seat.setId(row.getInteger("id"));
        seat.setRow(row.getInteger("row"));
        seat.setCol(row.getInteger("col"));
        seat.setVenueSectionId(row.getInteger("venue_section_id"));
        return seat;
    };

    public Uni<List<Seat>> getSeats(int venue_section_id) {
        String query = "SELECT * FROM seat WHERE venue_section_id = $1";
        return this.readAll(query, List.of(venue_section_id), seatMapper);
    }

    public Uni<Seat> getSeat(int seat_id) {
        String query = "SELECT * FROM seat WHERE id = $1";
        return this.read(query, List.of(seat_id), seatMapper);
    }

    public Uni<Seat> addSeat(Seat seat) {
        String query = "INSERT INTO seat (row, col, venue_section_id) VALUES ($1, $2, $3) RETURNING *";
        return this.create(query, List.of(seat.getRow(), seat.getCol(), seat.getVenueSectionId()), seatMapper);
    }

    public Uni<List<Seat>> addSeats(List<Seat> seats) {
        String query = "INSERT INTO seat (row, col, venue_section_id) VALUES ($1, $2, $3) RETURNING *";

        List<Tuple> batch = seats.stream()
            .map(seat -> Tuple.of(seat.getRow(), seat.getCol(), seat.getVenueSectionId()))
            .collect(Collectors.toList());

        return client.preparedQuery(query)
                    .executeBatch(batch)
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .onItem().transform(row -> seatMapper.apply(row))
                    .collect().asList();
    }


    public Uni<Void> updateSeat(Seat seat) {
        String query = "UPDATE seat SET row = $1, col = $2, venue_section_id = $3 WHERE id = $4";
        return this.update(query, List.of(seat.getRow(), seat.getCol(), seat.getVenueSectionId(), seat.getId()));
    }

    public Uni<Void> deleteSeat(int seatId) {
        String query = "DELETE FROM seat WHERE id = $1";
        return this.delete(query, List.of(seatId));
    }
}
