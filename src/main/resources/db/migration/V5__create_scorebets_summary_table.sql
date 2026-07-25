CREATE TABLE scorebets_summary
(
    bet_value VARCHAR(255) PRIMARY KEY,
    betters   VARCHAR(255),
    is_locked BOOLEAN,
    count     INTEGER
);
