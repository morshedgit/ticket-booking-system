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
import yar.sam.models.Event;
import yar.sam.models.Venue;
import yar.sam.models.VenueSectionEventDTO;
import yar.sam.models.EventStatus;

@Singleton
public class EventDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(EventDao.class);

    Function<Row, Event> eventMapper = row -> {
        ObjectMapper objectMapper = new ObjectMapper();
        Event event = new Event();
        event.setId(row.getInteger("id"));
        event.setEventStartTime(row.getLocalDateTime("event_start_time"));
        event.setEventEndTime(row.getLocalDateTime("event_end_time"));
        event.setStatus(EventStatus.valueOf(row.getString("status")));

        JsonObject venueJsonObject = row.getJsonObject("venue");
        if (venueJsonObject != null) {
            try {
                event.setVenue(objectMapper.readValue(venueJsonObject.encode(), Venue.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }


        List<VenueSectionEventDTO> venueSections = new ArrayList<>();
        JsonArray sectionsJsonArray = row.getJsonArray("sections");
        if (sectionsJsonArray != null) {
            try {
                venueSections = objectMapper.readValue(sectionsJsonArray.encode(), new TypeReference<List<VenueSectionEventDTO>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        event.setVenueSections(venueSections);
        return event;
    };

    // CRUD operations for Event
    public Uni<List<Event>> getEvents() {
        String query = "SELECT * FROM get_event_by_id(NULL)";
        return this.readAll(query, List.of(), eventMapper);
    }

    public Uni<List<Event>> getEvent(Long eventId) {
        String query = "SELECT * FROM get_event_by_id($1)";
        return this.readAll(query, List.of(eventId), eventMapper);
    }

    public Uni<Event> addEvent(Event event) {
        String query ="""
                    INSERT INTO event (event_start_time, event_end_time, venue_id)
                    VALUES ($1, $2, $3) RETURNING id AS new_event_id, (SELECT * FROM get_event_by_id(id));
                """;;
        return this.create(query, 
            List.of(event.getEventStartTime(), event.getEventEndTime(), event.getVenue().getId(), event.getStatus().toString()), 
            eventMapper);
    }

    public Uni<Void> updateEvent(Event event) {
        String query = "UPDATE event SET event_start_time = $1, event_end_time = $2, venue_id = $3, status = $4::event_status WHERE id = $5";
        return this.update(query, 
            List.of(event.getEventStartTime(), event.getEventEndTime(), event.getVenueId(), event.getStatus().toString(), event.getId()));
    }

    public Uni<Void> deleteEvent(int eventId) {
        String query = "DELETE FROM event WHERE id = $1";
        return this.delete(query, List.of(eventId));
    }
}
