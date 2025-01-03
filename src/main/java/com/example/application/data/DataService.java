package com.example.application.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

@Service
public class DataService {
    private static final String USERNAME = "username";
    private static final String BET_TYPE = "betType";
    private static final String BET_VALUE = "betValue";
    private static final String BETTERS = "betters";
    private static final String NUMBER_OF_BETS_MADE = "numberOfBetsMade";
    private static final String AMOUNT_OWING = "amountOwing";
    private static final String NUMBER_OF_BETS_WON = "numberOfBetsWon";
    private static final String AMOUNT_WON = "amountWon";
    private static final String NET_AMOUNT = "netAmount";
    private static final String NAME = "name";
    private static final String QUESTION = "question";
    private static final String CHOICES = "choices";
    private static final String WINNING_BET_VALUE = "winningBetValue";
    private static final String IS_WINNER = "isWinner";

    private final MongoCollection<Document> userBetsSummaryCollection;
    private final MongoCollection<Document> propBetsSummaryCollection;
    private final MongoCollection<Document> userBetsCollection;
    private final MongoCollection<Document> propBetsCollection;
    private final MongoCollection<Document> resultsCollection;

    @Autowired
    public DataService(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
        userBetsCollection = database.getCollection("UserBets");
        propBetsSummaryCollection = database.getCollection("PropBetsSummary");
        userBetsSummaryCollection = database.getCollection("UserBetsSummary");
        propBetsCollection = database.getCollection("PropBets");
        resultsCollection = database.getCollection("Results");
    }

    public List<UserBetsSummary> getUserBetsSummary() {
        MongoCursor<Document> cursor = userBetsSummaryCollection.find().iterator();

        List<UserBetsSummary> userBetsSummaries = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                UserBetsSummary userBetsSummary = new UserBetsSummary(document.getString(USERNAME),
                                                                      document.getInteger(NUMBER_OF_BETS_MADE),
                                                                      document.getDouble(AMOUNT_OWING),
                                                                      document.getInteger(NUMBER_OF_BETS_WON),
                                                                      document.getDouble(AMOUNT_WON),
                                                                      document.getDouble(NET_AMOUNT));

                userBetsSummaries.add(userBetsSummary);
            }
        } finally {
            cursor.close();
        }

        return userBetsSummaries;
    }

    public Map<String, List<String>> getPropBetsSummary() {
        MongoCursor<Document> cursor = propBetsSummaryCollection.find().iterator();

        Map<String, List<String>> propBetsSummaries = new HashMap<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                String betType = document.getString(BET_TYPE);

                if (!betType.equals("Score")) {
                    StringBuilder betSummary = new StringBuilder();
                    betSummary.append(document.getString(BET_VALUE))
                              .append(" - ")
                              .append(String.join(", ", document.getList(BETTERS, String.class))).append("\n");

                    propBetsSummaries.computeIfAbsent(betType + " - " + document.getString(QUESTION), k -> new ArrayList<>())
                                     .add(betSummary.toString());
                }
            }
        } finally {
            cursor.close();
        }

        return propBetsSummaries;
    }

    public Map<String, String> getScoreBoardBetsSummary() {
        MongoCursor<Document> cursor = propBetsSummaryCollection.find().iterator();

        Map<String, String> scoreBoardBetsSummaries = new HashMap<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                String betType = document.getString(BET_TYPE);

                if (betType.equals("Score")) {
                    String betValue = document.getString(BET_VALUE);
                    String betters = String.join("\n", document.getList(BETTERS, String.class));

                    scoreBoardBetsSummaries.put(betValue, betters);
                }
            }
        } finally {
            cursor.close();
        }

        return scoreBoardBetsSummaries;
    }

    public List<UserBet> getUserBets() {
        MongoCursor<Document> cursor = userBetsCollection.find().iterator();

        List<UserBet> userBets = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                UserBet userBet = new UserBet(document.getString(USERNAME),
                                              document.getString(BET_TYPE),
                                              document.getString(BET_VALUE));

                userBets.add(userBet);
            }
        } finally {
            cursor.close();
        }

        return userBets;
    }

    public List<PropBet> getPropBets() {
        MongoCursor<Document> cursor = propBetsCollection.find().iterator();

        List<PropBet> propBets = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                PropBet propBet = new PropBet(document.getString(NAME),
                                              document.getString(QUESTION),
                                              document.getList(CHOICES, String.class));

                propBets.add(propBet);
            }
        } finally {
            cursor.close();
        }

        return propBets;
    }

    public List<UserBet> findUserBetsByUsername(String username) {
        MongoCursor<Document> cursor = userBetsCollection.find(eq("username", username)).iterator();

        List<UserBet> userBets = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                UserBet userBet = new UserBet(document.getString(USERNAME),
                                              document.getString(BET_TYPE),
                                              document.getString(BET_VALUE));

                userBets.add(userBet);
            }
        } finally {
            cursor.close();
        }

        return userBets;
    }

    public boolean isPropBetNameTaken(String name) {
        return propBetsCollection.find(eq(NAME, toCamelCase(name))).first() != null;
    }

    public void deletePreviousBets(String username) {
        userBetsCollection.deleteMany(eq(USERNAME, username));

        propBetsSummaryCollection.updateMany(eq(BETTERS, username), Updates.pull(BETTERS, username));

        propBetsSummaryCollection.deleteMany(eq(BETTERS, Collections.emptyList()));
    }

    public void saveScoreBoardBets(String username, Map<String, String> bets) {
        userBetsCollection.deleteMany(and(eq(USERNAME, username), eq(BET_TYPE, "Score")));

        bets.forEach((betValue, betType) -> {
            UserBet bet = new UserBet(username, betType, betValue);
            addUserBet(bet);
            updatePropBetsSummary(betType, betValue, username);
        });
    }

    public void savePropBets(String username, Map<String, String> bets) {
        userBetsCollection.deleteMany(and(eq(USERNAME, username), ne(BET_TYPE, "Score")));

        bets.forEach((betType, betValue) -> {
            UserBet bet = new UserBet(username, betType, betValue);
            addUserBet(bet);
            updatePropBetsSummary(betType, betValue, username);
        });
    }

    public void addUserBet(UserBet userBet) {
        Document document = new Document().append(USERNAME, userBet.username())
                                          .append(BET_TYPE, userBet.betType())
                                          .append(BET_VALUE, userBet.betValue());

        userBetsCollection.insertOne(document);
    }

    public void updatePropBetsSummary(String betType, String betValue, String username) {
        Document foundPropBetsSummary = propBetsSummaryCollection.find(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue))).first();
        Document foundPropBets = propBetsCollection.find(eq(NAME, betType)).first();

        if (foundPropBetsSummary != null) {
            List<String> betters = new ArrayList<>(foundPropBetsSummary.getList(BETTERS, String.class));
            betters.add(username);
            Collections.sort(betters);

            propBetsSummaryCollection.updateOne(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue)), Updates.set(BETTERS, betters));
        } else {
            Document newPropBetsSummary = new Document(BET_TYPE, betType).append(BET_VALUE, betValue)
                                                                         .append(BETTERS, List.of(username));

            if (foundPropBets != null) {
                newPropBetsSummary.append(QUESTION, foundPropBets.getString(QUESTION));
            }

            propBetsSummaryCollection.insertOne(newPropBetsSummary);
        }
    }

    public void updateUserBetsSummary(String username, Integer numberOfBetsMade, Integer totalBetAmount) {
        Document foundUser = userBetsSummaryCollection.find(eq(USERNAME, username)).first();

        if (foundUser != null) {
            Double amountWon = foundUser.getDouble(AMOUNT_WON);

            userBetsSummaryCollection.updateOne(eq(USERNAME, username),
                                                Updates.combine(Updates.set(NUMBER_OF_BETS_MADE, numberOfBetsMade),
                                                                Updates.set(AMOUNT_OWING, totalBetAmount.doubleValue()),
                                                                Updates.set(NET_AMOUNT, amountWon - totalBetAmount.doubleValue())));
        } else {
            Document newUser = new Document(USERNAME, username).append(NUMBER_OF_BETS_MADE, numberOfBetsMade)
                                                               .append(AMOUNT_OWING, totalBetAmount.doubleValue())
                                                               .append(NUMBER_OF_BETS_WON, 0)
                                                               .append(AMOUNT_WON, 0.0)
                                                               .append(NET_AMOUNT, -totalBetAmount.doubleValue());

            userBetsSummaryCollection.insertOne(newUser);
        }
    }

    public void createNewPropBet(String name, String question, String choices) {
        List<String> choicesList = Stream.of(choices.split(","))
                                         .map(String::trim)
                                         .map(this::toCamelCase)
                                         .toList();

        Document document = new Document().append(NAME, toCamelCase(name))
                                          .append(QUESTION, formatQuestion(question))
                                          .append(CHOICES, choicesList);

        propBetsCollection.insertOne(document);
    }

    private String toCamelCase(String input) {
        return Stream.of(input.split("\\s+"))
                     .filter(s -> !s.isEmpty())
                     .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                     .collect(Collectors.joining(" "))
                     .trim();
    }

    private String formatQuestion(String input) {
        String string = input.substring(0, 1).toUpperCase() + input.substring(1);
        return string.endsWith("?") ? string : string + "?";
    }

    public void saveResult(String betType, String winningBetValue) {
        Document document = new Document().append(BET_TYPE, betType)
                                          .append(WINNING_BET_VALUE, winningBetValue);

        resultsCollection.insertOne(document);

        Document foundPropBet = propBetsCollection.find(eq(NAME, betType)).first();
        List<String> winningBetters = new ArrayList<>();
        List<String> losingBetters = new ArrayList<>();

        if (foundPropBet != null) {
            List<String> betValues = foundPropBet.getList(CHOICES, String.class);

            betValues.forEach(betValue -> {
                Document foundPropBetsSummary = propBetsSummaryCollection.find(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue))).first();

                if (foundPropBetsSummary != null) {
                    List<String> betters = new ArrayList<>(foundPropBetsSummary.getList(BETTERS, String.class));

                    if (betValue.equals(winningBetValue)) {
                        winningBetters.addAll(betters);
                        propBetsSummaryCollection.updateOne(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue)),
                                                            Updates.set(IS_WINNER, true));
                    } else {
                        losingBetters.addAll(betters);
                        propBetsSummaryCollection.updateOne(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue)),
                                                            Updates.set(IS_WINNER, false));
                    }
                }
            });
        }

        Double amountWonPerBetter = Double.valueOf((winningBetters.size() + losingBetters.size()) * 2 / winningBetters.size());

        winningBetters.forEach(username -> {
            Document foundUserBetSummary = userBetsSummaryCollection.find(eq(USERNAME, username)).first();

            if (foundUserBetSummary != null) {
                Double amountOwing = foundUserBetSummary.getDouble(AMOUNT_OWING);
                Double updatedAmountWon = foundUserBetSummary.getDouble(AMOUNT_WON) + amountWonPerBetter;

                userBetsSummaryCollection.updateOne(eq(USERNAME, username),
                                                    Updates.combine(Updates.set(NUMBER_OF_BETS_WON,
                                                                                foundUserBetSummary.getInteger(NUMBER_OF_BETS_WON) + 1),
                                                                    Updates.set(AMOUNT_WON, updatedAmountWon),
                                                                    Updates.set(NET_AMOUNT, updatedAmountWon - amountOwing)));
            }
        });
    }
}
