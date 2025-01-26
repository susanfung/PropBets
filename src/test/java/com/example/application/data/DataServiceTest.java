package com.example.application.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
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
import static com.mongodb.client.model.Filters.and;
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
    private MongoCollection<Document> mockPropBetsSummaryCollection;

    @Mock
    private MongoCollection<Document> mockUserBetsCollection;

    @Mock
    private MongoCollection<Document> mockPropBetsCollection;

    @Mock
    private MongoCollection<Document> mockResultsCollection;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @Mock
    private FindIterable<Document> mockPropBetFindIterable;

    @Mock
    private FindIterable<Document> mockPropBetsSummaryFindIterable;

    @Mock
    private FindIterable<Document> mockUserBetsSummaryFindIterable;

    @Mock
    private MongoCursor<Document> mockCursor;

    private DataService dataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockMongoClient.getDatabase("SuperBowl")).thenReturn(mockDatabase);
        when(mockDatabase.getCollection("UserBetsSummary")).thenReturn(mockUserBetsSummaryCollection);
        when(mockDatabase.getCollection("PropBetsSummary")).thenReturn(mockPropBetsSummaryCollection);
        when(mockDatabase.getCollection("UserBets")).thenReturn(mockUserBetsCollection);
        when(mockDatabase.getCollection("PropBets")).thenReturn(mockPropBetsCollection);
        when(mockDatabase.getCollection("Results")).thenReturn(mockResultsCollection);

        dataService = new DataService(mockMongoClient);
    }

    @Test
    void getUserBetsSummary() {
        Document mockUserBetsSummaryDocument = new Document();
        mockUserBetsSummaryDocument.append("username", "john_doe")
                                   .append("numberOfBetsMade", 5)
                                   .append("amountOwing", 100.0)
                                   .append("numberOfBetsWon", 3)
                                   .append("amountWon", 150.0)
                                   .append("netAmount", 50.0);

        when(mockUserBetsSummaryCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(mockUserBetsSummaryDocument);

        verify(dataService.getUserBetsSummary()
                          .stream()
                          .map(UserBetsSummary::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getPropBetsSummary_whenReceivesNonScoreBetType_returnsPropBetsSummary() {
        String betType = "Proposal";
        String question = "Will Kelce propose at the game?";

        Document mockPropBetsSummaryDocument1 = new Document();
        mockPropBetsSummaryDocument1.append("betType", betType)
                                    .append("betValue", "Yes")
                                    .append("betters", List.of("jane_doe", "john_doe"))
                                    .append("question", question)
                                    .append("isWinner", true);

        Document mockPropBetsSummaryDocument2 = new Document();
        mockPropBetsSummaryDocument2.append("betType", betType)
                                    .append("betValue", "No")
                                    .append("betters", List.of("jack_doe", "jill_doe"))
                                    .append("question", question)
                                    .append("isWinner", false);

        when(mockPropBetsSummaryCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(mockPropBetsSummaryDocument1, mockPropBetsSummaryDocument2);

        verify(dataService.getPropBetsSummary()
                          .stream()
                          .map(PropBetsSummary::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getPropBetsSummary_whenReceivesScoreBetType_returnsPropBetsSummary() {
        String betType = "Proposal";
        String betValue = "Yes";

        Document mockPropBetsSummaryDocument1 = new Document();
        mockPropBetsSummaryDocument1.append("betType", betType)
                                    .append("betValue", betValue)
                                    .append("betters", List.of("jane_doe", "john_doe"))
                                    .append("question", "Will Kelce propose at the game?")
                                    .append("isWinner", true);

        Document mockPropBetsSummaryDocument2 = new Document();
        mockPropBetsSummaryDocument2.append("betType", "Score")
                                    .append("betValue", "0,0")
                                    .append("betters", List.of("jack_doe", "jill_doe"));

        when(mockPropBetsSummaryCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(mockPropBetsSummaryDocument1, mockPropBetsSummaryDocument2);

        verify(dataService.getPropBetsSummary()
                          .stream()
                          .map(PropBetsSummary::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getScoreBoardBetsSummary_whenReceivesScoreBetType_returnsScoreBoardBetsSummary() {
        String betType = "Score";

        Document mockPropBetsSummaryDocument1 = new Document();
        mockPropBetsSummaryDocument1.append("betType", betType)
                                    .append("betValue", "0,1")
                                    .append("betters", List.of("jane_doe", "john_doe"));

        Document mockPropBetsSummaryDocument2 = new Document();
        mockPropBetsSummaryDocument2.append("betType", betType)
                                    .append("betValue", "0,0")
                                    .append("betters", List.of("jack_doe", "jill_doe"));

        when(mockPropBetsSummaryCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(mockPropBetsSummaryDocument1, mockPropBetsSummaryDocument2);

        Map<String, String> result = dataService.getScoreBoardBetsSummary();

        verify(result.entrySet()
                     .stream()
                     .map(entry -> entry.getKey() + ":\n" + entry.getValue())
                     .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getUserBets() {
        Document mockUserBetDocument = new Document();
        mockUserBetDocument.append("username", "john_doe")
                          .append("betType", "Team 1 Score")
                          .append("betValue", "100");

        when(mockUserBetsCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(mockUserBetDocument);

        verify(dataService.getUserBets()
                          .stream()
                          .map(UserBet::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void getPropBets() {
        Document mockPropBetDocument = new Document();
        mockPropBetDocument.append("name", "Super Bowl MVP")
                          .append("question", "Who will be the Super Bowl MVP?")
                          .append("choices", List.of("Tom Brady", "Patrick Mahomes", "Aaron Rodgers", "Josh Allen"));

        when(mockPropBetsCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(mockPropBetDocument);

        verify(dataService.getPropBets()
                          .stream()
                          .map(PropBet::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void findUserBetsByUsername() {
        String username = "john_doe";

        Document mockUserBetDocument = new Document();
        mockUserBetDocument.append("username", username)
                           .append("betType", "Team 1 Score")
                           .append("betValue", "100");

        when(mockUserBetsCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(mockUserBetDocument);

        verify(dataService.findUserBetsByUsername(username)
                          .stream()
                          .map(UserBet::toString)
                          .reduce("", (s1, s2) -> s1 + s2 + "\n"));
        Mockito.verify(mockCursor, times(1)).close();
    }

    @Test
    void findResults() {
        Document mockResultsDocument = new Document();
        mockResultsDocument.append("betType", "Team 1 Score")
                           .append("winningBetValue", "100");

        when(mockResultsCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(mockResultsDocument);

        verify(dataService.findResults()
                          .stream()
                          .map(Result::toString)
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
    void deletePreviousBets() {
        String username = "john_doe";

        when(mockUserBetsCollection.deleteMany(any())).thenReturn(null);
        when(mockPropBetsSummaryCollection.updateMany(any(), Updates.pull("betters", any()))).thenReturn(null);
        when(mockPropBetsSummaryCollection.deleteMany(eq("betters", any()))).thenReturn(null);

        dataService.deletePreviousBets(username);

        Mockito.verify(mockUserBetsCollection, times(1)).deleteMany(eq("username", username));
        Mockito.verify(mockPropBetsSummaryCollection, times(1)).updateMany(eq("betters", username), Updates.pull("betters", username));
        Mockito.verify(mockPropBetsSummaryCollection, times(1)).deleteMany(eq("betters", Collections.emptyList()));
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

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

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

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

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

        Mockito.verify(mockUserBetsCollection, times(1)).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void updatePropBetsSummary_whenPropBetsSummmaryDoesNotExist_addNewPropBetsSummary() {
        String betType = "Proposal";
        String betValue = "Yes";

        Document propBet = new Document();
        propBet.append("name", betType)
               .append("question", "Will Kelce propose at the game?")
               .append("choices", List.of(betValue, "No"));

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetFindIterable);
        when(mockPropBetFindIterable.first()).thenReturn(propBet);

        dataService.updatePropBetsSummary(betType, betValue, "john_doe");

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockPropBetsSummaryCollection, never()).updateOne(any(), Collections.singletonList(any()));
        Mockito.verify(mockPropBetsSummaryCollection, times(1)).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void updatePropBetsSummary_whenPropBetsSummaryExists_updatesPropBetsSummary() {
        String betType = "Proposal";
        String betValue = "Yes";
        String username = "john_doe";

        Document mockPropBetsSummaryDocument = new Document();
        mockPropBetsSummaryDocument.append("betType", betType)
                                   .append("betValue", betValue)
                                   .append("betters", List.of("jane_doe"));

        Document propBet = new Document();
        propBet.append("name", betType)
               .append("question", "Will Kelce propose at the game?")
               .append("choices", List.of(betValue, "No"));

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(mockPropBetsSummaryDocument);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetFindIterable);
        when(mockPropBetFindIterable.first()).thenReturn(propBet);

        dataService.updatePropBetsSummary(betType, betValue, username);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Bson> updateCaptor = ArgumentCaptor.forClass(Bson.class);

        Mockito.verify(mockPropBetsSummaryCollection, never()).insertOne(any());
        Mockito.verify(mockPropBetsSummaryCollection).updateOne(filterCaptor.capture(), updateCaptor.capture());
        verify(updateCaptor.getValue().toString());
    }

    @Test
    void updateUserBetsSummary_whenUserDoesNotExist_addsNewUserBetsSummary() {
        when(mockUserBetsSummaryCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        dataService.updateUserBetsSummary("john_doe", 5, 5 * AMOUNT_PER_BET);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockUserBetsSummaryCollection, never()).updateOne(any(), Collections.singletonList(any()));
        Mockito.verify(mockUserBetsSummaryCollection, times(1)).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void updateUserBetsSummary_whenUserExists_updatesUserBetsSummary() {
        String username = "john_doe";
        int numberOfBetsMade = 5;

        Document mockUserBetsSummaryDocument = new Document();
        mockUserBetsSummaryDocument.append("username", username)
                                   .append("numberOfBetsMade", 10)
                                   .append("amountOwing", 100.0)
                                   .append("numberOfBetsWon", 3)
                                   .append("amountWon", 150.0)
                                   .append("netAmount", 50.0);

        when(mockUserBetsSummaryCollection.find(eq(any()))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(mockUserBetsSummaryDocument);

        dataService.updateUserBetsSummary(username, numberOfBetsMade, numberOfBetsMade * AMOUNT_PER_BET);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Bson> updateCaptor = ArgumentCaptor.forClass(Bson.class);

        Mockito.verify(mockUserBetsSummaryCollection, never()).insertOne(any());
        Mockito.verify(mockUserBetsSummaryCollection).updateOne(filterCaptor.capture(), updateCaptor.capture());
        verify(updateCaptor.getValue().toString());
    }

    @Test
    void createNewPropBet() {
        dataService.createNewPropBet("Super Bowl MVP", "Who will be the Super Bowl MVP?", "Tom Brady, Patrick Mahomes, Aaron Rodgers, Josh Allen");

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockPropBetsCollection, times(1)).insertOne(captor.capture());
        verify(captor.getValue().toString());
    }

    @Test
    void saveResult() {
        String betType = "Proposal";
        String question = "Will Kelce propose at the game?";
        String winningBetValue = "Yes";
        String losingBetValue = "No";
        String winningUsername = "john_doe";
        String losingUsername = "jane_doe";
        Double amountOwing = 100.0;
        Integer numberOfBetsWon = 0;
        Double amountWon = 0.0;
        Double updatedAmountWon = amountWon + 2.67;

        Document propBet = new Document();
        propBet.append("name", betType)
               .append("question", question)
               .append("choices", List.of(winningBetValue, losingBetValue));

        Document winningPropBetsSummary = new Document();
        winningPropBetsSummary.append("betType", betType)
                              .append("betValue", winningBetValue)
                              .append("betters", List.of(winningUsername, "winner1", "winner2"))
                              .append("question", question);

        Document losingPropBetsSummary = new Document();
        losingPropBetsSummary.append("betType", betType)
                             .append("betValue", losingBetValue)
                             .append("betters", List.of(losingUsername))
                             .append("question", question);

        Document userBetsSummary = new Document();
        userBetsSummary.append("username", "john_doe")
                       .append("numberOfBetsMade", 5)
                       .append("amountOwing", amountOwing)
                       .append("numberOfBetsWon", numberOfBetsWon)
                       .append("amountWon", amountWon)
                       .append("netAmount", -100.0);

        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetFindIterable);
        when(mockPropBetFindIterable.first()).thenReturn(propBet);
        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", winningBetValue)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(winningPropBetsSummary);
        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", losingBetValue)))).thenReturn(mockPropBetsSummaryFindIterable);
        when(mockPropBetsSummaryFindIterable.first()).thenReturn(losingPropBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", any()))).thenReturn(mockUserBetsSummaryFindIterable);
        when(mockUserBetsSummaryFindIterable.first()).thenReturn(userBetsSummary);

        dataService.saveResult(betType, winningBetValue);

        ArgumentCaptor<Document> resultsCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockResultsCollection, times(1)).insertOne(resultsCaptor.capture());
        Mockito.verify(mockPropBetsSummaryCollection, times(2)).updateOne(and(eq("betvalue", any()), eq("betValue", winningBetValue)), Updates.set("isWinner", any()));
        Mockito.verify(mockPropBetsSummaryCollection).updateOne(and(eq("betType", betType), eq("betValue", winningBetValue)), Updates.set("isWinner", true));
        Mockito.verify(mockPropBetsSummaryCollection).updateOne(and(eq("betType", betType), eq("betValue", losingBetValue)), Updates.set("isWinner", false));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", winningUsername),
                          Updates.combine(Updates.set("numberOfBetsWon", numberOfBetsWon + 1),
                                          Updates.set("amountWon", updatedAmountWon),
                                          Updates.set("netAmount", updatedAmountWon - amountOwing)));
        verify(resultsCaptor.getValue().toString());
    }
}
