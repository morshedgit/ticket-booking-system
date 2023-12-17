-- Active: 1700591344350@@127.0.0.1@5432@booking@public
-- Drop triggers for all tables
DROP TRIGGER IF EXISTS update_address_modtime ON address;
DROP TRIGGER IF EXISTS update_contact_modtime ON contact;
DROP TRIGGER IF EXISTS update_account_modtime ON account;
DROP TRIGGER IF EXISTS update_client_modtime ON client;
DROP TRIGGER IF EXISTS update_venue_modtime ON venue;
DROP TRIGGER IF EXISTS update_venue_section_modtime ON venue_section;
DROP TRIGGER IF EXISTS update_seat_modtime ON seat;
DROP TRIGGER IF EXISTS update_event_modtime ON event;
DROP TRIGGER IF EXISTS update_pricing_modtime ON pricing;
DROP TRIGGER IF EXISTS update_reservation_modtime ON reservation;
DROP TRIGGER IF EXISTS update_booking_modtime ON booking;

-- Drop functions
DROP FUNCTION IF EXISTS update_updated_at_column;
DROP FUNCTION IF EXISTS add_event;
DROP FUNCTION IF EXISTS book_seat;
DROP FUNCTION IF EXISTS get_venue_by_id;
DROP FUNCTION IF EXISTS get_event_by_id;
DROP FUNCTION IF EXISTS get_client_by_id;
DROP FUNCTION IF EXISTS reserve_seat;

-- Drop all tables
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS venue;
DROP TABLE IF EXISTS venue_section;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS pricing;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS booking;


DROP TYPE IF EXISTS event_status;
DROP TYPE IF EXISTS booking_status;
DROP TYPE IF EXISTS reservation_status;

CREATE TYPE reservation_status AS ENUM ('NOT_RESERVED', 'RESERVED', 'CANCELED');
CREATE TYPE booking_status AS ENUM ('NOT_BOOKED', 'BOOKED', 'CANCELED');
CREATE TYPE event_status AS ENUM ('ACTIVE', 'CANCELED');

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

-- Function to update the updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Address Table
CREATE TABLE IF NOT EXISTS address (
    id SERIAL PRIMARY KEY,
    line_1 VARCHAR(255) NOT NULL,
    line_2 VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    province VARCHAR(255) NOT NULL,
    country_code VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_address_modtime
BEFORE UPDATE ON address
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Contact Table
CREATE TABLE IF NOT EXISTS contact (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[],
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_contact_modtime
BEFORE UPDATE ON contact
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Account Table
CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    contact_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[],
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_account_modtime
BEFORE UPDATE ON account
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Client Table
CREATE TABLE IF NOT EXISTS client (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    manager_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[],
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_client_modtime
BEFORE UPDATE ON client
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Venue Table
CREATE TABLE IF NOT EXISTS venue (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_id BigInt NOT NULL,
    manager_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[],
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_venue_modtime
BEFORE UPDATE ON venue
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Venue Section Table
CREATE TABLE IF NOT EXISTS venue_section (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    venue_id BigInt,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_venue_section_modtime
BEFORE UPDATE ON venue_section
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Seat Table
CREATE TABLE IF NOT EXISTS seat (
    id SERIAL PRIMARY KEY,
    row INT NOT NULL,
    col INT NOT NULL,
    venue_section_id BigInt,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_seat_modtime
BEFORE UPDATE ON seat
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Event Table
CREATE TABLE IF NOT EXISTS event (
    id SERIAL PRIMARY KEY,
    event_start_time TIMESTAMP NOT NULL,
    event_end_time TIMESTAMP NOT NULL,
    venue_id BigInt NOT NULL,
    client_id BIGINT NOT NULL,
    status event_status DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_event_modtime
BEFORE UPDATE ON event
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Pricing Table
CREATE TABLE IF NOT EXISTS pricing (
    id SERIAL PRIMARY KEY,
    event_id BigInt NOT NULL,
    venue_id BigInt NOT NULL,
    venue_section_id BigInt,
    price DECIMAL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_pricing_modtime
BEFORE UPDATE ON pricing
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Reservation Table
CREATE TABLE IF NOT EXISTS reservation (
    id SERIAL PRIMARY KEY,
    event_id BigInt,
    seat_id BigInt,
    status reservation_status DEFAULT 'NOT_RESERVED',
    account_id BigInt,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_reservation_modtime
BEFORE UPDATE ON reservation
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Booking Table
CREATE TABLE IF NOT EXISTS booking (
    id SERIAL PRIMARY KEY,
    event_id BigInt,
    seat_id BigInt,
    status booking_status DEFAULT 'NOT_BOOKED',
    account_id BigInt,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER update_booking_modtime
BEFORE UPDATE ON booking
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION get_client_by_id(selected_client_id BIGINT)
RETURNS TABLE(
    id BIGINT,
    name VARCHAR,
    manager_ids BigInt[],
    managers JSON
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        cl.id::BIGINT,
        cl.name::VARCHAR,
        cl.manager_ids::BIGINT[],
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
    WHERE (selected_client_id IS NOT NULL AND cl.id = selected_client_id) OR (selected_client_id IS NULL)
    GROUP BY cl.id;
END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION add_event(new_event_start_time TIMESTAMP, new_event_end_time TIMESTAMP, selected_venue_id BIGINT, selected_client_id BIGINT)
RETURNS SETOF event AS $$
DECLARE
    result event;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM event et
        WHERE et.venue_id = selected_venue_id
        AND et.status = 'ACTIVE'
        AND (
            new_event_start_time BETWEEN et.event_start_time AND et.event_end_time
            OR
            new_event_end_time BETWEEN et.event_start_time AND et.event_end_time
        )
    ) THEN
        RAISE EXCEPTION 'Event duration overlaps with an existing event';
    END IF;

    INSERT INTO event (event_start_time, event_end_time, venue_id, client_id)
    VALUES (new_event_start_time, new_event_end_time, selected_venue_id, selected_client_id)
    RETURNING * INTO result;

    RETURN NEXT result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION reserve_seat(
    selected_event_id BigInt,
    selected_seat_id BigInt,
    selected_account_id BigInt
)
RETURNS SETOF reservation AS $$
DECLARE
    result reservation;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM booking bk
        WHERE (bk.event_id = selected_event_id AND bk.seat_id = selected_seat_id AND bk.status = 'BOOKED')
        UNION
        SELECT 1
        FROM reservation rs
        WHERE (rs.event_id = selected_event_id AND rs.seat_id = selected_seat_id AND rs.status = 'RESERVED')        
    ) THEN
        RAISE EXCEPTION 'Seat cannot be reserved';
    END IF;
    INSERT INTO reservation (
        event_id,
        seat_id,
        status,
        account_id
    )
    VALUES (            
        selected_event_id,
        selected_seat_id,
        'RESERVED',
        selected_account_id
    )
    RETURNING * INTO result;

    RETURN NEXT result;

    RETURN;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION book_seat(
    selected_event_id BigInt,
    selected_seat_id BigInt,
    selected_account_id BigInt
)
RETURNS SETOF booking AS $$
DECLARE
    result booking;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM booking bk
        WHERE (bk.event_id = selected_event_id AND bk.seat_id = selected_seat_id AND bk.status = 'BOOKED')
        UNION
        SELECT 1
        FROM reservation rs
        WHERE (rs.event_id = selected_event_id AND rs.seat_id = selected_seat_id AND rs.status = 'RESERVED')        
    ) THEN
        RAISE EXCEPTION 'Seat cannot be booked';
    END IF;

    INSERT INTO booking (
        event_id,
        seat_id,
        status,
        account_id
    )
    VALUES (            
        selected_event_id,
        selected_seat_id,
        'BOOKED',
        selected_account_id
    )
    RETURNING * INTO result;

    RETURN NEXT result;

    RETURN;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_venue_by_id(venue_id_arg BIGINT)
RETURNS TABLE(id BIGINT, name VARCHAR, address_id BIGINT, manager_ids BIGINT[], managers JSON, venue_sections JSON, address JSON) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        vn.id::BIGINT,
        vn.name::VARCHAR,
        vn.address_id::BIGINT,
        vn.manager_ids::BIGINT[],
        json_agg(row_to_json(mg.*)) AS managers,
        json_agg(row_to_json(sc.*)) AS venue_sections,
        (SELECT row_to_json(ad.*) FROM address ad WHERE ad.id = vn.address_id) AS address
    FROM "venue" vn
    LEFT JOIN (
        SELECT 
            ac.*,
            json_agg(row_to_json(ct.*)) AS contacts
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
        FROM venue_section vs
        LEFT JOIN seat st ON st.venue_section_id = vs.id
        GROUP BY vs.id
    ) sc ON sc.venue_id = vn.id
    WHERE vn.id = venue_id_arg
    GROUP BY vn.id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_event_by_id(event_id_arg BIGINT) 
RETURNS TABLE(
    id BIGINT,
    event_start_time TIMESTAMP,
    event_end_time TIMESTAMP,
    venue_id BIGINT,
    status event_status,
    venue json, 
    sections json,
    client_id BIGINT,
    client json
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        et.id::BIGINT,
        et.event_start_time::TIMESTAMP,
        et.event_end_time::TIMESTAMP,
        et.venue_id::BIGINT,
        et.status::event_status,
        (SELECT row_to_json(vn.*) 
        FROM get_venue_by_id(et.venue_id) vn
        LIMIT 1)::JSON AS venue,
        json_agg(row_to_json(sc.*))::JSON AS sections,
        et.client_id::BIGINT,
        (SELECT row_to_json(cl.*) 
        FROM get_client_by_id(et.client_id) cl
        LIMIT 1)::JSON AS client
    FROM event et
    LEFT JOIN (
        SELECT
            vs.*,
            et.id AS event_id,
            json_agg(
                json_build_object(
                    'seat', row_to_json(st.*),
                    'price', pr.price,
                    'reserve_status', rs.status,
                    'booking_status', bk.status
                )
            ) AS seats
        FROM venue_section vs
        LEFT JOIN seat st ON st.venue_section_id = vs.id
        LEFT JOIN reservation rs ON rs.seat_id = st.id
        LEFT JOIN booking bk ON bk.seat_id = st.id
        LEFT JOIN pricing pr ON pr.venue_section_id = vs.id
        LEFT JOIN event et ON et.id = pr.event_id
        GROUP BY et.id, vs.id
    ) sc ON sc.event_id = et.id
    WHERE ( event_id_arg IS NOT NULL AND et.id = event_id_arg) OR (event_id_arg IS NULL)
    GROUP BY et.id;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM get_event_by_id(1);


DELETE FROM address;
DELETE FROM contact;
DELETE FROM account;
DELETE FROM client;
DELETE FROM venue;
DELETE FROM venue_section;
DELETE FROM seat;
DELETE FROM event;
DELETE FROM pricing;
DELETE FROM reservation;
DELETE FROM booking;

-- Insert a test address
INSERT INTO address (line_1, line_2, city, province, country_code, postal_code)
VALUES ('123 Test Street', 'Suite 101', 'Test City', 'Test Province', 'TC', '12345');

-- Insert a test contact
INSERT INTO contact (email, phone_number, address_ids)
VALUES ('johndoe@example.com', '1234567890', ARRAY[1]);

-- Insert a test account
INSERT INTO account (first_name, last_name, full_name, contact_ids)
VALUES ('John', 'Doe', 'John Doe', ARRAY[1]);

-- Insert a test client
INSERT INTO client (name, manager_ids)
VALUES ('Test Client', ARRAY[1]);

-- Insert a test venue
INSERT INTO venue (name, address_id, manager_ids)
VALUES ('Test Venue', 1, ARRAY[1]);

-- Insert venue sections
INSERT INTO venue_section (name, venue_id)
VALUES ('Section A', 1), ('Section B', 1), ('Section C', 1), ('Section D', 1), ('Section E', 1);

-- Insert seats for each section
DO $$
DECLARE
    section_id INT;
BEGIN
    FOR section_id IN 1..5 LOOP
        FOR row IN 1..3 LOOP
            FOR col IN 1..5 LOOP
                INSERT INTO seat (row, col, venue_section_id)
                VALUES (row, col, section_id);
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

-- Insert an event
SELECT
    e.*
FROM add_event(
    '2023-12-05 15:00:00',
    '2023-12-05 17:00:00',
    1,
    1
) ec
JOIN LATERAL (SELECT * FROM get_event_by_id(ec.id) LIMIT 1) e ON e.id = ec.id;


SELECT 
    p.proname AS function_name,
    n.nspname AS schema_name,
    pg_catalog.pg_get_function_result(p.oid) AS return_type,
    pg_catalog.pg_get_function_arguments(p.oid) AS arguments,
    CASE
        WHEN p.prokind = 'a' THEN 'aggregate'
        WHEN p.prokind = 'w' THEN 'window'
        WHEN p.prokind = 'p' THEN 'procedure'
        ELSE 'function'
    END AS type
FROM 
    pg_catalog.pg_proc p
    LEFT JOIN pg_catalog.pg_namespace n ON n.oid = p.pronamespace
WHERE 
    pg_catalog.pg_function_is_visible(p.oid)
    AND n.nspname NOT IN ('pg_catalog', 'information_schema')
ORDER BY 
    function_name;



-- Insert pricings for all sections
DO $$
DECLARE
    section_id INT;
    calculated_price NUMERIC;
BEGIN
    FOR section_id IN 1..5 LOOP
        IF section_id > 4 THEN
            calculated_price := 50.00 * section_id / 4;
        ELSE
            calculated_price := 50.00;
        END IF;

        INSERT INTO pricing (event_id, venue_id, venue_section_id, price)
        VALUES (1, 1, section_id, calculated_price);
    END LOOP;
END $$;

-- Get accounts with contact_ids and address_ids
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
GROUP BY ac.id;


-- Get account by email
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
    WHERE ct.email = 'johndoe@example.com'
    GROUP BY ct.id
) ct ON ct.id = ANY(ac.contact_ids)
GROUP BY ac.id;

SELECT * FROM reservation;

