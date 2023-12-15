package yar.sam.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import yar.sam.models.Account;
import yar.sam.models.Address;
import yar.sam.models.Venue;
import yar.sam.models.VenueSection;

@Singleton
public class VenueDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(VenueDao.class);

    Function<Row, Venue> venueMapper = row -> {
        ObjectMapper objectMapper = new ObjectMapper();
        Venue venue = new Venue();
        venue.setId(row.getInteger("id"));
        venue.setName(row.getString("name"));
        JsonObject addressJsonObject = row.getJsonObject("address");
        if (addressJsonObject != null) {
            try {
                venue.setAddress(objectMapper.readValue(addressJsonObject.encode(), Address.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        List<Account> managers = new ArrayList<>();
        JsonArray managersJsonArray = row.getJsonArray("managers");
        if (managersJsonArray != null) {
            try {
                managers = objectMapper.readValue(managersJsonArray.encode(), new TypeReference<List<Account>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        venue.setManagers(managers);

        List<VenueSection> sections = new ArrayList<>();
        JsonArray sectionsJsonArray = row.getJsonArray("sections");
        if (sectionsJsonArray != null) {
            try {
                sections = objectMapper.readValue(sectionsJsonArray.encode(), new TypeReference<List<VenueSection>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        venue.setVenueSections(sections);

        return venue;
    };



    public Uni<List<Venue>> getVenues() {
        String query = """
            SELECT 
                vn.*,
                json_agg( row_to_json(mg.*)        
                ) AS managers,
                json_agg(row_to_json(sc.*)
                ) AS sections,
                (SELECT row_to_json(ad.*) FROM address ad WHERE ad.id = vn.address_id) AS address
            FROM "venue" vn
            LEFT JOIN (
                SELECT 
                    ac.*,
                    json_agg( row_to_json(ct.*)
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
            ) mg ON mg.id = ANY(vn.manager_ids)
            LEFT JOIN (
                SELECT
                    vs.*,
                    json_agg(row_to_json(st.*)) AS seats
                FROM 
                venue_section vs
                LEFT JOIN seat st ON st.venue_section_id = vs.id
                GROUP BY vs.id
            ) sc ON sc.venue_id = vn.id
            GROUP BY vn.id
            """;
        return this.readAll(query, List.of(), venueMapper);
    }

    public Uni<Venue> addVenue(Venue venue) {
        String query = """
            INSERT INTO venue (name, address_id) 
                VALUES ($1, $2) 
                RETURNING *, 
                    (SELECT row_to_json((SELECT d FROM (SELECT id, line_1, line_2, city, province, country_code, postal_code FROM address WHERE id = address_id) d)) AS address),
                    '[]'::jsonb AS sections,
                    '[]'::jsonb AS managers;
                """;
        return this.create(query, List.of(venue.getName(), venue.getAddressId()), venueMapper);
    }

    public Uni<Void> updateVenue(Venue venue) {
        String query = "UPDATE venue SET name = $1, address_id = $2, manager_ids = $3 WHERE id = $4";
        return this.update(query, List.of(venue.getName(), venue.getAddressId(), venue.getManagerIds().toArray(new Integer[0]), venue.getId()));
    }

    public Uni<Void> deleteVenue(int venueId) {
        String query = "DELETE FROM venue WHERE id = $1";
        return this.delete(query, List.of(venueId));
    }
    
}
