package com.example.application.data;

public record UserBet(String username, String betType, String betValue) {
    @Override
    public String toString() {
        return getClass().getName() + "[username=" + username + ",betType=" + betType + ",betValue=" + betValue + "]";
    }
}
