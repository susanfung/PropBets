package com.example.application.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.application.utils.Utils.AMOUNT_PER_BET;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

@Service
public class DataService {
    private static final String USERNAME = "username";
    private static final String BET_TYPE = "betType";
    private static final String BET_VALUE = "betValue";
    private static final String BETTERS = "betters";
    private static final String NUMBER_OF_BETS_MADE = "numberOfBetsMade";
    private static final String AMOUNT_OWING = "amountOwing";
    private static final String NUMBER_OF_SCOREBOARD_BETS_WON = "numberOfScoreBoardBetsWon";
    private static final String AMOUNT_OF_SCOREBOARD_BETS_WON = "amountOfScoreBoardBetsWon";
    private static final String NUMBER_OF_PROPBETS_WON = "numberOfPropBetsWon";
    private static final String AMOUNT_OF_PROPBETS_WON = "amountOfPropBetsWon";
    private static final String NUMBER_OF_BETS_WON = "numberOfBetsWon";
    private static final String AMOUNT_WON = "amountWon";
    private static final String NET_AMOUNT = "netAmount";
    private static final String NAME = "name";
    private static final String QUESTION = "question";
    private static final String CHOICES = "choices";
    private static final String WINNING_BET_VALUE = "winningBetValue";
    private static final String IS_WINNER = "isWinner";
    private static final String SCORE_BET_TYPE = "Score";
    private static final String COUNT = "count";
    private static final String IS_SCOREBOARD_EVENTS_TRACKER = "isScoreBoardEventsTracker";
    private static final String TOTAL_AMOUNT_OF_BETS = "totalAmountOfBets";
    private static final String NUMBER_OF_WINNING_EVENTS = "numberOfWinningEvents";
    private static final String TOTAL_AMOUNT_WON_PER_EVENT = "totalAmountWonPerEvent";
    private static final String IS_LOCKED = "isLocked";

    private final MongoCollection<Document> userBetsSummaryCollection;
    private final MongoCollection<Document> propBetsSummaryCollection;
    private final MongoCollection<Document> userBetsCollection;
    private final MongoCollection<Document> propBetsCollection;
    private final MongoCollection<Document> resultsCollection;
    private final MongoCollection<Document> scoreCollection;

    @Autowired
    public DataService(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
        userBetsCollection = database.getCollection("UserBets");
        propBetsSummaryCollection = database.getCollection("PropBetsSummary");
        userBetsSummaryCollection = database.getCollection("UserBetsSummary");
        propBetsCollection = database.getCollection("PropBets");
        resultsCollection = database.getCollection("Results");
        scoreCollection = database.getCollection("Score");
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

    public List<PropBetsSummary> getPropBetsSummary() {
        MongoCursor<Document> cursor = propBetsSummaryCollection.find().iterator();

        List<PropBetsSummary> propBetsSummaries = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                String betType = document.getString(BET_TYPE);

                if (!betType.equals("Score")) {
                    PropBetsSummary summary = new PropBetsSummary(betType,
                                                                  document.getString(BET_VALUE),
                                                                  document.getList(BETTERS, String.class),
                                                                  document.getString(QUESTION),
                                                                  document.getBoolean(IS_WINNER));
                    propBetsSummaries.add(summary);
                }
            }
        } finally {
            cursor.close();
        }

        return propBetsSummaries;
    }

    public List<ScoreBoardBetsSummary> getScoreBoardBetsSummary() {
        MongoCursor<Document> cursor = propBetsSummaryCollection.find().iterator();

        List<ScoreBoardBetsSummary> scoreBoardBetsSummaries = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                String betType = document.getString(BET_TYPE);

                if (betType.equals("Score")) {
                    String betValue = document.getString(BET_VALUE);
                    List<String> betters = document.getList(BETTERS, String.class);
                    Optional<Integer> count = Optional.ofNullable(document.getInteger(COUNT));

                    ScoreBoardBetsSummary summary = new ScoreBoardBetsSummary(betValue, betters, count);
                    scoreBoardBetsSummaries.add(summary);
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
                                              document.getList(CHOICES, String.class),
                                              Optional.ofNullable(document.getBoolean(IS_LOCKED)));

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

    public List<Result> findResults() {
        MongoCursor<Document> cursor = resultsCollection.find().iterator();

        List<Result> results = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                Result result = new Result(document.getString(BET_TYPE),
                                           document.getString(WINNING_BET_VALUE));

                results.add(result);
            }
        } finally {
            cursor.close();
        }

        return results;
    }

    public boolean isPropBetNameTaken(String name) {
        return propBetsCollection.find(eq(NAME, toCamelCase(name))).first() != null;
    }

    public void deletePreviousBets(String username) {
        Bson isLockedFilter = Filters.not(exists(IS_LOCKED));

        Bson filter = Filters.and(eq(USERNAME, username), isLockedFilter);
        userBetsCollection.deleteMany(filter);

        Bson updateFilter = Filters.and(eq(BETTERS, username), isLockedFilter);
        propBetsSummaryCollection.updateMany(updateFilter, Updates.pull(BETTERS, username));

        Bson deleteEmptyFilter = Filters.and(eq(BETTERS, Collections.emptyList()), isLockedFilter);
        propBetsSummaryCollection.deleteMany(deleteEmptyFilter);
    }

    public void saveScoreBoardBets(String username, Map<String, String> bets) {
        bets.forEach((betValue, betType) -> {
            UserBet bet = new UserBet(username, betType, betValue);
            addUserBet(bet);
            updatePropBetsSummary(betType, betValue, username);
        });
    }

    public void savePropBets(String username, Map<String, String> bets) {
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

    public void updateUserBetsSummary(String username, Integer amountPerBet) {
        Document foundUser = userBetsSummaryCollection.find(eq(USERNAME, username)).first();
        List<UserBet> userBetsList = findUserBetsByUsername(username);
        Integer numberOfBetsMade = userBetsList.size();
        Double amountOwing = Double.valueOf(numberOfBetsMade * amountPerBet);

        if (foundUser != null) {
            Double amountWon = foundUser.getDouble(AMOUNT_WON);

            userBetsSummaryCollection.updateOne(eq(USERNAME, username),
                                                Updates.combine(Updates.set(NUMBER_OF_BETS_MADE, numberOfBetsMade),
                                                                Updates.set(AMOUNT_OWING, amountOwing),
                                                                Updates.set(NET_AMOUNT, amountWon - amountOwing)));
        } else {
            Document newUser = new Document(USERNAME, username).append(NUMBER_OF_BETS_MADE, numberOfBetsMade)
                                                               .append(AMOUNT_OWING, amountOwing)
                                                               .append(NUMBER_OF_BETS_WON, 0)
                                                               .append(AMOUNT_WON, 0.0)
                                                               .append(NET_AMOUNT, -amountOwing);

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
        saveResultsToCollection(resultsCollection, BET_TYPE, betType, WINNING_BET_VALUE, winningBetValue);

        Document foundPropBet = propBetsCollection.find(eq(NAME, betType)).first();
        List<String> winningBetters = new ArrayList<>();
        List<String> losingBetters = new ArrayList<>();

        if (foundPropBet != null) {
            updatePropBetsSummaryWithResults(betType, winningBetValue, foundPropBet, winningBetters, losingBetters);
        }

        BigDecimal totalBetters = BigDecimal.valueOf(winningBetters.size() + losingBetters.size());
        Double amountWonPerBetter = totalBetters.multiply(BigDecimal.valueOf(AMOUNT_PER_BET))
                                                .divide(BigDecimal.valueOf(winningBetters.size()), 2, RoundingMode.HALF_UP)
                                                .doubleValue();

        updateUserBetsSummaryForAllWinningBetters(winningBetters, amountWonPerBetter);
    }

    private void updatePropBetsSummaryWithResults(String betType,
                                                  String winningBetValue,
                                                  Document foundPropBet,
                                                  List<String> winningBetters,
                                                  List<String> losingBetters) {
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

    private void updateUserBetsSummaryForAllWinningBetters(List<String> winningBetters, Double amountWonPerBetter) {
        winningBetters.forEach(username -> {
            Document foundUserBetSummary = userBetsSummaryCollection.find(eq(USERNAME, username)).first();

            if (foundUserBetSummary != null) {
                Double amountOwing = foundUserBetSummary.getDouble(AMOUNT_OWING);
                Optional<Integer> numberOfScoreBoardBetsWon = Optional.ofNullable(foundUserBetSummary.getInteger(NUMBER_OF_SCOREBOARD_BETS_WON));
                Optional<Double> amountOfScoreBoardBetsWon = Optional.ofNullable(foundUserBetSummary.getDouble(AMOUNT_OF_SCOREBOARD_BETS_WON));
                Integer numberOfPropBetsWon = Optional.ofNullable(foundUserBetSummary.getInteger(NUMBER_OF_PROPBETS_WON)).orElse(0) + 1;
                Double amountOfPropBetsWon = BigDecimal.valueOf(Optional.ofNullable(foundUserBetSummary.getDouble(AMOUNT_OF_PROPBETS_WON))
                                                                        .orElse(0.0) + amountWonPerBetter)
                                                       .setScale(2, RoundingMode.HALF_UP)
                                                       .doubleValue();
                Integer numberOfBetsWon = numberOfScoreBoardBetsWon.orElse(0) + numberOfPropBetsWon;
                Double amountWon = BigDecimal.valueOf(amountOfScoreBoardBetsWon.orElse(0.0) + amountOfPropBetsWon)
                                             .setScale(2, RoundingMode.HALF_UP)
                                             .doubleValue();
                Double netAmount = BigDecimal.valueOf(amountWon - amountOwing).setScale(2, RoundingMode.HALF_UP).doubleValue();

                userBetsSummaryCollection.updateOne(eq(USERNAME, username),
                                                    Updates.combine(Updates.set(NUMBER_OF_PROPBETS_WON, numberOfPropBetsWon),
                                                                    Updates.set(AMOUNT_OF_PROPBETS_WON, amountOfPropBetsWon),
                                                                    Updates.set(NUMBER_OF_BETS_WON, numberOfBetsWon),
                                                                    Updates.set(AMOUNT_WON, amountWon),
                                                                    Updates.set(NET_AMOUNT, netAmount)));
            }
        });
    }

    public void saveScore(String team1Name, String team1Score, String team2Name, String team2Score) {
        saveResultsToCollection(scoreCollection, team1Name, team1Score, team2Name, team2Score);

        String betValue = team1Score.substring(team1Score.length() - 1) + "," + team2Score.substring(team2Score.length() - 1);

        Document foundPropBetsSummary = propBetsSummaryCollection.find(and(eq(BET_TYPE, SCORE_BET_TYPE), eq(BET_VALUE, betValue))).first();

        if (foundPropBetsSummary != null) {
            Document scoreBoardEventsTracker = getIsScoreBoardEventsTracker();
            final Double[] totalAmountWonPerEvent = {0.0};

            if (scoreBoardEventsTracker != null) {
                updateScoreBoardEventsTracker(scoreBoardEventsTracker, totalAmountWonPerEvent);
            }

            updateScoreInPropBetsSummmary(foundPropBetsSummary);

            Map<String, Integer> winningBettersCountMap = new HashMap<>();
            Map<String, Double> winningBettersTotalMap = new HashMap<>();

            findAllWinningScoreEvents(winningBettersCountMap, winningBettersTotalMap, totalAmountWonPerEvent);

            updateScoreBoardBetsInUserBetsSummary(winningBettersCountMap, winningBettersTotalMap);
        }
    }

    public void saveResultsToCollection(MongoCollection<Document> collection, String team1Name, String team1Score, String team2Name, String team2Score) {
        Document scoreDocument = new Document().append(team1Name, team1Score)
                                               .append(team2Name, team2Score);

        collection.insertOne(scoreDocument);
    }

    public Document getIsScoreBoardEventsTracker() {
        return scoreCollection.find(eq("isScoreBoardEventsTracker", true)).first();
    }

    public void updateScoreBoardEventsTracker(Document scoreBoardEventsTracker, Double[] totalAmountWonPerEvent) {
        Double totalAmountOfBets = scoreBoardEventsTracker.getDouble("totalAmountOfBets");
        Integer numberOfWinningEvents = scoreBoardEventsTracker.getInteger("numberOfWinningEvents");

        numberOfWinningEvents += 1;

        totalAmountWonPerEvent[0] = BigDecimal.valueOf(totalAmountOfBets / numberOfWinningEvents)
                                              .setScale(2, RoundingMode.HALF_UP)
                                              .doubleValue();

        scoreBoardEventsTracker.put("numberOfWinningEvents", numberOfWinningEvents);
        scoreBoardEventsTracker.put("totalAmountWonPerEvent", totalAmountWonPerEvent[0]);

        scoreCollection.replaceOne(eq("isScoreBoardEventsTracker", true), scoreBoardEventsTracker);
    }

    public void updateScoreInPropBetsSummmary(Document foundPropBetsSummary) {
        String betType = foundPropBetsSummary.getString(BET_TYPE);
        String betValue = foundPropBetsSummary.getString(BET_VALUE);
        Optional<Integer> count = Optional.ofNullable(foundPropBetsSummary.getInteger(COUNT));

        if (count.isPresent()) {
            propBetsSummaryCollection.updateOne(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue)),
                                                Updates.set(COUNT, count.get() + 1));
        } else {
            propBetsSummaryCollection.updateOne(and(eq(BET_TYPE, betType), eq(BET_VALUE, betValue)),
                                                Updates.set(COUNT, 1));
        }
    }

    public void findAllWinningScoreEvents(Map<String, Integer> winningBettersCountMap,
                                          Map<String, Double> winningBettersTotalMap,
                                          Double[] totalAmountWonPerEvent) {
        MongoCursor<Document> cursor = propBetsSummaryCollection.find(and(eq(BET_TYPE, SCORE_BET_TYPE), exists(COUNT))).iterator();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                List<String> winningBetters = document.getList(BETTERS, String.class);

                Double amountWonPerBetter = BigDecimal.valueOf((document.getInteger(COUNT) * totalAmountWonPerEvent[0]) / winningBetters.size())
                                                      .setScale(2, RoundingMode.HALF_UP)
                                                      .doubleValue();

                winningBetters.forEach(username -> {
                    winningBettersCountMap.put(username, winningBettersCountMap.getOrDefault(username, 0) + document.getInteger(COUNT));
                    winningBettersTotalMap.put(username, winningBettersTotalMap.getOrDefault(username, 0.0) + amountWonPerBetter);
                });
            }
        } finally {
            cursor.close();
        }
    }

    public void updateScoreBoardBetsInUserBetsSummary(Map<String, Integer> winningBettersCountMap,
                                                       Map<String, Double> winningBettersTotalMap) {
        winningBettersCountMap.forEach((username, numberOfScoreBoardBetsWon) -> {
            Document foundUserBetSummary = userBetsSummaryCollection.find(eq(USERNAME, username)).first();

            if (foundUserBetSummary != null) {
                Double amountOwing = foundUserBetSummary.getDouble(AMOUNT_OWING);
                Optional<Integer> numberOfPropBetsWon = Optional.ofNullable(foundUserBetSummary.getInteger(NUMBER_OF_PROPBETS_WON));
                Optional<Double> amountOfPropBetsWon = Optional.ofNullable(foundUserBetSummary.getDouble(AMOUNT_OF_PROPBETS_WON));
                Integer numberOfBetsWon = numberOfPropBetsWon.orElse(0) + numberOfScoreBoardBetsWon;
                Double amountOfScoreBoardBetsWon = winningBettersTotalMap.get(username);
                Double amountWon = BigDecimal.valueOf(amountOfPropBetsWon.orElse(0.0) + amountOfScoreBoardBetsWon)
                                             .setScale(2, RoundingMode.HALF_UP)
                                             .doubleValue();
                Double netAmount = BigDecimal.valueOf(amountWon - amountOwing).setScale(2, RoundingMode.HALF_UP).doubleValue();

                userBetsSummaryCollection.updateOne(eq(USERNAME, username),
                                                    Updates.combine(Updates.set(NUMBER_OF_SCOREBOARD_BETS_WON, numberOfScoreBoardBetsWon),
                                                                    Updates.set(AMOUNT_OF_SCOREBOARD_BETS_WON, amountOfScoreBoardBetsWon),
                                                                    Updates.set(NUMBER_OF_BETS_WON, numberOfBetsWon),
                                                                    Updates.set(AMOUNT_WON, amountWon),
                                                                    Updates.set(NET_AMOUNT, netAmount)));
            }
        });
    }

    public void createScoreBoardEventsTracker() {
        List<String> allBetters = new ArrayList<>();

        List<ScoreBoardBetsSummary> scoreBoardBetsSummary = getScoreBoardBetsSummary();

        scoreBoardBetsSummary.forEach(summary -> allBetters.addAll(summary.betters()));

        Document document = new Document().append(IS_SCOREBOARD_EVENTS_TRACKER, true)
                                          .append(TOTAL_AMOUNT_OF_BETS, allBetters.size() * AMOUNT_PER_BET)
                                          .append(NUMBER_OF_WINNING_EVENTS, 0)
                                          .append(TOTAL_AMOUNT_WON_PER_EVENT, 0.0);

        scoreCollection.insertOne(document);
    }

    public void lockPropBets() {
        List<PropBet> propBets = getPropBets();
        List<UserBet> userBets = getUserBets();

        propBets.forEach(
                propBet -> propBetsCollection.updateOne(eq(NAME, propBet.name()), Updates.set(IS_LOCKED, true)));
        userBets.forEach(
                userBet -> userBetsCollection.updateOne(and(eq(USERNAME, userBet.username()), eq(BET_TYPE, userBet.betType()), eq(BET_VALUE, userBet.betValue())), Updates.set(IS_LOCKED, true)));
    }

    public void deleteAllData() {
        propBetsSummaryCollection.deleteMany(new Document());
        resultsCollection.deleteMany(new Document());
        scoreCollection.deleteMany(new Document());
        userBetsCollection.deleteMany(new Document());
        userBetsSummaryCollection.deleteMany(new Document());
    }
}
