CREATE TABLE scoreboard_events_tracker
(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    total_amount_of_bets INTEGER,
    number_of_winning_events INTEGER,
    total_amount_won_per_event FLOAT,
    is_locked BOOLEAN
);
