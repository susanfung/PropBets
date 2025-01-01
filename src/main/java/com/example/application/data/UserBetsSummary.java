package com.example.application.data;

public record UserBetsSummary(String username,
                              Integer numberOfBetsMade,
                              Double amountOwing,
                              Integer numberOfBetsWon,
                              Double amountWon,
                              Double netAmount) {
    @Override
    public String toString() {
        return getClass().getName() + "[username=" + username + ",numberOfBetsMade=" + numberOfBetsMade + ",amountOwing=" + amountOwing + ",numberOfBetsWon=" + numberOfBetsWon + ",amountWon=" + amountWon + ",netAmount=" + netAmount + "]";
    }
}
