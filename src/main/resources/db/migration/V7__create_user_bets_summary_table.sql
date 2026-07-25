CREATE TABLE user_bets_summary
(
    username VARCHAR(255) PRIMARY KEY,
    number_of_bets_made INTEGER,
    amount_owing FLOAT,
    number_of_bets_won INTEGER,
    amount_won FLOAT,
    net_amount FLOAT,
    amount_of_scoreboard_bets_won FLOAT,
    number_of_scoreboard_bets_won INTEGER,
    amount_of_propbets_won FLOAT,
    number_of_propbets_won INTEGER
);
