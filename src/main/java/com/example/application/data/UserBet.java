package com.example.application.data;

public class UserBet {
    private final String username;
    private final String betType;
    private final String betValue;

    public UserBet(String username, String betType, String betValue) {
        this.username = username;
        this.betType = betType;
        this.betValue = betValue;
    }

    public String getUsername() {
        return username;
    }

    public String getBetType() {
        return betType;
    }

    public String getBetValue() {
        return betValue;
    }
}
