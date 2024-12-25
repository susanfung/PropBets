package com.example.application.security;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final MongoCollection<Document> usersCollection;

    public UserService(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
        usersCollection = database.getCollection("Users");
    }

    public void saveUser(String username) {
        Document user = new Document("username", username);
        usersCollection.insertOne(user);
    }

    public Document findUserByUsername(String username) {
        return usersCollection.find(new Document("username", username)).first();
    }
}