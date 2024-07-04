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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;

@Service
public class DataService {
    private static final String USERNAME = "username";
    private static final String BET_TYPE = "betType";
    private static final String BET_VALUE = "betValue";
    private static final String NAME = "name";
    private static final String QUESTION = "question";
    private static final String CHOICES = "choices";
    private static final String NUMBER_OF_BETS_MADE = "numberOfBetsMade";
    private static final String AMOUNT_OWING = "amountOwing";
    private static final String NUMBER_OF_BETS_WON = "numberOfBetsWon";
    private static final String AMOUNT_WON = "amountWon";
    private static final String NET_AMOUNT = "netAmount";

    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> userBetsCollection;
    private final MongoCollection<Document> propBetsCollection;

    @Autowired
    public DataService(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
        usersCollection = database.getCollection("Users");
        userBetsCollection = database.getCollection("UserBets");
        propBetsCollection = database.getCollection("PropBets");
    }

    public List<User> getUsers() {
        MongoCursor<Document> cursor = usersCollection.find().iterator();

        List<User> users = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                User user = new User(document.getString(USERNAME),
                                     document.getInteger(NUMBER_OF_BETS_MADE),
                                     document.getDouble(AMOUNT_OWING),
                                     document.getInteger(NUMBER_OF_BETS_WON),
                                     document.getDouble(AMOUNT_WON),
                                     document.getDouble(NET_AMOUNT));

                users.add(user);
            }
        } finally {
            cursor.close();
        }

        return users;
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

    public boolean isPropBetNameTaken(String name) {
        return propBetsCollection.find(eq(NAME, toCamelCase(name))).first() != null;
    }

    public void saveScoreBoardBets(String username, Map<String, String> bets) {
        bets.forEach((betValue, betType) -> {
            UserBet bet = new UserBet(username, betType, betValue);
            addUserBet(bet);
        });
    }

    public void savePropBets(String username, Map<String, String> bets) {
        bets.forEach((betType, betValue) -> {
            UserBet bet = new UserBet(username, betType, betValue);
            addUserBet(bet);
        });
    }

    public void addUserBet(UserBet userBet) {
        Document document = new Document().append(USERNAME, userBet.getUsername())
                                          .append(BET_TYPE, userBet.getBetType())
                                          .append(BET_VALUE, userBet.getBetValue());

        userBetsCollection.insertOne(document);
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

    public void updateUser(String username, Integer numberOfBetsMade) {
        Document foundUser = usersCollection.find(eq(USERNAME, username)).first();

        if (foundUser != null) {
            Integer updatedNumberOfBetsMade = foundUser.getInteger(NUMBER_OF_BETS_MADE) + numberOfBetsMade;
            Double updatedAmountOwing = foundUser.getDouble(AMOUNT_OWING) + (numberOfBetsMade * 2);
            Double amountWon = foundUser.getDouble(AMOUNT_WON);

            usersCollection.updateOne(eq(USERNAME, username),
                                      Updates.combine(Updates.set(NUMBER_OF_BETS_MADE, updatedNumberOfBetsMade),
                                                      Updates.set(AMOUNT_OWING, updatedAmountOwing),
                                                      Updates.set(NUMBER_OF_BETS_WON, foundUser.getInteger(NUMBER_OF_BETS_WON)),
                                                      Updates.set(AMOUNT_WON, amountWon),
                                                      Updates.set(NET_AMOUNT, amountWon - updatedAmountOwing)));
        } else {
            Double amountOwing = Double.valueOf(numberOfBetsMade * 2);
            Double amountWon = Double.valueOf(0);

            Document newUser = new Document(USERNAME, username).append(NUMBER_OF_BETS_MADE, numberOfBetsMade)
                                                               .append(AMOUNT_OWING, amountOwing)
                                                               .append(NUMBER_OF_BETS_WON, 0)
                                                               .append(AMOUNT_WON, amountWon)
                                                               .append(NET_AMOUNT, amountWon - amountOwing);

            usersCollection.insertOne(newUser);
        }
    }
}