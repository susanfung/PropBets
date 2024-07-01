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
    private final MongoCollection<Document> userBetsCollection;
    private final MongoCollection<Document> propBetsCollection;

    @Autowired
    public DataService(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
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
}