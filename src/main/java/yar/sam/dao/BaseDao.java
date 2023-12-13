package yar.sam.dao;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class BaseDao {

    @Inject
    protected PgPool client;

    // Generic Read operation
    public <T> Uni<T> read(String sql, List<Object> params, Function<Row, T> mapper) {
        return client.preparedQuery(sql)
                     .execute(Tuple.tuple(params))
                     .onItem().transformToUni(rows -> {
                             return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                     });
    }
    // Generic Create operation
    public <T> Uni<T> create(String sql, List<Object> params, Function<Row, T> mapper) {
        return client.preparedQuery(sql)
                     .execute(Tuple.tuple(params))
                     .onItem().transformToUni(rows -> {
                         if (rows.rowCount() == 0) {
                             return Uni.createFrom().failure(new RuntimeException("No rows affected"));
                         } else {
                             return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                         }
                     });
    }

    // Method to read multiple rows
    public <T> Uni<List<T>> readAll(String sql, List<Object> params, Function<Row, T> mapper) {
        return client.preparedQuery(sql)
                    .execute(Tuple.tuple(params))
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .map(mapper)
                    .collect().asList();
    }


    // Basic Update operation
    public <T> Uni<T> update(String sql, List<Object> params, Function<Row, T> mapper) {
        return client.preparedQuery(sql).execute(Tuple.tuple(params))
                     .onFailure().transform(throwable -> new SQLException("Error executing query", throwable))
                     .onItem().transformToUni(rows -> {
                         if (rows.rowCount() == 0) {
                             return Uni.createFrom().failure(new RuntimeException("No rows affected"));
                         } else {
                             return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                         }
                     });
    }

    // Basic Delete operation
    public <T> Uni<T> delete(String sql, List<Object> params, Function<Row, T> mapper) {
        return client.preparedQuery(sql).execute(Tuple.tuple(params))
                     .onItem().transformToUni(rows -> {
                         if (rows.rowCount() == 0) {
                             return Uni.createFrom().failure(new RuntimeException("No rows affected"));
                         } else {
                             return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                         }
                     });
    }
}
