package com.example.application.data;

import com.example.application.supabase.SupabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.application.data.DataService.TABLE_PROP_BETS;
import static com.example.application.data.DataService.TABLE_PROP_BETS_SUMMARY;
import static com.example.application.data.DataService.TABLE_RESULTS;
import static com.example.application.data.DataService.TABLE_SCORE;
import static com.example.application.data.DataService.TABLE_SCOREBOARD_EVENTS_TRACKER;
import static com.example.application.data.DataService.TABLE_SCORE_BETS_SUMMARY;
import static com.example.application.data.DataService.TABLE_USER_BETS;
import static com.example.application.data.DataService.TABLE_USER_BETS_SUMMARY;
import static org.approvaltests.Approvals.verify;

class DataServiceTest {
    @Mock
    private SupabaseService mockSupabaseService;

    private DataService dataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataService = new DataService(mockSupabaseService);
    }

    @Test
    void getUserBetsSummary() throws Exception {
        String response = "[{\"username\":\"john_doe\",\"number_of_bets_made\":5,\"amount_owing\":100.0,\"number_of_bets_won\":3,\"amount_won\":150.0,\"net_amount\":50.0,\"amount_of_scoreboard_bets_won\":36.99,\"number_of_scoreboard_bets_won\":4,\"amount_of_propbets_won\":2.4,\"number_of_propbets_won\":1}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString())).thenReturn(response);

        String result = dataService.getUserBetsSummary()
                                   .stream()
                                   .map(UserBetsSummary::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void getPropBetsSummary() throws Exception {
        String response = "[" +
                "{\"bet_type\":\"Proposal\",\"bet_value\":\"Yes\",\"betters\":[\"jane_doe\",\"john_doe\"],\"is_locked\":true,\"question\":\"Will Kelce propose at the game?\",\"is_winner\":true}," +
                "{\"bet_type\":\"Proposal\",\"bet_value\":\"No\",\"betters\":[\"jack_doe\",\"jill_doe\"],\"is_locked\":true,\"question\":\"Will Kelce propose at the game?\",\"is_winner\":false}," +
                "{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\",\"betters\":[\"jack_doe\",\"jill_doe\"],\"is_locked\":true,\"question\":\"Who wins the coin toss?\",\"is_winner\":true}," +
                "{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Eagles\",\"betters\":[\"jack_doe\",\"jill_doe\"],\"is_locked\":true,\"question\":\"Who wins the coin toss?\",\"is_winner\":false}" +
                "]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString())).thenReturn(response);

        String result = dataService.getPropBetsSummary()
                                   .stream()
                                   .map(PropBetsSummary::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void getScoreBetsSummary() throws Exception {
        String response = "[" +
                "{\"bet_value\":\"0,1\",\"betters\":[\"jane_doe\",\"john_doe\"],\"is_locked\":true,\"count\":1}," +
                "{\"bet_value\":\"0,0\",\"betters\":[\"jack_doe\",\"jill_doe\"],\"is_locked\":true}" +
                "]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString())).thenReturn(response);

        List<ScoreBetsSummary> resultList = dataService.getScoreBetsSummary();

        String result = resultList.stream()
                                  .map(entry -> entry.betValue() + ":\n" + entry.betters() + ":\n" + entry.count())
                                  .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void getUserBets() throws Exception {
        String response = "[{\"username\":\"john_doe\",\"bet_type\":\"Team 1 Score\",\"bet_value\":\"100\",\"is_locked\":true},{\"username\":\"jane_doe\",\"bet_type\":\"Team 2 Score\",\"bet_value\":\"75\",\"is_locked\":false}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.anyString())).thenReturn(response);

        String result = dataService.getUserBets()
                                   .stream()
                                   .map(UserBet::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void getPropBets() throws Exception {
        String response = "[{\"name\":\"Super Bowl MVP\",\"question\":\"Who will be the Super Bowl MVP?\",\"choices\":[\"Tom Brady\",\"Patrick Mahomes\",\"Aaron Rodgers\",\"Josh Allen\"],\"is_locked\":true}, {\"name\":\"Coin Toss\",\"question\":\"Who wins the coin toss?\",\"choices\":[\"Chiefs\",\"Eagles\"]}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString())).thenReturn(response);

        String result = dataService.getPropBets()
                                   .stream()
                                   .map(PropBet::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void findUserBetsByUsername() throws Exception {
        String username = "john_doe";
        String response = "[{\"username\":\"john_doe\",\"bet_type\":\"Team 1 Score\",\"bet_value\":\"100\",\"is_locked\":true}, {\"username\":\"john_doe\",\"bet_type\":\"Team 2 Score\",\"bet_value\":\"50\",\"is_locked\":true}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.eq("username=eq." + username))).thenReturn(response);

        String result = dataService.findUserBetsByUsername(username)
                                   .stream()
                                   .map(UserBet::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void findResults() throws Exception {
        String response = "[{\"bet_type\":\"Team 1 Score\",\"winning_bet_value\":\"100\"}, {\"bet_type\":\"Coin toss\",\"winning_bet_value\":\"Chiefs\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_RESULTS), Mockito.anyString())).thenReturn(response);

        String result = dataService.findResults()
                                   .stream()
                                   .map(Result::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");

        verify(result);
    }

    @Test
    void isPropBetNameTaken_whenPropBetNameDoesNotExist_returnFalse() throws Exception {
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString())).thenReturn("[]");

        verify(String.valueOf(dataService.isPropBetNameTaken("Super Bowl MVP")));
    }

    @Test
    void isPropBetNameTaken_whenPropBetNameExists_returnTrue() throws Exception {
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn("[{\"name\":\"Super Bowl MVP\"}]");

        verify(String.valueOf(dataService.isPropBetNameTaken("Super Bowl MVP")));
    }

    @Test
    void deletePreviousBets() throws Exception {
        String username = "john_doe";
        String expectedDeleteQuery = "username=eq." + username + "&or=(is_locked.eq.false,is_locked.is.null)";
        String expectedGetQuery = "betters.cs.%5B%22" + username + "%22%5D&or=(is_locked.eq.false,is_locked.is.null)";

        Mockito.when(mockSupabaseService.delete(Mockito.eq(TABLE_USER_BETS), Mockito.eq(expectedDeleteQuery))).thenReturn(null);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.eq(expectedGetQuery))).thenReturn("[]");

        dataService.deletePreviousBets(username);

        Mockito.verify(mockSupabaseService, Mockito.times(1)).delete(Mockito.eq(TABLE_USER_BETS), Mockito.eq(expectedDeleteQuery));
        Mockito.verify(mockSupabaseService, Mockito.times(1)).get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.eq(expectedGetQuery));
    }

    @Test
    void addUserBet() throws Exception {
        UserBet userBet = new UserBet("john_doe", "Team 1 Score", "100");

        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_USER_BETS), Mockito.anyString())).thenReturn(null);

        dataService.addUserBet(userBet);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(1)).post(Mockito.eq(TABLE_USER_BETS), captor.capture());
    }

    @Test
    void createNewPropBet() throws Exception {
        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString())).thenReturn(null);

        dataService.createNewPropBet("Super Bowl MVP", "Who will be the Super Bowl MVP?",
                                     "Tom Brady, Patrick Mahomes, Aaron Rodgers, Josh Allen");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(1)).post(Mockito.eq(TABLE_PROP_BETS), captor.capture());
    }

    @Test
    void updatePropBetsSummary_whenRecordExists_updatesExistingBetters() throws Exception {
        String betType = "Coin Toss";
        String betValue = "Chiefs";
        String username = "new_user";

        String existingResponse = "[{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\",\"betters\":[\"existing_user\",\"another_user\"],\"is_locked\":true,\"question\":\"Who wins the coin toss?\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn(existingResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.updatePropBetsSummary(betType, betValue, username);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), queryCaptor.capture(), bodyCaptor.capture());

        verify("Query: " + queryCaptor.getValue() + "\nBody: " + bodyCaptor.getValue());
    }

    @Test
    void updatePropBetsSummary_whenNoRecordExists_createsNewRecord() throws Exception {
        String betType = "New Bet";
        String betValue = "Option A";
        String username = "test_user";

        String emptyResponse = "[]";
        String propBetResponse = "[{\"bet_type\":\"New Bet\",\"question\":\"What is the new bet about?\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn(emptyResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn(propBetResponse);
        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn(null);

        dataService.updatePropBetsSummary(betType, betValue, username);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_PROP_BETS_SUMMARY), bodyCaptor.capture());

        verify("New record body: " + bodyCaptor.getValue());
    }

    @Test
    void updateScoreBetsSummary_whenRecordExists_updatesExistingBetters() throws Exception {
        String betValue = "1,2";
        String username = "new_user";

        String existingResponse = "[{\"bet_value\":\"1,2\",\"betters\":[\"existing_user\",\"another_user\"],\"is_locked\":true,\"count\":\"0\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn(existingResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.updateScoreBetsSummary(betValue, username);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .patch(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), queryCaptor.capture(), bodyCaptor.capture());

        verify("Query: " + queryCaptor.getValue() + "\nBody: " + bodyCaptor.getValue());
    }

    @Test
    void updateScoreBetsSummary_whenNoRecordExists_createsNewRecord() throws Exception {
        String betValue = "1,2";
        String username = "test_user";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn("[]");

        dataService.updateScoreBetsSummary(betValue, username);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), bodyCaptor.capture());

        verify("New record body: " + bodyCaptor.getValue());
    }

    @Test
    void updateUserBetsSummary_whenUserExists_updatesExistingUser() throws Exception {
        String username = "john_doe";
        Integer amountPerBet = 5;

        String existingUserResponse = "[{\"username\":\"john_doe\",\"number_of_bets_made\":3,\"amount_owing\":15.0,\"number_of_bets_won\":1,\"amount_won\":25.0,\"net_amount\":10.0}]";
        String userBetsResponse = "[{\"username\":\"john_doe\",\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\"},{\"username\":\"john_doe\",\"bet_type\":\"MVP\",\"bet_value\":\"Mahomes\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.john_doe")))
               .thenReturn(existingUserResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.eq("username=eq.john_doe")))
               .thenReturn(userBetsResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.updateUserBetsSummary(username, amountPerBet);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), queryCaptor.capture(), bodyCaptor.capture());

        verify("Existing User Update - Query: " + queryCaptor.getValue() + "\nBody: " + bodyCaptor.getValue());
    }

    @Test
    void updateUserBetsSummary_whenUserDoesNotExist_createsNewUser() throws Exception {
        String username = "new_user";
        Integer amountPerBet = 5;

        String emptyUserResponse = "[]";
        String userBetsResponse = "[{\"username\":\"new_user\",\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\"},{\"username\":\"new_user\",\"bet_type\":\"MVP\",\"bet_value\":\"Mahomes\"},{\"username\":\"new_user\",\"bet_type\":\"Score\",\"bet_value\":\"1,2\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.new_user")))
               .thenReturn(emptyUserResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.eq("username=eq.new_user")))
               .thenReturn(userBetsResponse);
        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn(null);

        dataService.updateUserBetsSummary(username, amountPerBet);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_USER_BETS_SUMMARY), bodyCaptor.capture());

        verify("New User Creation - Body: " + bodyCaptor.getValue());
    }

    @Test
    void saveScoreBets_withEmptyBets_doesNotCallAnyMethods() throws Exception {
        String username = "john_doe";
        List<String> emptyBets = new ArrayList<>();

        dataService.saveScoreBets(username, emptyBets);

        Mockito.verify(mockSupabaseService, Mockito.never()).post(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never()).get(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never()).patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void saveScoreBets_withSingleBet_callsCorrectMethods() throws Exception {
        String username = "john_doe";
        List<String> bets = List.of("1,2");

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn("[]");

        dataService.saveScoreBets(username, bets);

        ArgumentCaptor<String> userBetCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_USER_BETS), userBetCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString());

        verify("User bet posted: " + userBetCaptor.getValue());
    }

    @Test
    void saveScoreBets_withMultipleBets_callsMethodsForEachBet() throws Exception {
        String username = "jane_doe";
        List<String> bets = List.of("0,1", "2,3", "4,5");

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn("[]");

        dataService.saveScoreBets(username, bets);

        Mockito.verify(mockSupabaseService, Mockito.times(3))
               .post(Mockito.eq(TABLE_USER_BETS), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(3))
               .post(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString());

        verify("Number of bets processed: " + bets.size());
    }

    @Test
    void savePropBets_withEmptyBets_doesNotCallAnyMethods() throws Exception {
        String username = "john_doe";
        Map<String, String> emptyBets = new HashMap<>();

        dataService.savePropBets(username, emptyBets);

        Mockito.verify(mockSupabaseService, Mockito.never()).post(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never()).get(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never()).patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void savePropBets_withSingleBet_callsCorrectMethods() throws Exception {
        String username = "john_doe";
        Map<String, String> bets = Map.of("Coin Toss", "Chiefs");

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn("[]");
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn("[{\"bet_type\":\"Coin Toss\",\"question\":\"Who wins the coin toss?\"}]");

        dataService.savePropBets(username, bets);

        ArgumentCaptor<String> userBetCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> propBetsSummaryCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_USER_BETS), userBetCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_PROP_BETS_SUMMARY), propBetsSummaryCaptor.capture());

        verify("User bet posted: " + userBetCaptor.getValue() + "\nProp bets summary posted: " + propBetsSummaryCaptor.getValue());
    }

    @Test
    void savePropBets_withMultipleBets_callsMethodsForEachBet() throws Exception {
        String username = "jane_doe";
        Map<String, String> bets = Map.of(
            "Coin Toss", "Chiefs",
            "MVP", "Mahomes",
            "First Touchdown", "Kelce"
        );

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn("[]");
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn("[{\"bet_type\":\"Coin Toss\",\"question\":\"Who wins the coin toss?\"}]")
               .thenReturn("[{\"bet_type\":\"MVP\",\"question\":\"Who will be MVP?\"}]")
               .thenReturn("[{\"bet_type\":\"First Touchdown\",\"question\":\"Who scores first?\"}]");

        dataService.savePropBets(username, bets);

        Mockito.verify(mockSupabaseService, Mockito.times(3))
               .post(Mockito.eq(TABLE_USER_BETS), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(3))
               .post(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString());

        verify("Number of prop bets processed: " + bets.size());
    }

    @Test
    void savePropBets_withExistingPropBetsSummary_updatesExistingRecord() throws Exception {
        String username = "new_user";
        Map<String, String> bets = Map.of("Coin Toss", "Chiefs");

        String existingResponse = "[{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\",\"betters\":[\"existing_user\"],\"question\":\"Who wins the coin toss?\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
               .thenReturn(existingResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.savePropBets(username, bets);

        ArgumentCaptor<String> userBetCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_USER_BETS), userBetCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), queryCaptor.capture(), bodyCaptor.capture());

        verify("User bet: " + userBetCaptor.getValue() + "\nQuery: " + queryCaptor.getValue() + "\nUpdate body: " + bodyCaptor.getValue());
    }

    @Test
    void saveResult_withWinningBetters_savesResultAndUpdatesWinners() throws Exception {
        String betType = "Coin Toss";
        String winningBetValue = "Chiefs";
        String propBetResponse = "[{\"bet_type\":\"Coin Toss\",\"question\":\"Who wins the coin toss?\",\"choices\":[\"Chiefs\",\"Eagles\"]}]";
        String winningSummaryResponse = "[{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\",\"betters\":[\"user1\",\"user2\"],\"question\":\"Who wins the coin toss?\"}]";
        String losingSummaryResponse = "[{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Eagles\",\"betters\":[\"user3\",\"user4\"],\"question\":\"Who wins the coin toss?\"}]";
        String user1Summary = "[{\"username\":\"user1\",\"amount_owing\":15.0,\"number_of_propbets_won\":0,\"amount_of_propbets_won\":0.0,\"number_of_scoreboard_bets_won\":1,\"amount_of_scoreboard_bets_won\":10.0}]";
        String user2Summary = "[{\"username\":\"user2\",\"amount_owing\":10.0,\"number_of_propbets_won\":1,\"amount_of_propbets_won\":5.0,\"number_of_scoreboard_bets_won\":0,\"amount_of_scoreboard_bets_won\":0.0}]";

        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_RESULTS), Mockito.anyString()))
               .thenReturn(null);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn(propBetResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.contains("bet_value=eq.Chiefs")))
               .thenReturn(winningSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.contains("bet_value=eq.Eagles")))
               .thenReturn(losingSummaryResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.user1")))
               .thenReturn(user1Summary);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.user2")))
               .thenReturn(user2Summary);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.saveResult(betType, winningBetValue);

        ArgumentCaptor<String> resultCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_RESULTS), resultCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(2))
               .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(2))
               .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString());

        verify("Result saved: " + resultCaptor.getValue());
    }

    @Test
    void saveResult_withNoWinningBetters_updatesOnlyPropBetsSummary() throws Exception {
        String betType = "MVP";
        String winningBetValue = "Mahomes";
        String propBetResponse = "[{\"bet_type\":\"MVP\",\"question\":\"Who will be MVP?\",\"choices\":[\"Mahomes\",\"Brady\"]}]";
        String losingSummaryResponse = "[{\"bet_type\":\"MVP\",\"bet_value\":\"Brady\",\"betters\":[\"user1\"],\"question\":\"Who will be MVP?\"}]";

        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_RESULTS), Mockito.anyString()))
               .thenReturn(null);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn(propBetResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.contains("bet_value=eq.Mahomes")))
               .thenReturn("[]");
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.contains("bet_value=eq.Brady")))
               .thenReturn(losingSummaryResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.saveResult(betType, winningBetValue);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .post(Mockito.eq(TABLE_RESULTS), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never())
               .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void saveResult_calculatesCorrectAmountWonPerBetter() throws Exception {
        String betType = "First Score";
        String winningBetValue = "Touchdown";
        String propBetResponse = "[{\"bet_type\":\"First Score\",\"question\":\"How will first points be scored?\",\"choices\":[\"Touchdown\",\"Field Goal\",\"Safety\"]}]";
        String winningSummaryResponse = "[{\"bet_type\":\"First Score\",\"bet_value\":\"Touchdown\",\"betters\":[\"winner1\",\"winner2\"]}]";
        String losing1SummaryResponse = "[{\"bet_type\":\"First Score\",\"bet_value\":\"Field Goal\",\"betters\":[\"loser1\",\"loser2\",\"loser3\"]}]";
        String losing2SummaryResponse = "[{\"bet_type\":\"First Score\",\"bet_value\":\"Safety\",\"betters\":[\"loser4\"]}]";
        String winner1Summary = "[{\"username\":\"winner1\",\"amount_owing\":20.0,\"number_of_propbets_won\":0,\"amount_of_propbets_won\":0.0,\"number_of_scoreboard_bets_won\":0,\"amount_of_scoreboard_bets_won\":0.0}]";
        String winner2Summary = "[{\"username\":\"winner2\",\"amount_owing\":15.0,\"number_of_propbets_won\":1,\"amount_of_propbets_won\":10.0,\"number_of_scoreboard_bets_won\":2,\"amount_of_scoreboard_bets_won\":25.0}]";

        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_RESULTS), Mockito.anyString()))
               .thenReturn(null);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn(propBetResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.argThat(query -> query.contains("bet_type=eq.First+Score") && query.contains("bet_value=eq.Touchdown"))))
               .thenReturn(winningSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.argThat(query -> query.contains("bet_type=eq.First+Score") && query.contains("bet_value=eq.Field+Goal"))))
               .thenReturn(losing1SummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.argThat(query -> query.contains("bet_type=eq.First+Score") && query.contains("bet_value=eq.Safety"))))
               .thenReturn(losing2SummaryResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.winner1")))
               .thenReturn(winner1Summary);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.winner2")))
               .thenReturn(winner2Summary);
        Mockito.when(mockSupabaseService.patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString()))
               .thenReturn(null);

        dataService.saveResult(betType, winningBetValue);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(2))
               .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), bodyCaptor.capture());

        List<String> capturedBodies = bodyCaptor.getAllValues();
        verify("Winner updates:\nFirst winner: " + capturedBodies.get(0) + "\nSecond winner: " + capturedBodies.get(1));
    }

    @Test
    void saveScore_whenScoreBetsSummaryExists_savesResultsAndUpdatesAllTables() throws Exception {
        String team1Name = "Chiefs";
        String team1Score = "21";
        String team2Name = "Eagles";
        String team2Score = "14";
        String scoreBetsSummaryResponse = "[{\"bet_value\":\"1,4\",\"betters\":[\"john_doe\",\"jane_doe\"],\"count\":2}]";
        String scoreboardEventsTrackerResponse = "[{\"id\":1,\"totalAmountOfBets\":100.0,\"numberOfWinningEvents\":2,\"totalAmountWonPerEvent\":25.0}]";
        String winningScoreEventsResponse = "[{\"betters\":[\"john_doe\",\"jane_doe\"],\"count\":2}]";
        String johnUserBetsSummaryResponse = "[{\"username\":\"john_doe\",\"amount_owing\":50.0,\"numberOfPropBetsWon\":1,\"amountOfPropBetsWon\":10.0}]";
        String janeUserBetsSummaryResponse = "[{\"username\":\"jane_doe\",\"amount_owing\":40.0,\"numberOfPropBetsWon\":2,\"amountOfPropBetsWon\":15.0}]";

        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&bet_value=eq.1%2C4")
        )).thenReturn(scoreBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.eq("")))
                .thenReturn(scoreboardEventsTrackerResponse);
        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&count=not.is.null")
        )).thenReturn(winningScoreEventsResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.john_doe")))
                .thenReturn(johnUserBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.jane_doe")))
                .thenReturn(janeUserBetsSummaryResponse);
        Mockito.when(mockSupabaseService.post(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(mockSupabaseService.patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<String> scoreCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> trackerUpdateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> propBetsUpdateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userBetsUpdateCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .post(Mockito.eq(TABLE_SCORE), scoreCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .patch(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.eq("id=eq.1"), trackerUpdateCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.eq("bet_type=eq.Score&bet_value=eq.1%2C4"), propBetsUpdateCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(2))
                .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), userBetsUpdateCaptor.capture());

        verify("Score saved: " + scoreCaptor.getValue() + "\n" +
               "Tracker update: " + trackerUpdateCaptor.getValue() + "\n" +
               "PropBets update: " + propBetsUpdateCaptor.getValue() + "\n" +
               "UserBets updates: " + String.join(", ", userBetsUpdateCaptor.getAllValues()));
    }

    @Test
    void saveScore_whenNoScoreBetsSummaryExists_onlySavesResultsToTable() throws Exception {
        String team1Name = "Chiefs";
        String team1Score = "28";
        String team2Name = "Eagles";
        String team2Score = "21";

        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&bet_value=eq.8%2C1")
        )).thenReturn("[]");
        Mockito.when(mockSupabaseService.post(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<String> scoreCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .post(Mockito.eq(TABLE_SCORE), scoreCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.never())
                .patch(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never())
                .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.never())
                .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), Mockito.anyString());

        verify("Score saved: " + scoreCaptor.getValue());
    }

    @Test
    void saveScore_whenScoreboardEventsTrackerDoesNotExist_createsNewScoreboardEventsTrackerAndUpdatesAllTables() throws Exception {
        String team1Name = "Chiefs";
        String team1Score = "17";
        String team2Name = "Eagles";
        String team2Score = "10";
        String scoreBetsSummaryResponse = "[{\"bet_value\":\"7,0\",\"betters\":[\"user1\",\"user2\"],\"count\":1}]";
        String winningScoreEventsResponse = "[{\"betters\":[\"user1\",\"user2\"],\"count\":1}]";
        String user1BetsSummaryResponse = "[{\"username\":\"user1\",\"amount_owing\":25.0,\"numberOfPropBetsWon\":0,\"amountOfPropBetsWon\":0.0}]";
        String user2BetsSummaryResponse = "[{\"username\":\"user2\",\"amount_owing\":30.0,\"numberOfPropBetsWon\":1,\"amountOfPropBetsWon\":5.0}]";

        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&bet_value=eq.7%2C0")
        )).thenReturn(scoreBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.eq("")))
                .thenReturn("[]");
        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&count=not.is.null")
        )).thenReturn(winningScoreEventsResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.user1")))
                .thenReturn(user1BetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("username=eq.user2")))
                .thenReturn(user2BetsSummaryResponse);
        Mockito.when(mockSupabaseService.post(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(mockSupabaseService.patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<String> scoreCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> propBetsUpdateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userBetsUpdateCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .post(Mockito.eq(TABLE_SCORE), scoreCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .patch(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.eq("bet_type=eq.Score&bet_value=eq.7%2C0"), propBetsUpdateCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.times(2))
                .patch(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString(), userBetsUpdateCaptor.capture());
        Mockito.verify(mockSupabaseService, Mockito.never())
                .patch(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.anyString(), Mockito.anyString());

        verify("Score saved: " + scoreCaptor.getValue() + "\n" +
               "PropBets count update: " + propBetsUpdateCaptor.getValue() + "\n" +
               "User bets updates (with 0.0 amounts due to missing tracker): " + String.join(", ", userBetsUpdateCaptor.getAllValues()));
    }

    @Test
    void saveScore_withSingleDigitScores_extractsCorrectBetValue() throws Exception {
        String team1Name = "Chiefs";
        String team1Score = "3";
        String team2Name = "Eagles";
        String team2Score = "7";
        String scoreBetsSummaryResponse = "[{\"bet_value\":\"3,7\",\"betters\":[\"test_user\"],\"count\":1}]";

        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&bet_value=eq.3%2C7")
        )).thenReturn(scoreBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.eq("")))
                .thenReturn("[]");
        Mockito.when(mockSupabaseService.get(
                Mockito.eq(TABLE_PROP_BETS_SUMMARY),
                Mockito.eq("bet_type=eq.Score&count=not.is.null")
        )).thenReturn(scoreBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString()))
                .thenReturn("[{\"username\":\"test_user\",\"amount_owing\":5.0,\"numberOfPropBetsWon\":0,\"amountOfPropBetsWon\":0.0}]");
        Mockito.when(mockSupabaseService.post(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(mockSupabaseService.patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        Mockito.verify(mockSupabaseService, Mockito.times(2))
                .get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.eq("bet_type=eq.Score&bet_value=eq.3%2C7"));
    }

    @Test
    void saveScore_withMultipleDigitScores_extractsLastDigitCorrectly() throws Exception {
        String team1Name = "Team1";
        String team1Score = "142";
        String team2Name = "Team2";
        String team2Score = "035";

        Mockito.when(mockSupabaseService.get(Mockito.anyString(), Mockito.anyString())).thenReturn("[]");
        Mockito.when(mockSupabaseService.post(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        dataService.saveScore(team1Name, team1Score, team2Name, team2Score);

        ArgumentCaptor<String> scoreCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .post(Mockito.eq(TABLE_SCORE), scoreCaptor.capture());

        verify("Score document: " + scoreCaptor.getValue());
    }

    @Test
    void saveScore_whenSupabaseServiceThrowsException_propagatesRuntimeException() throws Exception {
        String team1Name = "Chiefs";
        String team1Score = "21";
        String team2Name = "Eagles";
        String team2Score = "14";

        Mockito.when(mockSupabaseService.post(Mockito.eq(TABLE_SCORE), Mockito.anyString()))
                .thenThrow(new RuntimeException("Database connection failed"));

        try {
            dataService.saveScore(team1Name, team1Score, team2Name, team2Score);
            verify("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            verify("Exception message: " + e.getMessage());
        }
    }

    @Test
    void lockPropBets_locksAllBetsAndSummaries() throws Exception {
        String propBetsResponse = "[" +
                "{\"name\":\"Coin Toss\",\"question\":\"Who wins the coin toss?\",\"choices\":[\"Chiefs\",\"Eagles\"]}," +
                "{\"name\":\"MVP\",\"question\":\"Who will be MVP?\",\"choices\":[\"Mahomes\",\"Brady\"]}" +
                "]";
        String propBetsSummaryResponse = "[" +
                "{\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\",\"betters\":[\"user1\",\"user2\"]}," +
                "{\"bet_type\":\"MVP\",\"bet_value\":\"Mahomes\",\"betters\":[\"user3\"]}" +
                "]";
        String scoreBetsSummaryResponse = "[" +
                "{\"bet_value\":\"1,2\",\"betters\":[\"user1\",\"user4\"]}," +
                "{\"bet_value\":\"3,4\",\"betters\":[\"user2\"]}" +
                "]";
        String userBetsResponse = "[" +
                "{\"username\":\"user1\",\"bet_type\":\"Coin Toss\",\"bet_value\":\"Chiefs\"}," +
                "{\"username\":\"user2\",\"bet_type\":\"MVP\",\"bet_value\":\"Mahomes\"}," +
                "{\"username\":\"user3\",\"bet_type\":\"Score\",\"bet_value\":\"1,2\"}" +
                "]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
                .thenReturn(propBetsResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
                .thenReturn(propBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString()))
                .thenReturn(scoreBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.anyString()))
                .thenReturn(userBetsResponse);
        Mockito.when(mockSupabaseService.patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(null);

        dataService.lockPropBets();

        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString());
        Mockito.verify(mockSupabaseService, Mockito.times(1))
                .get(Mockito.eq(TABLE_USER_BETS), Mockito.anyString());

        ArgumentCaptor<String> tableCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockSupabaseService, Mockito.times(9))
                .patch(tableCaptor.capture(), queryCaptor.capture(), bodyCaptor.capture());

        List<String> tables = tableCaptor.getAllValues();
        List<String> queries = queryCaptor.getAllValues();
        List<String> bodies = bodyCaptor.getAllValues();

        verify("PropBets patches:\n" +
               "Table: " + tables.get(0) + ", Query: " + queries.get(0) + ", Body: " + bodies.get(0) + "\n" +
               "Table: " + tables.get(1) + ", Query: " + queries.get(1) + ", Body: " + bodies.get(1) + "\n" +
               "PropBetsSummary patches:\n" +
               "Table: " + tables.get(2) + ", Query: " + queries.get(2) + ", Body: " + bodies.get(2) + "\n" +
               "Table: " + tables.get(3) + ", Query: " + queries.get(3) + ", Body: " + bodies.get(3) + "\n" +
               "ScoreBetsSummary patches:\n" +
               "Table: " + tables.get(4) + ", Query: " + queries.get(4) + ", Body: " + bodies.get(4) + "\n" +
               "Table: " + tables.get(5) + ", Query: " + queries.get(5) + ", Body: " + bodies.get(5) + "\n" +
               "UserBets patches:\n" +
               "Table: " + tables.get(6) + ", Query: " + queries.get(6) + ", Body: " + bodies.get(6) + "\n" +
               "Table: " + tables.get(7) + ", Query: " + queries.get(7) + ", Body: " + bodies.get(7) + "\n" +
               "Table: " + tables.get(8) + ", Query: " + queries.get(8) + ", Body: " + bodies.get(8));
    }

    @Test
    void lockPropBets_withSpecialCharactersInBetNames_encodesQueriesCorrectly() throws Exception {
        String propBetsResponse = "[{\"name\":\"Team A vs Team B\",\"question\":\"Who wins?\",\"choices\":[\"Team A\",\"Team B\"]}]";
        String propBetsSummaryResponse = "[{\"bet_type\":\"Team A vs Team B\",\"bet_value\":\"Team A\",\"betters\":[\"user1\"]}]";
        String userBetsResponse = "[{\"username\":\"user@test.com\",\"bet_type\":\"Team A vs Team B\",\"bet_value\":\"Team A\"}]";

        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
                .thenReturn(propBetsResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString()))
                .thenReturn(propBetsSummaryResponse);
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.anyString()))
                .thenReturn("[]");
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.anyString()))
                .thenReturn(userBetsResponse);

        Mockito.when(mockSupabaseService.patch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(null);

        dataService.lockPropBets();

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockSupabaseService, Mockito.times(3))
                .patch(Mockito.anyString(), queryCaptor.capture(), Mockito.anyString());

        List<String> queries = queryCaptor.getAllValues();
        verify("Encoded queries:\n" +
               "PropBet query: " + queries.get(0) + "\n" +
               "PropBetsSummary query: " + queries.get(1) + "\n" +
               "UserBet query: " + queries.get(2));
    }

    @Test
    void deleteAllData_deletesFromAllTables() throws Exception {
        Mockito.when(mockSupabaseService.delete(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        dataService.deleteAllData();

        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_SCORE_BETS_SUMMARY), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_USER_BETS), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_PROP_BETS), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_RESULTS), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_SCORE), Mockito.eq("id=gte.0"));
        Mockito.verify(mockSupabaseService, Mockito.times(1))
               .delete(Mockito.eq(TABLE_SCOREBOARD_EVENTS_TRACKER), Mockito.eq("id=gte.0"));

        Mockito.verify(mockSupabaseService, Mockito.times(8))
               .delete(Mockito.anyString(), Mockito.eq("id=gte.0"));
    }

    @Test
    void deleteAllData_whenSupabaseServiceThrowsException_propagatesRuntimeException() throws Exception {
        String expectedExceptionMessage = "Database connection failed";

        Mockito.when(mockSupabaseService.delete(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString()))
               .thenThrow(new Exception(expectedExceptionMessage));

        try {
            dataService.deleteAllData();
            verify("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            verify("Exception caught - Message: " + e.getMessage() +
                   "\nExpected to contain: Failed to delete all data from Supabase");
        }
    }
}
