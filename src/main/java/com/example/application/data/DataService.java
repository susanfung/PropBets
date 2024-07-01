package com.example.application.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {
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

    public List<UserBet> getUserBets() {
        MongoCursor<Document> cursor = userBetsCollection.find().iterator();

        List<UserBet> userBets = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                UserBet userBet = new UserBet(document.getString("username"),
                                              document.getString("betType"),
                                              document.getString("betValue"));

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

                PropBet propBet = new PropBet(document.getString("name"),
                                              document.getString("question"),
                                              document.getList("choices", String.class));

                propBets.add(propBet);
            }
        } finally {
            cursor.close();
        }

        return propBets;
    }

    public void addUserBet(UserBet userBet) {
        Document document = new Document().append("username", userBet.getUsername())
                                          .append("betType", userBet.getBetType())
                                          .append("betValue", userBet.getBetValue());

        userBetsCollection.insertOne(document);
    }

    public void addPropBet(PropBet propBet) {
        String name = propBet.getName();
        String question = propBet.getQuestion();
        List<String> choices = new ArrayList<>();

        propBet.getChoices().forEach(choice -> choices.add(toCamelCase(choice)));

        Document document = new Document().append("name", toCamelCase(name))
                                          .append("question", formatQuestion(question))
                                          .append("choices", choices);

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
        Document foundUser = usersCollection.find(Filters.eq("username", username)).first();

        if (foundUser != null) {
            Integer updatedNumberOfBetsMade = foundUser.getInteger("numberOfBetsMade") + numberOfBetsMade;
            Double updatedAmountOwing = foundUser.getDouble("amountOwing") + (numberOfBetsMade * 2);
            Double amountWon = foundUser.getDouble("amountWon");

            usersCollection.updateOne(Filters.eq("username", username),
                                      Updates.combine(Updates.set("numberOfBetsMade", updatedNumberOfBetsMade),
                                                      Updates.set("amountOwing", updatedAmountOwing),
                                                      Updates.set("numberOfBetsWon", foundUser.getInteger("numberOfBetsWon")),
                                                      Updates.set("amountWon", amountWon),
                                                      Updates.set("netAmount", amountWon - updatedAmountOwing)));
        } else {
            Double amountOwing = Double.valueOf(numberOfBetsMade * 2);
            Double amountWon = Double.valueOf(0);

            Document newUser = new Document("username", username).append("numberOfBetsMade", numberOfBetsMade)
                                                                 .append("amountOwing", amountOwing)
                                                                 .append("numberOfBetsWon", 0)
                                                                 .append("amountWon", amountWon)
                                                                 .append("netAmount", amountWon - amountOwing);

            usersCollection.insertOne(newUser);
        }
    }
}