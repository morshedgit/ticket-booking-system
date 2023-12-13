CREATE TABLE IF NOT EXISTS seat (
    id SERIAL PRIMARY KEY,
    row INT,
    col INT,
    booked Boolean,
    reserved Boolean,
    client_id BigInt
);

DELETE FROM seat;

INSERT INTO seat (row,col,booked,reserved,client_id)
VALUES
    (1,1,FALSE,FALSE,NULL),
    (1,2,FALSE,FALSE,NULL),
    (1,3,FALSE,FALSE,NULL),
    (1,4,FALSE,FALSE,NULL),
    (1,5,FALSE,FALSE,NULL),
    (1,6,FALSE,FALSE,NULL),
    (1,7,FALSE,FALSE,NULL),
    (1,8,FALSE,FALSE,NULL),
    (1,9,FALSE,FALSE,NULL),
    (1,10,FALSE,FALSE,NULL);