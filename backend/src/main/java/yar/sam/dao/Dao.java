package yar.sam.dao;
import java.util.List;
import java.util.function.Function;


import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.inject.Singleton;
import yar.sam.models.Seat;
import yar.sam.models.SeatDTO;

@Singleton
public class Dao extends BaseDao {

    Function<Row,Seat> seatMapper = row -> {
        Seat createdSeat = new Seat();
        createdSeat.setId(row.getInteger("id")); // Adjust if your ID is of a different type
        createdSeat.setRow(row.getInteger("row"));
        createdSeat.setCol(row.getInteger("col"));
        return createdSeat;
    };
    
    public Uni<List<Seat>> getAllSeats() {        

        String query =  """
                            SELECT * FROM seat;
                        """;
        // Use non-blocking operations with Uni
        return this.readAll(query, List.of(), seatMapper);
    }
    
    public Uni<Seat> getSeat(Long id) {        

        String query =  """
                            SELECT * FROM seat WHERE (reserved <> TRUE OR booked <> TRUE) AND id = $1;
                        """;
        // Use non-blocking operations with Uni
        return this.read(query, List.of(id), seatMapper);
    }

    public Uni<Seat> addSeat(SeatDTO seat) {

        int seatRow = seat.getRow();
        int seatCol = seat.getCol();

        String query =  """
                            INSERT INTO seat (row, col, booked, client_id) VALUES ($1, $2, FALSE, NULL) RETURNING *;
                        """;
        // Use non-blocking operations with Uni
        return this.create(query, List.of(seatRow, seatCol), seatMapper);
        
    }

    public Uni<Seat> reserveSeat(Long id, Long clientId) {

        String query =  """
                            UPDATE seat
                            SET reserved = TRUE, client_id = $2
                            WHERE (reserved <> TRUE OR booked <> TRUE) AND id = $1 RETURNING *;
                        """;
        // Use non-blocking operations with Uni
        return this.create(query, List.of(id, clientId), seatMapper);        
    }

    public Uni<Seat> unreserveSeat(Long id) {

        String query =  """
                            UPDATE seat
                            SET reserved = FALSE,client_id = NULL
                            WHERE id = $1 RETURNING *;
                        """;
        // Use non-blocking operations with Uni
        return this.create(query, List.of(id), seatMapper);        
    }

    public Uni<Seat> bookSeat(Long id, Long clientId) {

        String query =  """
                            UPDATE seat
                            SET booked = TRUE, client_id = $2
                            WHERE (reserved <> TRUE OR booked <> TRUE) AND id = $1 RETURNING *;
                        """;
        // Use non-blocking operations with Uni
        return this.create(query, List.of(id, clientId), seatMapper);        
    }

    public Uni<Seat> unbookSeat(Long id) {

        String query =  """
                            UPDATE seat
                            SET booked = FALSE,client_id = NULL
                            WHERE id = $1 RETURNING *;
                        """;
        // Use non-blocking operations with Uni
        return this.create(query, List.of(id), seatMapper);        
    }
}