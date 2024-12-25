package com.example.application.data;

public class UserBetsSummary {
    private final String username;
    private final Integer numberOfBetsMade;
    private final Double amountOwing;
    private final Integer numberOfBetsWon;
    private final Double amountWon;
    private final Double netAmount;

    public UserBetsSummary(String username,
                           Integer numberOfBetsMade,
                           Double amountOwing,
                           Integer numberOfBetsWon,
                           Double amountWon,
                           Double netAmount) {
        this.username = username;
        this.numberOfBetsMade = numberOfBetsMade;
        this.amountOwing = amountOwing;
        this.numberOfBetsWon = numberOfBetsWon;
        this.amountWon = amountWon;
        this.netAmount = netAmount;
    }

    public String getUsername() {
        return username;
    }

    public Integer getNumberOfBetsMade() {
        return numberOfBetsMade;
    }

    public Double getAmountOwing() {
        return amountOwing;
    }

    public Integer getNumberOfBetsWon() {
        return numberOfBetsWon;
    }

    public Double getAmountWon() {
        return amountWon;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public String toString() {
        return getClass().getName() + "[username=" + username + ",numberOfBetsMade=" + numberOfBetsMade + ",amountOwing=" + amountOwing + ",numberOfBetsWon=" + numberOfBetsWon + ",amountWon=" + amountWon + ",netAmount=" + netAmount + "]";
    }
}
