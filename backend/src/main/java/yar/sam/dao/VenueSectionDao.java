package yar.sam.dao;

import java.util.List;
import java.util.function.Function;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.inject.Singleton;
import yar.sam.models.VenueSection;

@Singleton
public class VenueSectionDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(VenueSectionDao.class);

    Function<Row, VenueSection> venueSectionMapper = row -> {
        VenueSection venueSection = new VenueSection();
        venueSection.setId(row.getInteger("id"));
        venueSection.setName(row.getString("name"));
        venueSection.setVenueId(row.getInteger("venue_id"));
        return venueSection;
    };

    public Uni<List<VenueSection>> getVenueSections(int venueId) {
        String query = "SELECT * FROM venue_section WHERE venue_id = $1";
        return this.readAll(query, List.of(venueId), venueSectionMapper);
    }

    public Uni<VenueSection> getVenueSection(int venueId,int venueSectionId) {
        String query = "SELECT * FROM venue_section WHERE venue_id = $1 AND id = $2";
        return this.read(query, List.of(venueId,venueSectionId), venueSectionMapper);
    }

    public Uni<VenueSection> addVenueSection(VenueSection venueSection) {
        String query = "INSERT INTO venue_section (name, venue_id) VALUES ($1, $2) RETURNING *";
        return this.create(query, List.of(venueSection.getName(), venueSection.getVenueId()), venueSectionMapper);
    }

    public Uni<Void> updateVenueSection(VenueSection venueSection) {
        String query = "UPDATE venue_section SET name = $1, venue_id = $2 WHERE id = $3";
        return this.update(query, List.of(venueSection.getName(), venueSection.getVenueId(), venueSection.getId()));
    }

    public Uni<Void> deleteVenueSection(int venueSectionId) {
        String query = "DELETE FROM venue_section WHERE id = $1";
        return this.delete(query, List.of(venueSectionId));
    }
}
