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
        String response = "[{\"username\":\"john_doe\",\"numberOfBetsMade\":5,\"amountOwing\":100.0,\"numberOfBetsWon\":3,\"amountWon\":150.0,\"netAmount\":50.0}]";
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS_SUMMARY), Mockito.anyString())).thenReturn(response);

        String result = dataService.getUserBetsSummary()
                                   .stream()
                                   .map(UserBetsSummary::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");
        verify(result);
    }

    @Test
    void getPropBetsSummary_whenReceivesScoreBetType_returnsPropBetsSummary() throws Exception {
        String response = "[" +
                "{\"betType\":\"Proposal\",\"betValue\":\"Yes\",\"betters\":[\"jane_doe\",\"john_doe\"],\"question\":\"Will Kelce propose at the game?\",\"isWinner\":true}," +
                "{\"betType\":\"Proposal\",\"betValue\":\"No\",\"betters\":[\"jack_doe\",\"jill_doe\"],\"question\":\"Will Kelce propose at the game?\",\"isWinner\":false}," +
                "{\"betType\":\"Score\",\"betValue\":\"0,0\",\"betters\":[\"jack_doe\",\"jill_doe\"]}" +
                "]";
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString())).thenReturn(response);

        String result = dataService.getPropBetsSummary()
                                   .stream()
                                   .map(PropBetsSummary::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");
        verify(result);
    }

    @Test
    void getScoreBoardBetsSummary_whenReceivesScoreBetType_returnsScoreBoardBetsSummary() throws Exception {
        String response = "[" +
                "{\"betType\":\"Score\",\"betValue\":\"0,1\",\"betters\":[\"jane_doe\",\"john_doe\"],\"count\":1}," +
                "{\"betType\":\"Score\",\"betValue\":\"0,0\",\"betters\":[\"jack_doe\",\"jill_doe\"]}" +
                "]";
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS_SUMMARY), Mockito.anyString())).thenReturn(response);

        List<ScoreBoardBetsSummary> resultList = dataService.getScoreBoardBetsSummary();

        String result = resultList.stream()
                                  .map(entry -> entry.betValue() + ":\n" + entry.betters() + ":\n" + entry.count())
                                  .reduce("", (s1, s2) -> s1 + s2 + "\n");
        verify(result);
    }

    @Test
    void getUserBets() throws Exception {
        String response = "[{\"username\":\"john_doe\",\"betType\":\"Team 1 Score\",\"betValue\":\"100\"}]";
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.anyString())).thenReturn(response);

        String result = dataService.getUserBets()
                                   .stream()
                                   .map(UserBet::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");
        verify(result);
    }

    @Test
    void getPropBets_whenIsLockedIsEmpty() throws Exception {
        String response = "[{\"name\":\"Super Bowl MVP\",\"question\":\"Who will be the Super Bowl MVP?\",\"choices\":[\"Tom Brady\",\"Patrick Mahomes\",\"Aaron Rodgers\",\"Josh Allen\"]}]";
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString())).thenReturn(response);

        String result = dataService.getPropBets()
                                   .stream()
                                   .map(PropBet::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");
        verify(result);
    }

    @Test
    void getPropBets_whenIsLockedIsTrue() throws Exception {
        String response = "[{\"name\":\"Super Bowl MVP\",\"question\":\"Who will be the Super Bowl MVP?\",\"choices\":[\"Tom Brady\",\"Patrick Mahomes\",\"Aaron Rodgers\",\"Josh Allen\"],\"isLocked\":true}]";
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
        String response = "[{\"username\":\"john_doe\",\"betType\":\"Team 1 Score\",\"betValue\":\"100\"}]";
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_USER_BETS), Mockito.eq("username=eq." + username))).thenReturn(response);

        String result = dataService.findUserBetsByUsername(username)
                                   .stream()
                                   .map(UserBet::toString)
                                   .reduce("", (s1, s2) -> s1 + s2 + "\n");
        verify(result);
    }

    @Test
    void findResults() throws Exception {
        String response = "[{\"betType\":\"Team 1 Score\",\"winningBetValue\":\"100\"}]";
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

        boolean taken = dataService.isPropBetNameTaken("Super Bowl MVP");
        verify(String.valueOf(taken));
    }

    @Test
    void isPropBetNameTaken_whenPropBetNameExists_returnTrue() throws Exception {
        Mockito.when(mockSupabaseService.get(Mockito.eq(TABLE_PROP_BETS), Mockito.anyString()))
               .thenReturn("[{\"name\":\"Super Bowl MVP\"}]");

        boolean taken = dataService.isPropBetNameTaken("Super Bowl MVP");
        verify(String.valueOf(taken));
    }

    @Test
    void deletePreviousBets() throws Exception {
        String username = "john_doe";
        Mockito.when(mockSupabaseService.delete(Mockito.eq(TABLE_USER_BETS), Mockito.eq("username=eq." + username))).thenReturn(null);

        dataService.deletePreviousBets(username);

        Mockito.verify(mockSupabaseService, Mockito.times(1)).delete(Mockito.eq(TABLE_USER_BETS), Mockito.eq("username=eq." + username));
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
}
