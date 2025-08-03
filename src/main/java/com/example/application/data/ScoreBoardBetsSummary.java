package com.example.application.data;

import java.util.Set;
import java.util.Optional;

public record ScoreBoardBetsSummary(String betValue,
                                    Set<String> betters,
                                    Optional<Integer> count) {
    @Override
    public String toString() {
        return getClass().getName() + "[betValue=" + betValue + ",betters=" + betters + ",count=" + count + "]";
    }
}
