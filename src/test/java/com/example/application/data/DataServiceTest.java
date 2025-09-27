package com.example.application.data;

import com.example.application.supabase.SupabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.example.application.data.DataService.TABLE_PROP_BETS;
import static com.example.application.data.DataService.TABLE_PROP_BETS_SUMMARY;
import static com.example.application.data.DataService.TABLE_RESULTS;
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

        List<ScoreBoardBetsSummary> resultList = dataService.getScoreBetsSummary();

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
}
