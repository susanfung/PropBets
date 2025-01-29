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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.application.views.placebets.PlaceBets.AMOUNT_PER_BET;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
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
    private MongoCollection<Document> mockScoreCollection;

    @Mock
    private FindIterable<Document> mockPropBetsFindIterable;

    @Mock
    private FindIterable<Document> mockUserBetsFindIterable;

    @Mock
    private FindIterable<Document> mockPropBetsSummaryFindIterable1;

    @Mock
    private FindIterable<Document> mockPropBetsSummaryFindIterable2;

    @Mock
    private FindIterable<Document> mockPropBetsSummaryFindIterable3;

    @Mock
    private FindIterable<Document> mockUserBetsSummaryFindIterable1;

    @Mock
    private FindIterable<Document> mockUserBetsSummaryFindIterable2;

    @Mock
    private FindIterable<Document> mockUserBetsSummaryFindIterable3;

    @Mock
    private FindIterable<Document> mockResultsFindIterable;

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
        when(mockDatabase.getCollection("Score")).thenReturn(mockScoreCollection);

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

        when(mockUserBetsSummaryCollection.find()).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.iterator()).thenReturn(mockCursor);
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

        when(mockPropBetsSummaryCollection.find()).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.iterator()).thenReturn(mockCursor);
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

        when(mockPropBetsSummaryCollection.find()).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.iterator()).thenReturn(mockCursor);
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

        when(mockPropBetsSummaryCollection.find()).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.iterator()).thenReturn(mockCursor);
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

        when(mockUserBetsCollection.find()).thenReturn(mockUserBetsFindIterable);
        when(mockUserBetsFindIterable.iterator()).thenReturn(mockCursor);
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

        when(mockPropBetsCollection.find()).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.iterator()).thenReturn(mockCursor);
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

        when(mockUserBetsCollection.find(eq(any()))).thenReturn(mockUserBetsFindIterable);
        when(mockUserBetsFindIterable.iterator()).thenReturn(mockCursor);
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

        when(mockResultsCollection.find()).thenReturn(mockResultsFindIterable);
        when(mockResultsFindIterable.iterator()).thenReturn(mockCursor);
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
        when(mockPropBetsCollection.find(eq(any()))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(null);

        verify(dataService.isPropBetNameTaken("Super Bowl MVP"));
    }

    @Test
    void isPropBetNameTaken_whenPropBetNameExists_returnTrue() {
        when(mockPropBetsCollection.find(eq(any()))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(new Document());

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

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(null);

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

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(null);

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

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.first()).thenReturn(null);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(propBet);

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

        when(mockPropBetsSummaryCollection.find(any(Bson.class))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.first()).thenReturn(mockPropBetsSummaryDocument);
        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(propBet);

        dataService.updatePropBetsSummary(betType, betValue, username);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Bson> updateCaptor = ArgumentCaptor.forClass(Bson.class);

        Mockito.verify(mockPropBetsSummaryCollection, never()).insertOne(any());
        Mockito.verify(mockPropBetsSummaryCollection).updateOne(filterCaptor.capture(), updateCaptor.capture());
        verify(updateCaptor.getValue().toString());
    }

    @Test
    void updateUserBetsSummary_whenUserDoesNotExist_addsNewUserBetsSummary() {
        when(mockUserBetsSummaryCollection.find(eq(any()))).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.first()).thenReturn(null);

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

        when(mockUserBetsSummaryCollection.find(eq(any()))).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.first()).thenReturn(mockUserBetsSummaryDocument);

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
        String winningUsername1 = "john_doe";
        String winningUsername2 = "jack_doe";
        String winningUsername3 = "joe_doe";
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
                              .append("betters", List.of(winningUsername1, winningUsername2, winningUsername3))
                              .append("question", question);

        Document losingPropBetsSummary = new Document();
        losingPropBetsSummary.append("betType", betType)
                             .append("betValue", losingBetValue)
                             .append("betters", List.of(losingUsername))
                             .append("question", question);

        Document winningUsername1UserBetsSummary = new Document();
        winningUsername1UserBetsSummary.append("username", winningUsername1)
                                       .append("numberOfBetsMade", 5)
                                       .append("amountOwing", amountOwing)
                                       .append("numberOfBetsWon", numberOfBetsWon)
                                       .append("amountWon", amountWon)
                                       .append("netAmount", -100.0);

        Document winningUsername2UserBetsSummary = new Document();
        winningUsername2UserBetsSummary.append("username", winningUsername2)
                                       .append("numberOfBetsMade", 5)
                                       .append("amountOwing", amountOwing)
                                       .append("numberOfBetsWon", numberOfBetsWon)
                                       .append("amountWon", amountWon)
                                       .append("netAmount", -100.0);

        Document winningUsername3UserBetsSummary = new Document();
        winningUsername3UserBetsSummary.append("username", winningUsername3)
                                       .append("numberOfBetsMade", 5)
                                       .append("amountOwing", amountOwing)
                                       .append("numberOfBetsWon", numberOfBetsWon)
                                       .append("amountWon", amountWon)
                                       .append("netAmount", -100.0);

        when(mockPropBetsCollection.find(any(Bson.class))).thenReturn(mockPropBetsFindIterable);
        when(mockPropBetsFindIterable.first()).thenReturn(propBet);
        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", winningBetValue)))).thenReturn(
                mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.first()).thenReturn(winningPropBetsSummary);
        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", losingBetValue)))).thenReturn(
                mockPropBetsSummaryFindIterable2);
        when(mockPropBetsSummaryFindIterable2.first()).thenReturn(losingPropBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", winningUsername1))).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.first()).thenReturn(winningUsername1UserBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", winningUsername2))).thenReturn(mockUserBetsSummaryFindIterable2);
        when(mockUserBetsSummaryFindIterable2.first()).thenReturn(winningUsername2UserBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", winningUsername3))).thenReturn(mockUserBetsSummaryFindIterable3);
        when(mockUserBetsSummaryFindIterable3.first()).thenReturn(winningUsername3UserBetsSummary);

        dataService.saveResult(betType, winningBetValue);

        ArgumentCaptor<Document> resultsCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockResultsCollection, times(1)).insertOne(resultsCaptor.capture());
        Mockito.verify(mockPropBetsSummaryCollection, times(2)).updateOne(and(eq("betvalue", any()), eq("betValue", winningBetValue)), Updates.set("isWinner", any()));
        Mockito.verify(mockPropBetsSummaryCollection).updateOne(and(eq("betType", betType), eq("betValue", winningBetValue)), Updates.set("isWinner", true));
        Mockito.verify(mockPropBetsSummaryCollection).updateOne(and(eq("betType", betType), eq("betValue", losingBetValue)), Updates.set("isWinner", false));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", winningUsername1),
                          Updates.combine(Updates.set("numberOfBetsWon", numberOfBetsWon + 1),
                                          Updates.set("amountWon", updatedAmountWon),
                                          Updates.set("netAmount", updatedAmountWon - amountOwing)));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", winningUsername2),
                          Updates.combine(Updates.set("numberOfBetsWon", numberOfBetsWon + 1),
                                          Updates.set("amountWon", updatedAmountWon),
                                          Updates.set("netAmount", updatedAmountWon - amountOwing)));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", winningUsername3),
                          Updates.combine(Updates.set("numberOfBetsWon", numberOfBetsWon + 1),
                                          Updates.set("amountWon", updatedAmountWon),
                                          Updates.set("netAmount", updatedAmountWon - amountOwing)));
        verify(resultsCaptor.getValue().toString());
    }

    @Test
    void saveScore_whenCountIsZero() {
        String team1Name = "Team 1";
        String team1Score = "12";
        String team2Name = "Team 2";
        String team2Score = "10";
        String betType = "Score";
        String betValue = "2,0";
        String username1 = "john_doe";
        String username2 = "jane_doe";
        Double amountOwing = 20.0;
        Integer numberOfScoreBoardBetsWonForUsername1 = 1;
        Integer numberOfScoreBoardBetsWonForUsername2 = 2;
        Double amountScoreBoardBetsWonForUsername1 = 4.67;
        Double amountScoreBoardBetsWonForUsername2 = 14.0;
        Integer numberOfPropBetsWon = 1;
        Double amountOfPropBetsWon = 1.0;
        Double updatedAmountWonForUsername1 = amountScoreBoardBetsWonForUsername1 + amountOfPropBetsWon;
        Double updatedAmountWonForUsername2 = amountScoreBoardBetsWonForUsername2 + amountOfPropBetsWon;

        Document scoreBoardEventsTracker = new Document();
        scoreBoardEventsTracker.append("isScoreBoardEventsTracker", true)
                               .append("totalAmountOfBets", 84.0)
                               .append("numberOfWinningEvents", 8)
                               .append("totalAmountWonPerEvent", 10.5);

        Document propBetsSummary = new Document();
        propBetsSummary.append("betType", betType)
                       .append("betValue", betValue)
                       .append("betters", List.of(username1, username2));

        Document winningPropBetsSummary1 = new Document();
        winningPropBetsSummary1.append("betType", betType)
                               .append("betValue", betValue)
                               .append("betters", List.of(username1, username2))
                               .append("count", 1);

        Document winningPropBetsSummary2 = new Document();
        winningPropBetsSummary2.append("betType", betType)
                               .append("betValue", "2,1")
                               .append("betters", List.of(username2))
                               .append("count", 1);

        Document username1UserBetsSummary = new Document();
        username1UserBetsSummary.append("username", username1)
                                .append("numberOfBetsMade", 5)
                                .append("amountOwing", amountOwing)
                                .append("numberOfScoreBoardBetsWon", 0)
                                .append("amountOfScoreBoardBetsWon", 0)
                                .append("numberOfPropBetsWon", numberOfPropBetsWon)
                                .append("amountOfPropBetsWon", amountOfPropBetsWon)
                                .append("numberOfBetsWon", 0)
                                .append("amountWon", 0)
                                .append("netAmount", -20.0);

        Document username2UserBetsSummary = new Document();
        username2UserBetsSummary.append("username", username2)
                                .append("numberOfBetsMade", 5)
                                .append("amountOwing", amountOwing)
                                .append("numberOfScoreBoardBetsWon", 0)
                                .append("amountOfScoreBoardBetsWon", 0)
                                .append("numberOfPropBetsWon", numberOfPropBetsWon)
                                .append("amountOfPropBetsWon", amountOfPropBetsWon)
                                .append("numberOfBetsWon", 0)
                                .append("amountWon", 0)
                                .append("netAmount", -20.0);

        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", betValue)))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.first()).thenReturn(propBetsSummary);
        when(mockPropBetsSummaryCollection.find(eq("isScoreBoardEventsTracker", true))).thenReturn(mockPropBetsSummaryFindIterable2);
        when(mockPropBetsSummaryFindIterable2.first()).thenReturn(scoreBoardEventsTracker);
        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), exists("count")))).thenReturn(mockPropBetsSummaryFindIterable3);
        when(mockPropBetsSummaryFindIterable3.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(winningPropBetsSummary1, winningPropBetsSummary2);
        when(mockUserBetsSummaryCollection.find(eq("username", username1))).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.first()).thenReturn(username1UserBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", username2))).thenReturn(mockUserBetsSummaryFindIterable2);
        when(mockUserBetsSummaryFindIterable2.first()).thenReturn(username2UserBetsSummary);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<Document> teamScoreCaptor = ArgumentCaptor.forClass(Document.class);
        ArgumentCaptor<Document> scoreBoardEventsTrackerCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockScoreCollection, times(1)).insertOne(teamScoreCaptor.capture());
        Mockito.verify(mockPropBetsSummaryCollection, times(1)).replaceOne(any(), scoreBoardEventsTrackerCaptor.capture());
        Mockito.verify(mockPropBetsSummaryCollection, times(1))
               .updateOne(and(eq("betType", betType), eq("betValue", betValue)), Updates.set("count", 1));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", username1),
                          Updates.combine(Updates.set("numberOfScoreBoardBetsWon", numberOfScoreBoardBetsWonForUsername1),
                                          Updates.set("amountOfScoreBoardBetsWon", amountScoreBoardBetsWonForUsername1),
                                          Updates.set("numberOfBetsWon", numberOfScoreBoardBetsWonForUsername1 + numberOfPropBetsWon),
                                          Updates.set("amountWon", updatedAmountWonForUsername1),
                                          Updates.set("netAmount", updatedAmountWonForUsername1 - amountOwing)));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", username2),
                          Updates.combine(Updates.set("numberOfScoreBoardBetsWon", numberOfScoreBoardBetsWonForUsername2),
                                          Updates.set("amountOfScoreBoardBetsWon", amountScoreBoardBetsWonForUsername2),
                                          Updates.set("numberOfBetsWon", numberOfScoreBoardBetsWonForUsername2 + numberOfPropBetsWon),
                                          Updates.set("amountWon", updatedAmountWonForUsername2),
                                          Updates.set("netAmount", updatedAmountWonForUsername2 - amountOwing)));

        StringBuilder results = new StringBuilder();
        results.append("Team Score:\n");
        results.append(teamScoreCaptor.getValue().toString());
        results.append("\nScoreBoard Events Tracker:\n");
        results.append(scoreBoardEventsTrackerCaptor.getValue().toString());
        verify(results.toString());
    }

    @Test
    void saveScore_whenCountIsOne() {
        String team1Name = "Team 1";
        String team1Score = "12";
        String team2Name = "Team 2";
        String team2Score = "10";
        String betType = "Score";
        String betValue = "2,0";
        String username1 = "john_doe";
        String username2 = "jane_doe";
        Double amountOwing = 20.0;
        Integer numberOfScoreBoardBetsWonForUsername1 = 1;
        Integer numberOfScoreBoardBetsWonForUsername2 = 2;
        Double amountScoreBoardBetsWonForUsername1 = 9.33;
        Double amountScoreBoardBetsWonForUsername2 = 18.66;
        Integer numberOfPropBetsWon = 1;
        Double amountOfPropBetsWon = 1.0;
        Double updatedAmountWonForUsername1 = amountScoreBoardBetsWonForUsername1 + amountOfPropBetsWon;
        Double updatedAmountWonForUsername2 = amountScoreBoardBetsWonForUsername2 + amountOfPropBetsWon;

        Document scoreBoardEventsTracker = new Document();
        scoreBoardEventsTracker.append("isScoreBoardEventsTracker", true)
                               .append("totalAmountOfBets", 84.0)
                               .append("numberOfWinningEvents", 8)
                               .append("totalAmountWonPerEvent", 10.5);

        Document propBetsSummary = new Document();
        propBetsSummary.append("betType", betType)
                       .append("betValue", betValue)
                       .append("betters", List.of(username1, username2))
                       .append("count", 1);

        Document winningPropBetsSummary1 = new Document();
        winningPropBetsSummary1.append("betType", betType)
                               .append("betValue", betValue)
                               .append("betters", List.of(username1, username2))
                               .append("count", 2);

        Document winningPropBetsSummary2 = new Document();
        winningPropBetsSummary2.append("betType", betType)
                               .append("betValue", "2,1")
                               .append("betters", List.of(username2))
                               .append("count", 1);

        Document username1UserBetsSummary = new Document();
        username1UserBetsSummary.append("username", username1)
                                .append("numberOfBetsMade", 5)
                                .append("amountOwing", amountOwing)
                                .append("numberOfScoreBoardBetsWon", 0)
                                .append("amountOfScoreBoardBetsWon", 0)
                                .append("numberOfPropBetsWon", numberOfPropBetsWon)
                                .append("amountOfPropBetsWon", amountOfPropBetsWon)
                                .append("numberOfBetsWon", 0)
                                .append("amountWon", 0)
                                .append("netAmount", -20.0);

        Document username2UserBetsSummary = new Document();
        username2UserBetsSummary.append("username", username2)
                                .append("numberOfBetsMade", 5)
                                .append("amountOwing", amountOwing)
                                .append("numberOfScoreBoardBetsWon", 0)
                                .append("amountOfScoreBoardBetsWon", 0)
                                .append("numberOfPropBetsWon", numberOfPropBetsWon)
                                .append("amountOfPropBetsWon", amountOfPropBetsWon)
                                .append("numberOfBetsWon", 0)
                                .append("amountWon", 0)
                                .append("netAmount", -20.0);

        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", betValue)))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.first()).thenReturn(propBetsSummary);
        when(mockPropBetsSummaryCollection.find(eq("isScoreBoardEventsTracker", true))).thenReturn(mockPropBetsSummaryFindIterable2);
        when(mockPropBetsSummaryFindIterable2.first()).thenReturn(scoreBoardEventsTracker);
        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), exists("count")))).thenReturn(mockPropBetsSummaryFindIterable3);
        when(mockPropBetsSummaryFindIterable3.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(winningPropBetsSummary1, winningPropBetsSummary2);
        when(mockUserBetsSummaryCollection.find(eq("username", username1))).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.first()).thenReturn(username1UserBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", username2))).thenReturn(mockUserBetsSummaryFindIterable2);
        when(mockUserBetsSummaryFindIterable2.first()).thenReturn(username2UserBetsSummary);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<Document> teamScoreCaptor = ArgumentCaptor.forClass(Document.class);
        ArgumentCaptor<Document> scoreBoardEventsTrackerCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockScoreCollection, times(1)).insertOne(teamScoreCaptor.capture());
        Mockito.verify(mockPropBetsSummaryCollection, times(1)).replaceOne(any(), scoreBoardEventsTrackerCaptor.capture());
        Mockito.verify(mockPropBetsSummaryCollection, times(1))
               .updateOne(and(eq("betType", betType), eq("betValue", betValue)), Updates.set("count", 2));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", username1),
                          Updates.combine(Updates.set("numberOfScoreBoardBetsWon", numberOfScoreBoardBetsWonForUsername1),
                                          Updates.set("amountOfScoreBoardBetsWon", amountScoreBoardBetsWonForUsername1),
                                          Updates.set("numberOfBetsWon", numberOfScoreBoardBetsWonForUsername1 + numberOfPropBetsWon),
                                          Updates.set("amountWon", updatedAmountWonForUsername1),
                                          Updates.set("netAmount", updatedAmountWonForUsername1 - amountOwing)));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", username2),
                          Updates.combine(Updates.set("numberOfScoreBoardBetsWon", numberOfScoreBoardBetsWonForUsername2),
                                          Updates.set("amountOfScoreBoardBetsWon", amountScoreBoardBetsWonForUsername2),
                                          Updates.set("numberOfBetsWon", numberOfScoreBoardBetsWonForUsername2 + numberOfPropBetsWon),
                                          Updates.set("amountWon", updatedAmountWonForUsername2),
                                          Updates.set("netAmount", updatedAmountWonForUsername2 - amountOwing)));

        StringBuilder results = new StringBuilder();
        results.append("Team Score:\n");
        results.append(teamScoreCaptor.getValue().toString());
        results.append("\nScoreBoard Events Tracker:\n");
        results.append(scoreBoardEventsTrackerCaptor.getValue().toString());
        verify(results.toString());
    }

    @Test
    void saveScore_whenNoEventsFound() {
        String team1Name = "Team 1";
        String team1Score = "12";
        String team2Name = "Team 2";
        String team2Score = "10";
        String betType = "Score";
        String betValue = "2,0";

        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), eq("betValue", betValue)))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.first()).thenReturn(null);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<Document> teamScoreCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockScoreCollection, times(1)).insertOne(teamScoreCaptor.capture());
        Mockito.verifyNoInteractions(mockPropBetsCollection);
        Mockito.verifyNoInteractions(mockUserBetsSummaryCollection);
        verify(teamScoreCaptor.getValue().toString());
    }

    @Test
    void saveResultsToCollection() {
        dataService.saveResultsToCollection(mockScoreCollection, "Team 1", "12", "Team 2", "10");

        ArgumentCaptor<Document> resultsCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockScoreCollection, times(1)).insertOne(resultsCaptor.capture());
        verify(resultsCaptor.getValue().toString());
    }

    @Test
    void updateScoreBoardEventsTracker() {
        Document scoreBoardEventsTracker = new Document();
        scoreBoardEventsTracker.append("isScoreBoardEventsTracker", true)
                               .append("totalAmountOfBets", 84.0)
                               .append("numberOfWinningEvents", 8)
                               .append("totalAmountWonPerEvent", 10.5);

        dataService.updateScoreBoardEventsTracker(scoreBoardEventsTracker, new Double[]{0.0});

        ArgumentCaptor<Document> resultsCaptor = ArgumentCaptor.forClass(Document.class);

        Mockito.verify(mockPropBetsSummaryCollection, times(1)).replaceOne(any(), resultsCaptor.capture());
        verify(resultsCaptor.getValue().toString());
    }

    @Test
    void updateScoreInPropBetsSummmary_whenCountDoesNotExist() {
        String betType = "Score";
        String betValue = "2,0";

        Document propBetsSummary = new Document();
        propBetsSummary.append("betType", betType)
                       .append("betValue", betValue)
                       .append("betters", List.of("john_doe"));

        dataService.updateScoreInPropBetsSummmary(propBetsSummary);

        Mockito.verify(mockPropBetsSummaryCollection, times(1))
               .updateOne(and(eq("betType", betType), eq("betValue", betValue)), Updates.set("count", 1));
    }

    @Test
    void updateScoreInPropBetsSummmary_whenCountDoesExist() {
        String betType = "Score";
        String betValue = "2,0";
        Integer count = 1;

        Document propBetsSummary = new Document();
        propBetsSummary.append("betType", betType)
                       .append("betValue", betValue)
                       .append("betters", List.of("john_doe"))
                       .append("count", count);

        dataService.updateScoreInPropBetsSummmary(propBetsSummary);

        Mockito.verify(mockPropBetsSummaryCollection, times(1))
               .updateOne(and(eq("betType", betType), eq("betValue", betValue)), Updates.set("count", count + 1));
    }

    @Test
    void findAllWinningScoreEvents() {
        Map<String, Integer> winningBettersCountMap = new HashMap<>();
        Map<String, Double> winningBettersTotalMap = new HashMap<>();
        String betType = "Score";
        String username1 = "john_doe";
        String username2 = "jane_doe";

        Document winningPropBetsSummary1 = new Document();
        winningPropBetsSummary1.append("betType", betType)
                               .append("betValue", "2,0")
                               .append("betters", List.of(username1, username2))
                               .append("count", 1);

        Document winningPropBetsSummary2 = new Document();
        winningPropBetsSummary2.append("betType", betType)
                               .append("betValue", "2,1")
                               .append("betters", List.of(username2))
                               .append("count", 1);

        when(mockPropBetsSummaryCollection.find(and(eq("betType", betType), exists("count")))).thenReturn(mockPropBetsSummaryFindIterable1);
        when(mockPropBetsSummaryFindIterable1.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(winningPropBetsSummary1, winningPropBetsSummary2);

        dataService.findAllWinningScoreEvents(winningBettersCountMap, winningBettersTotalMap, new Double[]{9.33});

        StringBuilder results = new StringBuilder();
        results.append("Winning Betters Count Map:\n");
        winningBettersCountMap.forEach((key, value) -> results.append(key).append(": ").append(value).append("\n"));
        results.append("\nWinning Betters Total Map:\n");
        winningBettersTotalMap.forEach((key, value) -> results.append(key).append(": ").append(value).append("\n"));

        verify(results.toString());
    }

    @Test
    void updateScoreBoardBetsInUserBetsSummary() {
        Map<String, Integer> winningBettersCountMap = new HashMap<>();
        String username1 = "jane_doe";
        String username2 = "john_doe";
        Double amountOwing = 20.0;
        Integer numberOfScoreBoardBetsWonForUsername1 = 2;
        Integer numberOfScoreBoardBetsWonForUsername2 = 1;
        Double amountScoreBoardBetsWonForUsername1 = 14.0;
        Double amountScoreBoardBetsWonForUsername2 = 4.67;
        Integer numberOfPropBetsWon = 1;
        Double amountOfPropBetsWon = 1.0;
        Double updatedAmountWonForUsername1 = amountScoreBoardBetsWonForUsername1 + amountOfPropBetsWon;
        Double updatedAmountWonForUsername2 = amountScoreBoardBetsWonForUsername2 + amountOfPropBetsWon;

        winningBettersCountMap.put(username1, numberOfScoreBoardBetsWonForUsername1);
        winningBettersCountMap.put(username2, numberOfScoreBoardBetsWonForUsername2);

        Map<String, Double> winningBettersTotalMap = new HashMap<>();
        winningBettersTotalMap.put(username1, amountScoreBoardBetsWonForUsername1);
        winningBettersTotalMap.put(username2, amountScoreBoardBetsWonForUsername2);

        Document username1UserBetsSummary = new Document();
        username1UserBetsSummary.append("username", username1)
                                .append("numberOfBetsMade", 5)
                                .append("amountOwing", amountOwing)
                                .append("numberOfScoreBoardBetsWon", 0)
                                .append("amountOfScoreBoardBetsWon", 0)
                                .append("numberOfPropBetsWon", numberOfPropBetsWon)
                                .append("amountOfPropBetsWon", amountOfPropBetsWon)
                                .append("numberOfBetsWon", 0)
                                .append("amountWon", 0)
                                .append("netAmount", -20.0);

        Document username2UserBetsSummary = new Document();
        username2UserBetsSummary.append("username", username2)
                                .append("numberOfBetsMade", 5)
                                .append("amountOwing", amountOwing)
                                .append("numberOfScoreBoardBetsWon", 0)
                                .append("amountOfScoreBoardBetsWon", 0)
                                .append("numberOfPropBetsWon", numberOfPropBetsWon)
                                .append("amountOfPropBetsWon", amountOfPropBetsWon)
                                .append("numberOfBetsWon", 0)
                                .append("amountWon", 0)
                                .append("netAmount", -20.0);

        when(mockUserBetsSummaryCollection.find(eq("username", username1))).thenReturn(mockUserBetsSummaryFindIterable1);
        when(mockUserBetsSummaryFindIterable1.first()).thenReturn(username1UserBetsSummary);
        when(mockUserBetsSummaryCollection.find(eq("username", username2))).thenReturn(mockUserBetsSummaryFindIterable2);
        when(mockUserBetsSummaryFindIterable2.first()).thenReturn(username2UserBetsSummary);

        dataService.updateScoreBoardBetsInUserBetsSummary(winningBettersCountMap, winningBettersTotalMap);

        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", username1),
                          Updates.combine(Updates.set("numberOfScoreBoardBetsWon", numberOfScoreBoardBetsWonForUsername1),
                                          Updates.set("amountOfScoreBoardBetsWon", amountScoreBoardBetsWonForUsername1),
                                          Updates.set("numberOfBetsWon", numberOfScoreBoardBetsWonForUsername1 + numberOfPropBetsWon),
                                          Updates.set("amountWon", updatedAmountWonForUsername1),
                                          Updates.set("netAmount", updatedAmountWonForUsername1 - amountOwing)));
        Mockito.verify(mockUserBetsSummaryCollection)
               .updateOne(eq("username", username2),
                          Updates.combine(Updates.set("numberOfScoreBoardBetsWon", numberOfScoreBoardBetsWonForUsername2),
                                          Updates.set("amountOfScoreBoardBetsWon", amountScoreBoardBetsWonForUsername2),
                                          Updates.set("numberOfBetsWon", numberOfScoreBoardBetsWonForUsername2 + numberOfPropBetsWon),
                                          Updates.set("amountWon", updatedAmountWonForUsername2),
                                          Updates.set("netAmount", updatedAmountWonForUsername2 - amountOwing)));
    }
}
