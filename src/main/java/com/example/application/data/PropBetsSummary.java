package com.example.application.data;

import java.util.List;

public record PropBetsSummary(String betType,
                              String betValue,
                              List<String> betters,
                              String question,
                              Boolean isWinner) {
    @Override
    public String toString() {
        return getClass().getName() + "[betType=" + betType + ",betValue=" + betValue + ",betters=" + betters + ",question=" + question + ",isWinner=" + isWinner + "]";
    }
}
