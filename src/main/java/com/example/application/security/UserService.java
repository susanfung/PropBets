package com.example.application.security;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final MongoCollection<Document> usersCollection;

    @Autowired
    public UserService(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("SuperBowl");
        usersCollection = database.getCollection("Users");
    }

    public void saveUser(String username) {
        Document user = new Document("username", username).append("role", "user");
        usersCollection.insertOne(user);
    }

    public void updateUser(String username, String firstName, String lastName, byte[] profileImage) {
        Document query = new Document("username", username);
        Document update = new Document();

        if (firstName != null) {
            update.append("firstName", firstName);
        }

        if (lastName != null) {
            update.append("lastName", lastName);
        }

        if (profileImage != null) {
            update.append("profileImage", profileImage);
        }

        usersCollection.updateOne(query, new Document("$set", update));
    }

    public Document findUserByUsername(String username) {
        return usersCollection.find(new Document("username", new Document("$regex", "^" + username + "$").append("$options", "i"))).first();
    }
}
