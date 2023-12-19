package yar.sam.dao;

import java.util.List;
import java.util.function.Function;
import jakarta.inject.Singleton;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import yar.sam.models.Pricing;

@Singleton
public class PricingDao extends BaseDao {

    private Function<Row, Pricing> pricingMapper = row -> {
        Pricing pricing = new Pricing();
        pricing.setId(row.getInteger("id"));
        pricing.setEventId(row.getInteger("event_id"));
        pricing.setVenueId(row.getInteger("venue_id"));
        pricing.setVenueSectionId(row.getInteger("venue_section_id"));
        pricing.setPrice(row.getBigDecimal("price"));
        // Map Event, Venue, VenueSection if needed
        return pricing;
    };

    // CRUD operations
    public Uni<List<Pricing>> getAllPricings(int venueId, int eventId) {
        String query = "SELECT * FROM pricing WHERE venue_id = $1 AND event_id = $2";
        return this.readAll(query, List.of(venueId,eventId), pricingMapper);
    }

    public Uni<Pricing> addPricing(Pricing pricing) {

        String query = """
                    INSERT INTO pricing (event_id, venue_id, venue_section_id, price)
                    SELECT $1, $2, $3, $4
                    WHERE NOT EXISTS (
                        SELECT 1 FROM pricing WHERE event_id = $1 AND venue_id = $2 AND venue_section_id = $3
                    )
                    RETURNING *;            
                """;
        return this.create(query, List.of(pricing.getEventId(), pricing.getVenueId(), pricing.getVenueSectionId()), pricingMapper);
    }    

    public Uni<Void> updatePricing(Pricing pricing) {
        String query = "UPDATE pricing SET event_id = $1, venue_id = $2, venue_section_id = $3, price = $4 WHERE id = $5";
        return this.update(query, List.of(pricing.getEventId(), pricing.getVenueId(), pricing.getVenueSectionId(), pricing.getPrice(), pricing.getId()));
    }

    public Uni<Void> deletePricing(int pricingId) {
        String query = "DELETE FROM pricing WHERE id = $1";
        return this.delete(query, List.of(pricingId));
    }
}
