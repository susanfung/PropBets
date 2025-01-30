package com.example.application.data;

import java.util.List;
import java.util.Optional;

public record ScoreBoardBetsSummary(String betValue,
                                    List<String> betters,
                                    Optional<Integer> count) {
    @Override
    public String toString() {
        return getClass().getName() + "[betValue=" + betValue + ",betters=" + betters + ",count=" + count + "]";
    }
}
