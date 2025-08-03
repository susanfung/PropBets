package com.example.application.data;

import java.util.Set;

public record PropBetsSummary(String betType,
                              String betValue,
                              Set<String> betters,
                              String question,
                              Boolean isWinner) {
    @Override
    public String toString() {
        return getClass().getName() + "[betType=" + betType + ",betValue=" + betValue + ",betters=" + betters + ",question=" + question + ",isWinner=" + isWinner + "]";
    }
}
