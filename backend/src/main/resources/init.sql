-- Active: 1700591344350@@127.0.0.1@5432@booking@public

DROP FUNCTION IF EXISTS add_event( TIMESTAMP,  TIMESTAMP, INT);
DROP FUNCTION IF EXISTS reserve_seat( BIGINT,  BIGINT, BIGINT);
DROP FUNCTION IF EXISTS book_seat( BIGINT,  BIGINT, BIGINT);
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
DROP TYPE IF EXISTS reservation_status;
DROP TYPE IF EXISTS booking_status;
DROP TYPE IF EXISTS event_status;

CREATE TYPE reservation_status AS ENUM ('NOT_RESERVED', 'RESERVED', 'CANCELED');
CREATE TYPE booking_status AS ENUM ('NOT_BOOKED', 'BOOKED', 'CANCELED');
CREATE TYPE event_status AS ENUM ('ACTIVE', 'CANCELED');

CREATE TABLE IF NOT EXISTS address (
    id SERIAL PRIMARY KEY,
    line_1 VARCHAR(255) NOT NULL,
    line_2 VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    province VARCHAR(255) NOT NULL,
    country_code VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS contact (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[]
);

CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    contact_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[]
);

CREATE TABLE IF NOT EXISTS client (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    manager_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[]
);

CREATE TABLE IF NOT EXISTS venue (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_id BigInt NOT NULL,
    manager_ids BigInt[] NOT NULL DEFAULT ARRAY[]::BigInt[]
);

CREATE TABLE IF NOT EXISTS venue_section (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    venue_id BigInt
);

CREATE TABLE IF NOT EXISTS seat (
    id SERIAL PRIMARY KEY,
    row INT NOT NULL,
    col INT NOT NULL,
    venue_section_id BigInt
);
CREATE TABLE IF NOT EXISTS event (
    id SERIAL PRIMARY KEY,
    event_start_time TIMESTAMP NOT NULL,
    event_end_time TIMESTAMP NOT NULL,
    venue_id BigInt NOT NULL,
    status event_status
);

CREATE TABLE IF NOT EXISTS pricing (
    id SERIAL PRIMARY KEY,
    event_id BigInt NOT NULL,
    venue_id BigInt NOT NULL,
    venue_section_id BigInt,
    price DECIMAL
);


CREATE TABLE IF NOT EXISTS reservation (
    id SERIAL PRIMARY KEY,
    event_id BigInt,
    seat_id BigInt,
    status reservation_status DEFAULT 'NOT_RESERVED',
    account_id BigInt
);

CREATE TABLE IF NOT EXISTS booking (
    id SERIAL PRIMARY KEY,
    event_id BigInt,
    seat_id BigInt,
    status booking_status DEFAULT 'NOT_BOOKED',
    account_id BigInt
);

CREATE OR REPLACE FUNCTION add_event(new_event_start_time TIMESTAMP, new_event_end_time TIMESTAMP, selected_venue_id INT)
RETURNS SETOF event AS $$
DECLARE
    result event;
BEGIN
    IF NOT EXISTS (
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
        INSERT INTO event (event_start_time, event_end_time, venue_id)
        VALUES (new_event_start_time, new_event_end_time, selected_venue_id)
        RETURNING * INTO result;

        RETURN NEXT result;
    END IF;

    RETURN;
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
    IF NOT EXISTS (
        SELECT 1
        FROM booking bk
        WHERE (bk.event_id = selected_event_id AND bk.seat_id = selected_seat_id AND bk.status = 'BOOKED')
        UNION
        SELECT 1
        FROM reservation rs
        WHERE (rs.event_id = selected_event_id AND rs.seat_id = selected_seat_id AND rs.status = 'RESERVED')        
    ) THEN
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
    END IF;

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
    IF NOT EXISTS (
        SELECT 1
        FROM booking bk
        WHERE (bk.event_id = selected_event_id AND bk.seat_id = selected_seat_id AND bk.status = 'BOOKED')
        UNION
        SELECT 1
        FROM reservation rs
        WHERE (rs.event_id = selected_event_id AND rs.seat_id = selected_seat_id AND rs.status = 'RESERVED')        
    ) THEN
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
    END IF;

    RETURN;
END;
$$ LANGUAGE plpgsql;

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

-- Insert an event for this Saturday 4pm-6pm
INSERT INTO event (event_start_time, event_end_time, venue_id)
VALUES (date_trunc('week', CURRENT_DATE) + INTERVAL '6 days 16:00', date_trunc('week', CURRENT_DATE) + INTERVAL '6 days 18:00', 1);

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


-- Get clients with their contact info
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
GROUP BY cl.id;


-- Get venues with their contact info
SELECT 
    vn.*,
    json_agg( row_to_json(mg.*)        
    ) AS managers,
    json_agg(row_to_json(sc.*)
    ) AS sections
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
GROUP BY vn.id;

-- Get event with venue and pricing info
SELECT
    et.*,
    vn.*,
    json_agg(row_to_json(sc.*)) AS sections
FROM event et
LEFT JOIN venue vn ON vn.id = et.venue_id
LEFT JOIN (
    SELECT
        vs.*,
        et.id AS event_id,
        json_agg(
            json_build_object(
                'seat', row_to_json(st.*),
                'price', pr.price,
                'reserve_status',rs.status,
                'booking_status',bk.status
            )
        ) AS seats
    FROM venue_section vs
    LEFT JOIN seat st ON st.venue_section_id = vs.id
    LEFT JOIN reservation rs ON rs.seat_id = st.id
    LEFT JOIN booking bk ON bk.seat_id = st.id
    LEFT JOIN pricing pr ON pr.venue_section_id = vs.id
    LEFT JOIN event et ON et.id = pr.event_id
    GROUP BY et.id,vs.id
) sc ON sc.event_id = et.id
GROUP BY et.id, vn.id;

-- ADD a new event
SELECT add_event('2023-12-01 16:00:00', '2023-12-01 18:00:00', 1);

-- Cancel an event
UPDATE event
SET status = 'CANCELED'
WHERE id = 1;

SELECT * FROM reserve_seat(1,10,1);
SELECT * FROM book_seat(1,10,1);

SELECT * FROM reservation;
SELECT * FROM booking;

DELETE FROM booking;



-- Cancel a reservation
UPDATE reservation
SET status = 'CANCELED'
WHERE id = 2;

