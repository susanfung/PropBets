package com.example.application.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.application.views.placebets.PlaceBets.AMOUNT_PER_BET;
import static com.mongodb.client.model.Filters.eq;
import static org.approvaltests.Approvals.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataServiceTest {
    @Mock
    private MongoClient mockMongoClient;

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    private MongoCollection<Document> mockUserBetsSummaryCollection;

    @Mock
    private MongoCollection<Document> mockUserBetsCollection;

    @Mock
    private MongoCollection<Document> mockPropBetsCollection;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @Mock
    private MongoCursor<Document> mockCursor;

    private DataService dataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockMongoClient.getDatabase("SuperBowl")).thenReturn(mockDatabase);
        when(mockDatabase.getCollection("UserBetsSummary")).thenReturn(mockUserBetsSummaryCollection);
        when(mockDatabase.getCollection("UserBets")).thenReturn(mockUserBetsCollection);
        when(mockDatabase.getCollection("PropBets")).thenReturn(mockPropBetsCollection);

        dataService = new DataService(mockMongoClient);
    }

    @Test
    void getUserBetsSummary() {
        when(mockUserBetsSummaryCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);

        Document mockUserBetsSummaryDocument = new Document();
        mockUserBetsSummaryDocument.append("username", "john_doe")
                                   .append("numberOfBetsMade", 5)
                                   .append("amountOwing", 100.0)
                                   .append("numberOfBetsWon", 3)
                                   .append("amountWon", 150.0)
                                   .append("netAmount", 50.0);
        when(mockCursor.next()).thenReturn(mockUserBetsSummaryDocument);

        verify(dataService.getUserBetsSummary()
                          .stream()
                          .map(UserBetsSummary::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getUserBets() {
        when(mockUserBetsCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);

        Document mockUserBetDocument = new Document();
        mockUserBetDocument.append("username", "john_doe")
                          .append("betType", "Team 1 Score")
                          .append("betValue", "100");
        when(mockCursor.next()).thenReturn(mockUserBetDocument);

        verify(dataService.getUserBets()
                          .stream()
                          .map(UserBet::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getPropBets() {
        when(mockPropBetsCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);

        Document mockPropBetDocument = new Document();
        mockPropBetDocument.append("name", "Super Bowl MVP")
                          .append("question", "Who will be the Super Bowl MVP?")
                          .append("choices", List.of("Tom Brady", "Patrick Mahomes", "Aaron Rodgers", "Josh Allen"));
        when(mockCursor.next()).thenReturn(mockPropBetDocument);

        verify(dataService.getPropBets()
                          .stream()
                          .map(PropBet::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void isPropBetNameTaken_whenPropBetNameDoesNotExist_returnFalse() {
        when(mockPropBetsCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        verify(dataService.isPropBetNameTaken("Super Bowl MVP"));
    }

    @Test
    void isPropBetNameTaken_whenPropBetNameExists_returnTrue() {
        when(mockPropBetsCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(new Document());

        verify(dataService.isPropBetNameTaken("Super Bowl MVP"));
    }

    @Test
    void saveScoreBoardBets() {
        String username = "john_doe";
        String betType1 = "Team 1 Score";
        String betType2 = "Team 2 Score";
        String betValue1 = "100";
        String betValue2 = "200";

        Document document1 = new Document();
        document1.append("username", username)
                 .append("betType", betType1)
                 .append("betValue", betValue1);

        Document document2 = new Document();
        document2.append("username", username)
                 .append("betType", betType2)
                 .append("betValue", betValue2);

        dataService.saveScoreBoardBets(username, Map.of(betValue1, betType1, betValue2, betType2));

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockUserBetsCollection, times(2)).insertOne(documentCaptor.capture());
        assertThat(documentCaptor.getAllValues(), containsInAnyOrder(document1, document2));
    }

    @Test
    void savePropBets() {
        String username = "john_doe";
        String betType1 = "Team 1 Score";
        String betType2 = "Team 2 Score";
        String betValue1 = "100";
        String betValue2 = "200";

        Document document1 = new Document();
        document1.append("username", username)
                 .append("betType", betType1)
                 .append("betValue", betValue1);

        Document document2 = new Document();
        document2.append("username", username)
                 .append("betType", betType2)
                 .append("betValue", betValue2);

        dataService.savePropBets(username, Map.of(betType1, betValue1, betType2, betValue2));

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockUserBetsCollection, times(2)).insertOne(captor.capture());
        assertThat(captor.getAllValues(), containsInAnyOrder(document1, document2));
    }

    @Test
    void addUserBet() {
        UserBet userBet = new UserBet("john_doe", "Team 1 Score", "100");

        dataService.addUserBet(userBet);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockUserBetsCollection, times(1)).insertOne(any());
        Mockito.verify(mockUserBetsCollection).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void createNewPropBet() {
        dataService.createNewPropBet("Super Bowl MVP", "Who will be the Super Bowl MVP?", "Tom Brady, Patrick Mahomes, Aaron Rodgers, Josh Allen");

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockPropBetsCollection, times(1)).insertOne(any());
        Mockito.verify(mockPropBetsCollection).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void updateUser_whenUserDoesNotExist_addsNewUser() {
        when(mockUserBetsSummaryCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        dataService.updateUser("john_doe", 5, 5 * AMOUNT_PER_BET);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockUserBetsSummaryCollection, never()).updateOne(any(), Collections.singletonList(any()));
        Mockito.verify(mockUserBetsSummaryCollection, times(1)).insertOne(any());
        Mockito.verify(mockUserBetsSummaryCollection).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void updateUser_whenUserExists_updatesUser() {
        String username = "john_doe";
        int numberOfBetsMade = 5;

        Document mockUserDocument = new Document();
        mockUserDocument.append("username", username)
                        .append("numberOfBetsMade", 5)
                        .append("amountOwing", 100.0)
                        .append("numberOfBetsWon", 3)
                        .append("amountWon", 150.0)
                        .append("netAmount", 50.0);

        when(mockUserBetsSummaryCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(mockUserDocument);

        dataService.updateUser(username, numberOfBetsMade, numberOfBetsMade * AMOUNT_PER_BET);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Bson> updateCaptor = ArgumentCaptor.forClass(Bson.class);

        Mockito.verify(mockUserBetsSummaryCollection, never()).insertOne(any());
        Mockito.verify(mockUserBetsSummaryCollection).updateOne(filterCaptor.capture(), updateCaptor.capture());
        verify(updateCaptor.getValue().toString());
    }
}
