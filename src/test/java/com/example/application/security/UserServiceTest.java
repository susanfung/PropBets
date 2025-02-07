package com.example.application.security;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.approvaltests.Approvals.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    @Mock
    private MongoClient mockMongoClient;

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    private MongoCollection<Document> mockUsersCollection;

    @Mock
    private FindIterable<Document> mockFindIterable;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockMongoClient.getDatabase("SuperBowl")).thenReturn(mockDatabase);
        when(mockDatabase.getCollection("Users")).thenReturn(mockUsersCollection);

        userService = new UserService(mockMongoClient);
    }

    @Test
    void saveUser() {
        userService.saveUser("john_doe");

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        Mockito.verify(mockUsersCollection).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void updateUser() {
        String username = "john_doe";
        String firstName = "John";
        String lastName = "Doe";
        byte[] profileImage = new byte[]{1, 2, 3};

        userService.updateUser(username, firstName, lastName, profileImage);

        ArgumentCaptor<Document> queryCaptor = ArgumentCaptor.forClass(Document.class);
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class);
        Mockito.verify(mockUsersCollection).updateOne(queryCaptor.capture(), updateCaptor.capture());

        List<String> results = new ArrayList<>();
        results.add(queryCaptor.getValue().toString());
        results.add(((Document) updateCaptor.getValue().get("$set")).getString("firstName"));
        results.add(((Document) updateCaptor.getValue().get("$set")).getString("lastName"));

        verify(String.join("\n", results));
    }

    @Test
    void findUserByUsername() {
        String username = "JohnDoe";

        when(mockUsersCollection.find(eq(any()))).thenReturn(mockFindIterable);
        Document mockUsersDocument = new Document();
        mockUsersDocument.append("username", username);
        when(mockFindIterable.first()).thenReturn(mockUsersDocument);

        verify(userService.findUserByUsername(username.toLowerCase()).toString());
    }
}
