package yar.sam.dao;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BeanParam;
import yar.sam.models.Account;
import yar.sam.models.Client;
import yar.sam.util.PaginationParams;

@Singleton
public class ClientDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(ClientDao.class);

    Function<Row, Client> clientMapper = row -> {
        ObjectMapper objectMapper = new ObjectMapper();
        Client client = new Client();
        client.setId(row.getInteger("id"));
        client.setName(row.getString("name"));
        client.setManagerIds(Arrays.asList(row.getArrayOfIntegers("manager_ids")));

        try {
            JsonArray managersJsonArray = row.getJsonArray("managers");
            List<Account> managers = objectMapper.readValue(managersJsonArray.encode(), new TypeReference<List<Account>>() {});
            client.setManagers(managers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }

        return client;
    };

    // CRUD operations for Client
    public Uni<List<Client>> getClients(@BeanParam PaginationParams paginationParams) {
        String query = """
            SELECT 
                cl.*,
                json_agg( row_to_json(mg.*)
                ) AS managers
            FROM "client" cl
            LEFT JOIN (
                SELECT 
                    ac.*,
                    json_agg(row_to_json(ct.*)
                    ) AS contacts
                FROM "account" ac
                LEFT JOIN (
                    SELECT 
                        ct.*,
                        json_agg(row_to_json(ad.*)) AS addresses
                    FROM contact ct
                    LEFT JOIN address ad ON ad.id = ANY(ct.address_ids)
                    GROUP BY ct.id
                ) ct ON ct.id = ANY(ac.contact_ids)
                GROUP BY ac.id
            ) mg ON mg.id = ANY(cl.manager_ids)
            GROUP BY cl.id
                """;
        return this.readAll(query, List.of(), clientMapper,paginationParams, "cl");
    }

    public Uni<Client> addClient(Client client) {
        String query = "INSERT INTO client (name) VALUES ($1) RETURNING *, '[]'::jsonb AS managers";
        return this.create(query, List.of(client.getName()),clientMapper);
    }

    public Uni<Void> updateClient(Client client) {
        String query = "UPDATE client SET name = $1, manager_ids = $2 WHERE id = $3";
        return this.update(query, List.of(client.getName(), client.getManagerIds().toArray(new Integer[0]), client.getId()));
    }

    public Uni<Void> deleteClient(int clientId) {
        String query = "DELETE FROM client WHERE id = $1";
        return this.delete(query, List.of(clientId));
    }
}
