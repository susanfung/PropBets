package com.example.application.data;

import com.example.application.supabase.SupabaseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {
    public static final String TABLE_USER_BETS_SUMMARY = "user_bets_summary";
    public static final String TABLE_PROP_BETS_SUMMARY = "propbets_summary";
    public static final String TABLE_SCORE_BETS_SUMMARY = "scorebets_summary";
    public static final String TABLE_USER_BETS = "user_bets";
    public static final String TABLE_PROP_BETS = "propbets";
    public static final String TABLE_RESULTS = "results";

    private static final String USERNAME = "username";
    private static final String BET_TYPE = "bet_type";
    private static final String BET_VALUE = "bet_value";
    private static final String BETTERS = "betters";
    private static final String NUMBER_OF_BETS_MADE = "number_of_bets_made";
    private static final String AMOUNT_OWING = "amount_owing";
    private static final String NUMBER_OF_BETS_WON = "number_of_bets_won";
    private static final String AMOUNT_WON = "amount_won";
    private static final String NET_AMOUNT = "net_amount";
    private static final String QUESTION = "question";
    private static final String CHOICES = "choices";
    private static final String WINNING_BET_VALUE = "winningBetValue";
    private static final String IS_WINNER = "is_winner";
    private static final String IS_LOCKED = "is_locked";
    private static final String COUNT = "count";
    private static final String SCORE_BET_TYPE = "Score";
    private static final String IS_SCOREBOARD_EVENTS_TRACKER = "isScoreBoardEventsTracker";
    private static final String TOTAL_AMOUNT_OF_BETS = "totalAmountOfBets";
    private static final String NUMBER_OF_WINNING_EVENTS = "numberOfWinningEvents";
    private static final String TOTAL_AMOUNT_WON_PER_EVENT = "totalAmountWonPerEvent";
    private static final String NUMBER_OF_PROPBETS_WON = "numberOfPropBetsWon";
    private static final String AMOUNT_OF_PROPBETS_WON = "amountOfPropBetsWon";
    private static final String NUMBER_OF_SCOREBOARD_BETS_WON = "numberOfScoreBoardBetsWon";
    private static final String AMOUNT_OF_SCOREBOARD_BETS_WON = "amountOfScoreBoardBetsWon";
    private static final Double AMOUNT_PER_BET = 5.0;

    private final SupabaseService supabaseService;

    @Autowired
    public DataService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public List<UserBetsSummary> getUserBetsSummary() {
        List<UserBetsSummary> userBetsSummaries = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_USER_BETS_SUMMARY, ""));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

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
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_PROP_BETS_SUMMARY, ""));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                PropBetsSummary summary = new PropBetsSummary(
                        obj.optString(BET_TYPE),
                        obj.optString(BET_VALUE),
                        toStringSet(obj.optJSONArray(BETTERS)),
                        obj.optString(QUESTION),
                        obj.optBoolean(IS_WINNER)
                );

                propBetsSummaries.add(summary);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch prop bets summary from Supabase", e);
        }

        return propBetsSummaries;
    }

    public List<ScoreBoardBetsSummary> getScoreBetsSummary() {
        List<ScoreBoardBetsSummary> scoreBoardBetsSummaries = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_SCORE_BETS_SUMMARY, ""));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String betValue = obj.optString(BET_VALUE);
                Set<String> betters = toStringSet(obj.optJSONArray(BETTERS));
                Optional<Integer> count = obj.has(COUNT) ? Optional.of(obj.optInt(COUNT)) : Optional.empty();
                ScoreBoardBetsSummary summary = new ScoreBoardBetsSummary(betValue, betters, count);

                scoreBoardBetsSummaries.add(summary);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch scoreboard bets summary from Supabase", e);
        }

        return scoreBoardBetsSummaries;
    }

    public List<UserBet> getUserBets() {
        List<UserBet> userBets = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_USER_BETS, ""));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

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
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_PROP_BETS, ""));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                PropBet propBet = new PropBet(
                    obj.optString(BET_TYPE),
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
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_USER_BETS, "username=eq." + username));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

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
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_RESULTS, ""));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

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

    private List<String> toStringList(JSONArray arr) {
        List<String> list = new ArrayList<>();

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.optString(i));
            }
        }

        return list;
    }

    private Set<String> toStringSet(JSONArray arr) {
        Set<String> set = new HashSet<>();

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                set.add(arr.optString(i));
            }
        }

        return set;
    }

    public boolean isPropBetNameTaken(String name) {
        try {
            String encodedName = URLEncoder.encode(toCamelCase(name), StandardCharsets.UTF_8);
            String response = supabaseService.get(TABLE_PROP_BETS, "bet_type=eq." + encodedName);
            JSONArray arr = new JSONArray(response);

            return arr.length() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check prop bet betType in Supabase", e);
        }
    }

    public void deletePreviousBets(String username) {
        try {
            String deleteUserBetsQuery = "username=eq." + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                                       "&or=(is_locked.eq.false,is_locked.is.null)";

            supabaseService.delete(TABLE_USER_BETS, deleteUserBetsQuery);

            String bettersJsonFilter = "[\"" + username + "\"]";
            String getPropBetsQuery = "betters.cs." + URLEncoder.encode(bettersJsonFilter, StandardCharsets.UTF_8) +
                                    "&or=(is_locked.eq.false,is_locked.is.null)";
            JSONArray propBetsSummaries = new JSONArray(supabaseService.get(TABLE_PROP_BETS_SUMMARY, getPropBetsQuery));

            for (int i = 0; i < propBetsSummaries.length(); i++) {
                JSONObject propBetSummary = propBetsSummaries.getJSONObject(i);
                String betType = propBetSummary.optString(BET_TYPE);
                String betValue = propBetSummary.optString(BET_VALUE);

                Set<String> betters = toStringSet(propBetSummary.optJSONArray(BETTERS));
                betters.remove(username);

                String updateQuery = "bet_type=eq." + URLEncoder.encode(betType, StandardCharsets.UTF_8) +
                                   "&bet_value=eq." + URLEncoder.encode(betValue, StandardCharsets.UTF_8) +
                                   "&or=(is_locked.eq.false,is_locked.is.null)";

                if (betters.isEmpty()) {
                    supabaseService.delete(TABLE_PROP_BETS_SUMMARY, updateQuery);
                } else {
                    JSONObject updateObj = new JSONObject();
                    updateObj.put(BETTERS, new JSONArray(betters.stream().sorted().collect(Collectors.toList())));
                    supabaseService.patch(TABLE_PROP_BETS_SUMMARY, updateQuery, updateObj.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete previous bets in Supabase", e);
        }
    }

    private void processPropBetsSummaries(JSONArray propBetsSummaries, String username, boolean checkIsLocked) throws Exception {
        for (int i = 0; i < propBetsSummaries.length(); i++) {
            JSONObject propBetSummary = propBetsSummaries.getJSONObject(i);
            String betType = propBetSummary.optString(BET_TYPE);
            String betValue = propBetSummary.optString(BET_VALUE);

            Set<String> betters = toStringSet(propBetSummary.optJSONArray(BETTERS));
            betters.remove(username);

            String updateQuery = "bet_type=eq." + URLEncoder.encode(betType, StandardCharsets.UTF_8) +
                               "&bet_value=eq." + URLEncoder.encode(betValue, StandardCharsets.UTF_8);

            if (checkIsLocked) {
                updateQuery += "&or=(is_locked.is.null,is_locked.eq.false)";
            }

            if (betters.isEmpty()) {
                supabaseService.delete(TABLE_PROP_BETS_SUMMARY, updateQuery);
            } else {
                JSONObject updateObj = new JSONObject();
                updateObj.put(BETTERS, new JSONArray(betters.stream().sorted().collect(Collectors.toList())));
                supabaseService.patch(TABLE_PROP_BETS_SUMMARY, updateQuery, updateObj.toString());
            }
        }
    }

    public void addUserBet(UserBet userBet) {
        try {
            JSONObject obj = new JSONObject();
            obj.put(USERNAME, userBet.username());
            obj.put(BET_TYPE, userBet.betType());
            obj.put(BET_VALUE, userBet.betValue());

            supabaseService.post(TABLE_USER_BETS, obj.toString());
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

            JSONObject obj = new JSONObject();
            obj.put(BET_TYPE, toCamelCase(name));
            obj.put(QUESTION, formatQuestion(question));
            obj.put(CHOICES, new JSONArray(choicesList));

            supabaseService.post(TABLE_PROP_BETS, obj.toString());
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
        try {
            String query = "bet_type=eq." + URLEncoder.encode(betType, StandardCharsets.UTF_8) +
                          "&bet_value=eq." + URLEncoder.encode(betValue, StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_PROP_BETS_SUMMARY, query));

            if (arr.length() > 0) {
                JSONObject existingRecord = arr.getJSONObject(0);
                Set<String> betters = toStringSet(existingRecord.optJSONArray(BETTERS));
                betters.add(username);

                JSONObject updateObj = new JSONObject();
                updateObj.put(BETTERS, new JSONArray(betters.stream().sorted().collect(Collectors.toList())));

                supabaseService.patch(TABLE_PROP_BETS_SUMMARY, query, updateObj.toString());
            } else {
                JSONObject newRecord = new JSONObject();
                newRecord.put(BET_TYPE, betType);
                newRecord.put(BET_VALUE, betValue);
                newRecord.put(BETTERS, new JSONArray(List.of(username)));

                String propBetQuery = "bet_type=eq." + URLEncoder.encode(betType, StandardCharsets.UTF_8);
                String propBetResponse = supabaseService.get(TABLE_PROP_BETS, propBetQuery);
                JSONArray propBetArr = new JSONArray(propBetResponse);

                if (propBetArr.length() > 0) {
                    JSONObject propBet = propBetArr.getJSONObject(0);
                    String question = propBet.optString(QUESTION);

                    if (!question.isEmpty()) {
                        newRecord.put(QUESTION, question);
                    }
                }

                supabaseService.post(TABLE_PROP_BETS_SUMMARY, newRecord.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update prop bets summary in Supabase", e);
        }
    }

    public void updateUserBetsSummary(String username, Integer amountPerBet) {
        try {
            String query = "username=eq." + URLEncoder.encode(username, StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(supabaseService.get(TABLE_USER_BETS_SUMMARY, query));

            List<UserBet> userBetsList = findUserBetsByUsername(username);
            Integer numberOfBetsMade = userBetsList.size();
            Double amountOwing = Double.valueOf(numberOfBetsMade * amountPerBet);

            if (arr.length() > 0) {
                JSONObject existingUser = arr.getJSONObject(0);
                Double amountWon = existingUser.optDouble(AMOUNT_WON, 0.0);

                JSONObject updateObj = new JSONObject();
                updateObj.put(NUMBER_OF_BETS_MADE, numberOfBetsMade);
                updateObj.put(AMOUNT_OWING, amountOwing);
                updateObj.put(NET_AMOUNT, amountWon - amountOwing);

                supabaseService.patch(TABLE_USER_BETS_SUMMARY, query, updateObj.toString());
            } else {
                JSONObject newUser = new JSONObject();
                newUser.put(USERNAME, username);
                newUser.put(NUMBER_OF_BETS_MADE, numberOfBetsMade);
                newUser.put(AMOUNT_OWING, amountOwing);
                newUser.put(NUMBER_OF_BETS_WON, 0);
                newUser.put(AMOUNT_WON, 0.0);
                newUser.put(NET_AMOUNT, -amountOwing);

                supabaseService.post(TABLE_USER_BETS_SUMMARY, newUser.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user bets summary in Supabase", e);
        }
    }

    public void saveScoreBoardBets(String username, java.util.Map<String, String> bets) {
        bets.forEach((betValue, betType) -> {
            UserBet bet = new UserBet(username, betType, betValue);

            addUserBet(bet);
            updatePropBetsSummary(betType, betValue, username);
        });
    }

    public void savePropBets(String username, java.util.Map<String, String> bets) {
        bets.forEach((betType, betValue) -> {
            UserBet bet = new UserBet(username, betType, betValue);

            addUserBet(bet);
            updatePropBetsSummary(betType, betValue, username);
        });
    }

    public void saveResult(String betType, String winningBetValue) {
        try {
            JSONObject resultObj = new JSONObject();
            resultObj.put(BET_TYPE, betType);
            resultObj.put(WINNING_BET_VALUE, winningBetValue);
            supabaseService.post(TABLE_RESULTS, resultObj.toString());

            String propBetQuery = "bet_type=eq." + URLEncoder.encode(betType, StandardCharsets.UTF_8);
            String propBetResponse = supabaseService.get(TABLE_PROP_BETS, propBetQuery);
            JSONArray propBetsArray = new JSONArray(propBetResponse);

            List<String> winningBetters = new ArrayList<>();
            List<String> losingBetters = new ArrayList<>();

            if (propBetsArray.length() > 0) {
                JSONObject foundPropBet = propBetsArray.getJSONObject(0);

                updatePropBetsSummaryWithResults(betType, winningBetValue, foundPropBet, winningBetters, losingBetters);
            }

            BigDecimal totalBetters = BigDecimal.valueOf(winningBetters.size() + losingBetters.size());
            if (winningBetters.size() > 0) {
                Double amountWonPerBetter = totalBetters.multiply(BigDecimal.valueOf(AMOUNT_PER_BET))
                                                        .divide(BigDecimal.valueOf(winningBetters.size()), 2, RoundingMode.HALF_UP)
                                                        .doubleValue();

                updateUserBetsSummaryForAllWinningBetters(winningBetters, amountWonPerBetter);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save result to Supabase", e);
        }
    }

    private void updatePropBetsSummaryWithResults(String betType,
                                                  String winningBetValue,
                                                  JSONObject foundPropBet,
                                                  List<String> winningBetters,
                                                  List<String> losingBetters) {
        try {
            List<String> betValues = toStringList(foundPropBet.optJSONArray(CHOICES));

            for (String betValue : betValues) {
                String summaryQuery = "bet_type=eq." + URLEncoder.encode(betType, StandardCharsets.UTF_8) +
                                    "&bet_value=eq." + URLEncoder.encode(betValue, StandardCharsets.UTF_8);
                String summaryResponse = supabaseService.get(TABLE_PROP_BETS_SUMMARY, summaryQuery);
                JSONArray summaryArray = new JSONArray(summaryResponse);

                if (summaryArray.length() > 0) {
                    JSONObject foundPropBetsSummary = summaryArray.getJSONObject(0);
                    Set<String> betters = toStringSet(foundPropBetsSummary.optJSONArray(BETTERS));

                    JSONObject updateObj = new JSONObject();

                    if (betValue.equals(winningBetValue)) {
                        winningBetters.addAll(betters);
                        updateObj.put(IS_WINNER, true);
                    } else {
                        losingBetters.addAll(betters);
                        updateObj.put(IS_WINNER, false);
                    }

                    supabaseService.patch(TABLE_PROP_BETS_SUMMARY, summaryQuery, updateObj.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update prop bets summary with results", e);
        }
    }

    private void updateUserBetsSummaryForAllWinningBetters(List<String> winningBetters, Double amountWonPerBetter) {
        try {
            for (String username : winningBetters) {
                String userSummaryQuery = "username=eq." + URLEncoder.encode(username, StandardCharsets.UTF_8);
                JSONArray userSummaryArray = new JSONArray(supabaseService.get(TABLE_USER_BETS_SUMMARY, userSummaryQuery));

                if (userSummaryArray.length() > 0) {
                    JSONObject foundUserBetSummary = userSummaryArray.getJSONObject(0);

                    Double amountOwing = foundUserBetSummary.optDouble(AMOUNT_OWING, 0.0);
                    Integer numberOfScoreBoardBetsWon = foundUserBetSummary.optInt(NUMBER_OF_SCOREBOARD_BETS_WON, 0);
                    Double amountOfScoreBoardBetsWon = foundUserBetSummary.optDouble(AMOUNT_OF_SCOREBOARD_BETS_WON, 0.0);
                    Integer numberOfPropBetsWon = foundUserBetSummary.optInt(NUMBER_OF_PROPBETS_WON, 0) + 1;
                    Double amountOfPropBetsWon = BigDecimal.valueOf(foundUserBetSummary.optDouble(AMOUNT_OF_PROPBETS_WON, 0.0) + amountWonPerBetter)
                                                           .setScale(2, RoundingMode.HALF_UP)
                                                           .doubleValue();
                    Integer numberOfBetsWon = numberOfScoreBoardBetsWon + numberOfPropBetsWon;
                    Double amountWon = BigDecimal.valueOf(amountOfScoreBoardBetsWon + amountOfPropBetsWon)
                                                 .setScale(2, RoundingMode.HALF_UP)
                                                 .doubleValue();
                    Double netAmount = BigDecimal.valueOf(amountWon - amountOwing).setScale(2, RoundingMode.HALF_UP).doubleValue();

                    JSONObject updateObj = new JSONObject();
                    updateObj.put(NUMBER_OF_PROPBETS_WON, numberOfPropBetsWon);
                    updateObj.put(AMOUNT_OF_PROPBETS_WON, amountOfPropBetsWon);
                    updateObj.put(NUMBER_OF_BETS_WON, numberOfBetsWon);
                    updateObj.put(AMOUNT_WON, amountWon);
                    updateObj.put(NET_AMOUNT, netAmount);

                    supabaseService.patch(TABLE_USER_BETS_SUMMARY, userSummaryQuery, updateObj.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user bets summary for winning betters", e);
        }
    }

    public void saveScore(String team1Name, String team1Score, String team2Name, String team2Score) {
        saveResultsToTable(team1Name, team1Score, team2Name, team2Score);

        String betValue = team1Score.substring(team1Score.length() - 1) + "," + team2Score.substring(team2Score.length() - 1);

        ScoreBoardBetsSummary foundPropBetsSummary = getScoreBoardBetsSummaryByBetValue(betValue);

        if (foundPropBetsSummary != null) {
            JSONObject scoreBoardEventsTracker = getScoreBoardEventsTracker();
            final Double[] totalAmountWonPerEvent = {0.0};

            if (scoreBoardEventsTracker != null) {
                updateScoreBoardEventsTracker(scoreBoardEventsTracker, totalAmountWonPerEvent);
            }

            updateScoreInPropBetsSummary(betValue);

            java.util.Map<String, Integer> winningBettersCountMap = new java.util.HashMap<>();
            java.util.Map<String, Double> winningBettersTotalMap = new java.util.HashMap<>();

            findAllWinningScoreEvents(winningBettersCountMap, winningBettersTotalMap, totalAmountWonPerEvent);

            updateScoreBoardBetsInUserBetsSummary(winningBettersCountMap, winningBettersTotalMap);
        }
    }

    public void saveResultsToTable(String team1Name, String team1Score, String team2Name, String team2Score) {
        try {
            JSONObject scoreDocument = new JSONObject();
            scoreDocument.put(team1Name, team1Score);
            scoreDocument.put(team2Name, team2Score);

            supabaseService.post(TABLE_RESULTS, scoreDocument.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save results to Supabase", e);
        }
    }

    public JSONObject getScoreBoardEventsTracker() {
        try {
            String response = supabaseService.get(TABLE_RESULTS, IS_SCOREBOARD_EVENTS_TRACKER + "=eq.true");
            JSONArray arr = new JSONArray(response);

            if (arr.length() > 0) {
                return arr.getJSONObject(0);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get scoreboard events tracker from Supabase", e);
        }
    }

    public void updateScoreBoardEventsTracker(JSONObject scoreBoardEventsTracker, Double[] totalAmountWonPerEvent) {
        try {
            Double totalAmountOfBets = scoreBoardEventsTracker.getDouble(TOTAL_AMOUNT_OF_BETS);
            Integer numberOfWinningEvents = scoreBoardEventsTracker.getInt(NUMBER_OF_WINNING_EVENTS);

            numberOfWinningEvents += 1;

            totalAmountWonPerEvent[0] = BigDecimal.valueOf(totalAmountOfBets / numberOfWinningEvents)
                                                  .setScale(2, RoundingMode.HALF_UP)
                                                  .doubleValue();

            JSONObject updateObj = new JSONObject();
            updateObj.put(NUMBER_OF_WINNING_EVENTS, numberOfWinningEvents);
            updateObj.put(TOTAL_AMOUNT_WON_PER_EVENT, totalAmountWonPerEvent[0]);

            String query = IS_SCOREBOARD_EVENTS_TRACKER + "=eq.true";
            supabaseService.patch(TABLE_RESULTS, query, updateObj.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update scoreboard events tracker in Supabase", e);
        }
    }

    public void updateScoreInPropBetsSummary(String betValue) {
        try {
            String query = "bet_type=eq." + URLEncoder.encode(SCORE_BET_TYPE, StandardCharsets.UTF_8) +
                          "&bet_value=eq." + URLEncoder.encode(betValue, StandardCharsets.UTF_8);
            String response = supabaseService.get(TABLE_PROP_BETS_SUMMARY, query);
            JSONArray arr = new JSONArray(response);

            if (arr.length() > 0) {
                JSONObject foundPropBetsSummary = arr.getJSONObject(0);
                Integer count = foundPropBetsSummary.optInt(COUNT, 0);

                JSONObject updateObj = new JSONObject();
                updateObj.put(COUNT, count + 1);

                supabaseService.patch(TABLE_PROP_BETS_SUMMARY, query, updateObj.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update score in prop bets summary in Supabase", e);
        }
    }

    public void findAllWinningScoreEvents(java.util.Map<String, Integer> winningBettersCountMap,
                                          java.util.Map<String, Double> winningBettersTotalMap,
                                          Double[] totalAmountWonPerEvent) {
        try {
            String query = "bet_type=eq." + URLEncoder.encode(SCORE_BET_TYPE, StandardCharsets.UTF_8) +
                          "&count=not.is.null";
            String response = supabaseService.get(TABLE_PROP_BETS_SUMMARY, query);
            JSONArray arr = new JSONArray(response);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject document = arr.getJSONObject(i);

                Set<String> winningBetters = toStringSet(document.optJSONArray(BETTERS));
                Integer count = document.getInt(COUNT);

                Double amountWonPerBetter = BigDecimal.valueOf((count * totalAmountWonPerEvent[0]) / winningBetters.size())
                                                      .setScale(2, RoundingMode.HALF_UP)
                                                      .doubleValue();

                winningBetters.forEach(username -> {
                    winningBettersCountMap.put(username, winningBettersCountMap.getOrDefault(username, 0) + count);
                    winningBettersTotalMap.put(username, winningBettersTotalMap.getOrDefault(username, 0.0) + amountWonPerBetter);
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all winning score events in Supabase", e);
        }
    }

    public void updateScoreBoardBetsInUserBetsSummary(java.util.Map<String, Integer> winningBettersCountMap,
                                                       java.util.Map<String, Double> winningBettersTotalMap) {
        try {
            winningBettersCountMap.forEach((username, numberOfScoreBoardBetsWon) -> {
                try {
                    String query = "username=eq." + URLEncoder.encode(username, StandardCharsets.UTF_8);
                    String response = supabaseService.get(TABLE_USER_BETS_SUMMARY, query);
                    JSONArray arr = new JSONArray(response);

                    if (arr.length() > 0) {
                        JSONObject foundUserBetSummary = arr.getJSONObject(0);

                        Double amountOwing = foundUserBetSummary.getDouble(AMOUNT_OWING);
                        Integer numberOfPropBetsWon = foundUserBetSummary.optInt(NUMBER_OF_PROPBETS_WON, 0);
                        Double amountOfPropBetsWon = foundUserBetSummary.optDouble(AMOUNT_OF_PROPBETS_WON, 0.0);
                        Integer numberOfBetsWon = numberOfPropBetsWon + numberOfScoreBoardBetsWon;
                        Double amountOfScoreBoardBetsWon = winningBettersTotalMap.get(username);
                        Double amountWon = BigDecimal.valueOf(amountOfPropBetsWon + amountOfScoreBoardBetsWon)
                                                     .setScale(2, RoundingMode.HALF_UP)
                                                     .doubleValue();
                        Double netAmount = BigDecimal.valueOf(amountWon - amountOwing).setScale(2, RoundingMode.HALF_UP).doubleValue();

                        JSONObject updateObj = new JSONObject();
                        updateObj.put(NUMBER_OF_SCOREBOARD_BETS_WON, numberOfScoreBoardBetsWon);
                        updateObj.put(AMOUNT_OF_SCOREBOARD_BETS_WON, amountOfScoreBoardBetsWon);
                        updateObj.put(NUMBER_OF_BETS_WON, numberOfBetsWon);
                        updateObj.put(AMOUNT_WON, amountWon);
                        updateObj.put(NET_AMOUNT, netAmount);

                        supabaseService.patch(TABLE_USER_BETS_SUMMARY, query, updateObj.toString());
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to update scoreboard bets in user bets summary for user: " + username, e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to update scoreboard bets in user bets summary in Supabase", e);
        }
    }

    public void createScoreBoardEventsTracker() {
        try {
            List<String> allBetters = new ArrayList<>();
            List<ScoreBoardBetsSummary> scoreBoardBetsSummary = getScoreBetsSummary();

            scoreBoardBetsSummary.forEach(summary -> allBetters.addAll(summary.betters()));

            JSONObject document = new JSONObject();
            document.put(IS_SCOREBOARD_EVENTS_TRACKER, true);
            document.put(TOTAL_AMOUNT_OF_BETS, Double.valueOf(allBetters.size() * AMOUNT_PER_BET));
            document.put(NUMBER_OF_WINNING_EVENTS, 0);
            document.put(TOTAL_AMOUNT_WON_PER_EVENT, 0.0);

            supabaseService.post(TABLE_RESULTS, document.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create scoreboard events tracker in Supabase", e);
        }
    }

    private ScoreBoardBetsSummary getScoreBoardBetsSummaryByBetValue(String betValue) {
        try {
            String query = "bet_type=eq." + URLEncoder.encode(SCORE_BET_TYPE, StandardCharsets.UTF_8) +
                          "&bet_value=eq." + URLEncoder.encode(betValue, StandardCharsets.UTF_8);
            String response = supabaseService.get(TABLE_PROP_BETS_SUMMARY, query);
            JSONArray arr = new JSONArray(response);

            if (arr.length() > 0) {
                JSONObject obj = arr.getJSONObject(0);
                Set<String> betters = toStringSet(obj.optJSONArray(BETTERS));
                Optional<Integer> count = obj.has(COUNT) ? Optional.of(obj.optInt(COUNT)) : Optional.empty();
                return new ScoreBoardBetsSummary(betValue, betters, count);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get scoreboard bets summary by bet value from Supabase", e);
        }
    }

    public void lockPropBets() {
        try {
            List<PropBet> propBets = getPropBets();
            List<PropBetsSummary> propBetsSummary = getPropBetsSummary();
            List<ScoreBoardBetsSummary> scoreBoardBetsSummary = getScoreBetsSummary();
            List<UserBet> userBets = getUserBets();

            JSONObject updateObj = new JSONObject();
            updateObj.put(IS_LOCKED, true);

            for (PropBet propBet : propBets) {
                String query = "bet_type=eq." + URLEncoder.encode(propBet.name(), StandardCharsets.UTF_8);
                supabaseService.patch(TABLE_PROP_BETS, query, updateObj.toString());
            }

            for (PropBetsSummary summary : propBetsSummary) {
                String query = "bet_type=eq." + URLEncoder.encode(summary.betType(), StandardCharsets.UTF_8) +
                        "&bet_value=eq." + URLEncoder.encode(summary.betValue(), StandardCharsets.UTF_8);
                supabaseService.patch(TABLE_PROP_BETS_SUMMARY, query, updateObj.toString());
            }

            for (ScoreBoardBetsSummary summary : scoreBoardBetsSummary) {
                String query = "bet_type=eq." + URLEncoder.encode(SCORE_BET_TYPE, StandardCharsets.UTF_8) +
                        "&bet_value=eq." + URLEncoder.encode(summary.betValue(), StandardCharsets.UTF_8);
                supabaseService.patch(TABLE_PROP_BETS_SUMMARY, query, updateObj.toString());
            }

            for (UserBet userBet : userBets) {
                String query = "username=eq." + URLEncoder.encode(userBet.username(), StandardCharsets.UTF_8) +
                        "&bet_type=eq." + URLEncoder.encode(userBet.betType(), StandardCharsets.UTF_8) +
                        "&bet_value=eq." + URLEncoder.encode(userBet.betValue(), StandardCharsets.UTF_8);
                supabaseService.patch(TABLE_USER_BETS, query, updateObj.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to lock prop bets in Supabase", e);
        }
    }

    public void deleteAllData() {
        try {
            supabaseService.delete(TABLE_USER_BETS_SUMMARY, "id=gte.0");
            supabaseService.delete(TABLE_PROP_BETS_SUMMARY, "id=gte.0");
            supabaseService.delete(TABLE_USER_BETS, "id=gte.0");
            supabaseService.delete(TABLE_PROP_BETS, "id=gte.0");
            supabaseService.delete(TABLE_RESULTS, "id=gte.0");

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all data from Supabase", e);
        }
    }
}
