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

    @Autowired
    public DataService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public List<UserBet> getUserBets() {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
        MongoCollection<Document> collection = database.getCollection("UserBets");
        MongoCursor<Document> cursor = collection.find().iterator();

        List<UserBet> userBets = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                UserBet userBet = new UserBet(
                        document.getString("username"),
                        document.getString("betType"),
                        document.getString("betValue")
                );

                userBets.add(userBet);
            }
        } finally {
            cursor.close();
        }

        return userBets;
    }
}