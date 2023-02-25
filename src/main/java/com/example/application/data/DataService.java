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

    public List<Document> getDocuments() {
        MongoDatabase database = mongoClient.getDatabase("mydatabase");
        MongoCollection<Document> collection = database.getCollection("mycollection");
        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> documents = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                documents.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return documents;
    }
}