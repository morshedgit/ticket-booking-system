package yar.sam.dao;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import yar.sam.api.AccountResource;
import yar.sam.util.PaginationParams;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import org.jboss.logging.Logger;

public class BaseDao {

    @Inject
    protected PgPool client;

    private static final Logger LOGGER = Logger.getLogger(AccountResource.class);

    // Generic Read operation
    public <T> Uni<T> read(String sql, List<Object> params, Function<Row, T> mapper) {
        return client.preparedQuery(sql)
                     .execute(Tuple.tuple(params))                     
                    .onItem().transformToUni(rows -> {
                            if (!rows.iterator().hasNext()) {
                                // Return a null item when no rows are found
                                return Uni.createFrom().failure(new NoSuchElementException("No rows affected"));
                            } else {
                                return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                            }
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


    public <T> Uni<List<T>> readAll(String baseSql, List<Object> params, Function<Row, T> mapper, 
                                    @BeanParam PaginationParams paginationParams) {
        // Create a new mutable list from the original params
        List<Object> mutableParams = new ArrayList<>(params);

        // Initialize SQL query with base SQL
        StringBuilder paginatedSql = new StringBuilder(baseSql);

        // Append ORDER BY clause if sort is present
        String sort = paginationParams.getSort();
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");
            paginatedSql.append(" ORDER BY ").append(sortParts[0]).append(" ")
                        .append(sortParts.length > 1 ? sortParts[1] : "asc");
        }

        // Calculate offset and append LIMIT and OFFSET clauses
        int page = paginationParams.getPage();
        int size = paginationParams.getSize();
        int offset = (page - 1) * size;
        int limitPlaceholder = mutableParams.size() + 1;
        int offsetPlaceholder = mutableParams.size() + 2;

        paginatedSql.append(" LIMIT $").append(limitPlaceholder)
                    .append(" OFFSET $").append(offsetPlaceholder);

        // Append pagination parameters to the mutable list
        mutableParams.add(size);
        mutableParams.add(offset);

        String finalQuery = paginatedSql.toString();

        LOGGER.info(finalQuery);

        // Execute query
        return client.preparedQuery(finalQuery)
                    .execute(Tuple.tuple(mutableParams))
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .map(mapper)
                    .collect().asList();
    }

    public <T> Uni<List<T>> readAll(String baseSql, List<Object> params, Function<Row, T> mapper, 
                                    @BeanParam PaginationParams paginationParams, String orderByTablePrefix) {
        // Create a new mutable list from the original params
        List<Object> mutableParams = new ArrayList<>(params);

        // Initialize SQL query with base SQL
        StringBuilder paginatedSql = new StringBuilder(baseSql);

        // Append ORDER BY clause if sort is present
        String sort = paginationParams.getSort();
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");
            paginatedSql.append(" ORDER BY ").append(orderByTablePrefix).append(".").append(sortParts[0]).append(" ")
                        .append(sortParts.length > 1 ? sortParts[1] : "asc");
        }

        // Calculate offset and append LIMIT and OFFSET clauses
        int page = paginationParams.getPage();
        int size = paginationParams.getSize();
        int offset = (page - 1) * size;
        int limitPlaceholder = mutableParams.size() + 1;
        int offsetPlaceholder = mutableParams.size() + 2;

        paginatedSql.append(" LIMIT $").append(limitPlaceholder)
                    .append(" OFFSET $").append(offsetPlaceholder);

        // Append pagination parameters to the mutable list
        mutableParams.add(size);
        mutableParams.add(offset);

        String finalQuery = paginatedSql.toString();

        LOGGER.info(finalQuery);

        // Execute query
        return client.preparedQuery(finalQuery)
                    .execute(Tuple.tuple(mutableParams))
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .map(mapper)
                    .collect().asList();
    }


    public <T> Uni<List<T>> readAll(String baseSql, List<Object> params, Function<Row, T> mapper) {

        // Execute query
        return client.preparedQuery(baseSql.toString())
                    .execute(Tuple.tuple(params))
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .map(mapper)
                    .collect().asList();
    }



    // Basic Update operation
    public <T> Uni<Void> update(String sql, List<Object> params) {
        return client.preparedQuery(sql).execute(Tuple.tuple(params))
                     .onFailure().transform(throwable -> new SQLException("Error executing query", throwable))
                     .onItem().transformToUni(rows -> {
                         if (rows.rowCount() == 0) {
                             return Uni.createFrom().failure(new NoSuchElementException("No rows affected"));
                         } else {
                            //  return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                            return Uni.createFrom().nullItem(); // Return Uni<Void> for success
                         }
                        });// Ensure a Uni<Void> is returned on failure
    }

    // Basic Delete operation
    public <T> Uni<Void> delete(String sql, List<Object> params) {
        return client.preparedQuery(sql).execute(Tuple.tuple(params))
                     .onItem().transformToUni(rows -> {
                         if (rows.rowCount() == 0) {
                             return Uni.createFrom().failure(new NoSuchElementException("No rows affected"));
                         } else {
                            //  return Uni.createFrom().item(mapper.apply(rows.iterator().next()));
                            return Uni.createFrom().nullItem(); // Return Uni<Void> for success
                         }
                     });
    }
}
