package com.example.application.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> userBetsCollection;

    @Autowired
    public DataService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        database = mongoClient.getDatabase("SuperBowl");
        userBetsCollection = database.getCollection("UserBets");
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

    public void addUserBet(UserBet userBet) {
        Document document = new Document().append("username", userBet.getUsername())
                                          .append("betType", userBet.getBetType())
                                          .append("betValue", userBet.getBetValue());

        userBetsCollection.insertOne(document);
    }
}