CREATE TABLE user_bets
(
    username VARCHAR(255) PRIMARY KEY,
    bet_type VARCHAR(255),
    bet_value VARCHAR(255),
    is_locked BOOLEAN
);
