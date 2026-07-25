CREATE TABLE propbets (
    bet_type   VARCHAR(255) PRIMARY KEY,
    question   VARCHAR(255),
    choices    VARCHAR(255),
    is_locked  BOOLEAN
);
