CREATE TABLE propbets_summary (
    bet_type   VARCHAR(255),
    bet_value  VARCHAR(255),
    betters    VARCHAR(255),
    is_locked  BOOLEAN,
    count      INTEGER,
    question   VARCHAR(255),
    is_winner  BOOLEAN,
    PRIMARY KEY (bet_type, bet_value)
);
