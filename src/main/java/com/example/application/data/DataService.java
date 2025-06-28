package com.example.application.data;

import com.example.application.supabase.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {
    private static final String USERNAME = "username";
    private static final String BET_TYPE = "betType";
    private static final String BET_VALUE = "betValue";
    private static final String BETTERS = "betters";
    private static final String NUMBER_OF_BETS_MADE = "numberOfBetsMade";
    private static final String AMOUNT_OWING = "amountOwing";
    private static final String NUMBER_OF_BETS_WON = "numberOfBetsWon";
    private static final String AMOUNT_WON = "amountWon";
    private static final String NET_AMOUNT = "netAmount";
    private static final String NAME = "name";
    private static final String QUESTION = "question";
    private static final String CHOICES = "choices";
    private static final String WINNING_BET_VALUE = "winningBetValue";
    private static final String IS_WINNER = "isWinner";
    private static final String IS_LOCKED = "isLocked";

    private final SupabaseService supabaseService;

    @Autowired
    public DataService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public List<UserBetsSummary> getUserBetsSummary() {
        List<UserBetsSummary> userBetsSummaries = new ArrayList<>();
        try {
            String response = supabaseService.get("UserBetsSummary", "");
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                UserBetsSummary userBetsSummary = new UserBetsSummary(
                    obj.optString(USERNAME),
                    obj.optInt(NUMBER_OF_BETS_MADE),
                    obj.optDouble(AMOUNT_OWING),
                    obj.optInt(NUMBER_OF_BETS_WON),
                    obj.optDouble(AMOUNT_WON),
                    obj.optDouble(NET_AMOUNT)
                );
                userBetsSummaries.add(userBetsSummary);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user bets summary from Supabase", e);
        }
        return userBetsSummaries;
    }

    public List<PropBetsSummary> getPropBetsSummary() {
        List<PropBetsSummary> propBetsSummaries = new ArrayList<>();
        try {
            String response = supabaseService.get("PropBetsSummary", "");
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                String betType = obj.optString(BET_TYPE);
                if (!betType.equals("Score")) {
                    PropBetsSummary summary = new PropBetsSummary(
                        betType,
                        obj.optString(BET_VALUE),
                        toStringList(obj.optJSONArray(BETTERS)),
                        obj.optString(QUESTION),
                        obj.optBoolean(IS_WINNER)
                    );
                    propBetsSummaries.add(summary);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch prop bets summary from Supabase", e);
        }
        return propBetsSummaries;
    }

    public List<ScoreBoardBetsSummary> getScoreBoardBetsSummary() {
        List<ScoreBoardBetsSummary> scoreBoardBetsSummaries = new ArrayList<>();
        try {
            String response = supabaseService.get("PropBetsSummary", "");
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                String betType = obj.optString(BET_TYPE);
                if (betType.equals("Score")) {
                    String betValue = obj.optString(BET_VALUE);
                    List<String> betters = toStringList(obj.optJSONArray(BETTERS));
                    Optional<Integer> count = obj.has("count") ? Optional.of(obj.optInt("count")) : Optional.empty();
                    ScoreBoardBetsSummary summary = new ScoreBoardBetsSummary(betValue, betters, count);
                    scoreBoardBetsSummaries.add(summary);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch scoreboard bets summary from Supabase", e);
        }
        return scoreBoardBetsSummaries;
    }

    public List<UserBet> getUserBets() {
        List<UserBet> userBets = new ArrayList<>();
        try {
            String response = supabaseService.get("UserBets", "");
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                UserBet userBet = new UserBet(
                    obj.optString(USERNAME),
                    obj.optString(BET_TYPE),
                    obj.optString(BET_VALUE)
                );
                userBets.add(userBet);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user bets from Supabase", e);
        }
        return userBets;
    }

    public List<PropBet> getPropBets() {
        List<PropBet> propBets = new ArrayList<>();
        try {
            String response = supabaseService.get("PropBets", "");
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                PropBet propBet = new PropBet(
                    obj.optString(NAME),
                    obj.optString(QUESTION),
                    toStringList(obj.optJSONArray(CHOICES)),
                    obj.has(IS_LOCKED) ? Optional.of(obj.optBoolean(IS_LOCKED)) : Optional.empty()
                );
                propBets.add(propBet);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch prop bets from Supabase", e);
        }
        return propBets;
    }

    public List<UserBet> findUserBetsByUsername(String username) {
        List<UserBet> userBets = new ArrayList<>();
        try {
            String response = supabaseService.get("UserBets", "username=eq." + username);
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                UserBet userBet = new UserBet(
                    obj.optString(USERNAME),
                    obj.optString(BET_TYPE),
                    obj.optString(BET_VALUE)
                );
                userBets.add(userBet);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user bets by username from Supabase", e);
        }
        return userBets;
    }

    public List<Result> findResults() {
        List<Result> results = new ArrayList<>();
        try {
            String response = supabaseService.get("Results", "");
            org.json.JSONArray arr = new org.json.JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                Result result = new Result(
                    obj.optString(BET_TYPE),
                    obj.optString(WINNING_BET_VALUE)
                );
                results.add(result);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch results from Supabase", e);
        }
        return results;
    }

    private List<String> toStringList(org.json.JSONArray arr) {
        List<String> list = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.optString(i));
            }
        }
        return list;
    }

    public boolean isPropBetNameTaken(String name) {
        try {
            String response = supabaseService.get("PropBets", "name=eq." + toCamelCase(name));
            org.json.JSONArray arr = new org.json.JSONArray(response);
            return arr.length() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check prop bet name in Supabase", e);
        }
    }

    public void deletePreviousBets(String username) {
        try {
            supabaseService.delete("UserBets", "username=eq." + username);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete previous bets in Supabase", e);
        }
    }

    public void addUserBet(UserBet userBet) {
        try {
            org.json.JSONObject obj = new org.json.JSONObject();
            obj.put(USERNAME, userBet.username());
            obj.put(BET_TYPE, userBet.betType());
            obj.put(BET_VALUE, userBet.betValue());
            supabaseService.post("UserBets", obj.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add user bet to Supabase", e);
        }
    }

    public void createNewPropBet(String name, String question, String choices) {
        try {
            List<String> choicesList = Stream.of(choices.split(","))
                                             .map(String::trim)
                                             .map(this::toCamelCase)
                                             .toList();
            org.json.JSONObject obj = new org.json.JSONObject();
            obj.put(NAME, toCamelCase(name));
            obj.put(QUESTION, formatQuestion(question));
            obj.put(CHOICES, choicesList);
            supabaseService.post("PropBets", obj.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new prop bet in Supabase", e);
        }
    }

    private String toCamelCase(String input) {
        return Stream.of(input.split("\\s+")).filter(s -> !s.isEmpty()).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining(" ")).trim();
    }

    private String formatQuestion(String input) {
        String string = input.substring(0, 1).toUpperCase() + input.substring(1);
        return string.endsWith("?") ? string : string + "?";
    }

    public void updatePropBetsSummary(String betType, String betValue, String username) {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void updateUserBetsSummary(String username, Integer amountPerBet) {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void saveScoreBoardBets(String username, java.util.Map<String, String> bets) {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void savePropBets(String username, java.util.Map<String, String> bets) {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void saveResult(String betType, String winningBetValue) {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void saveScore(String team1Name, String team1Score, String team2Name, String team2Score) {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void lockPropBets() {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }

    public void deleteAllData() {
        throw new UnsupportedOperationException("Not yet implemented for Supabase");
    }
}
