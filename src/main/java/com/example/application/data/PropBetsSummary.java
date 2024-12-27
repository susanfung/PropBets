package com.example.application.data;

import java.util.List;

public class PropBetsSummary {
    private final String betType;
    private final String betValue;
    private final List<String> betters;

    public PropBetsSummary(String betType, String betValue, List<String> betters) {
        this.betType = betType;
        this.betValue = betValue;
        this.betters = betters;
    }

    public String toString() {
        return getClass().getName() + "[betType=" + betType + ", betValue=" + betValue + ", betters=" + String.join(", ", betters) + "]";
    }
}
