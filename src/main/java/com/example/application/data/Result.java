package com.example.application.data;

public record Result(String betType, String winningBetValue) {
    @Override
    public String toString() {
        return getClass().getName() + "[betType=" + betType + ",betValue=" + winningBetValue + "]";
    }
}
