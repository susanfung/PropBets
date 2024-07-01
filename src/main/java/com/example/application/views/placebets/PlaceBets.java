package com.example.application.views.placebets;

import com.example.application.data.DataService;
import com.example.application.data.PropBet;
import com.example.application.data.UserBet;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@PageTitle("Place Bets")
@Route(value = "placeBets", layout = MainLayout.class)
public class PlaceBets extends VerticalLayout {
    private final DataService dataService;

    private static final Map<String, String> bets = new HashMap<>();

    public PlaceBets(DataService dataService) {
        this.dataService = dataService;

        TextField name = new TextField("Your name");
        name.setRequiredIndicatorVisible(true);
        add(name);

        H2 scoreBoardTitle = new H2("Score Board");

        HorizontalLayout teams = new HorizontalLayout();

        Button team1 = new Button("Team 1 Score");
        team1.setEnabled(false);
        team1.addClassName("team-1");

        Button team2 = new Button("Team 2 Score");
        team1.setEnabled(false);
        team2.addClassName("team-2");

        teams.add(team1, team2);

        HorizontalLayout columnNamesForScoreBoard = createColumnNamesForScoreBoard();
        add(scoreBoardTitle, teams, columnNamesForScoreBoard);

        IntStream.rangeClosed(0, 9).forEach(row -> {
            HorizontalLayout rowsForScoreBoard = createRowsForScoreBoard(row);
            add(rowsForScoreBoard);
        });

        H2 propBetsTitle = new H2("PropBets");
        add(propBetsTitle);

        List<PropBet> propBets = this.dataService.getPropBets();
        propBets.forEach(propBet -> {
            RadioButtonGroup<String> bet = createPropBet(propBet.getName(), propBet.getQuestion(), propBet.getChoices());
            add(bet);
        });

        Button submit = new Button("Submit");
        submit.addClickListener(e -> {
            String username = name.getValue();

            if (username.isEmpty()) {
                Notification.show("Please enter your name");
                return;
            }

            bets.forEach((betValue, betType) -> {
                UserBet bet = new UserBet(username, betType, betValue);
                this.dataService.addUserBet(bet);
            });

            Notification.show("Bet submitted!");
        });

        add(submit);
    }

    private static HorizontalLayout createColumnNamesForScoreBoard() {
        HorizontalLayout columnNames = new HorizontalLayout();
        columnNames.add(new Button());
        for (int col = 0; col <= 9; col++) {
            Button columnButton = new Button(String.valueOf(col));
            columnButton.setEnabled(false);
            columnButton.addClassName("team-2");
            columnNames.add(columnButton);
        }
        return columnNames;
    }

    private HorizontalLayout createRowsForScoreBoard(int row) {
        HorizontalLayout rows = new HorizontalLayout();
        Button rowButton = new Button(String.valueOf(row));
        rowButton.setEnabled(false);
        rowButton.addClassName("team-1");
        rows.add(rowButton);

        IntStream.rangeClosed(0, 9).forEach(col -> {
            Button cellButton = new Button(row + "," + col);
            cellButton.addClickListener(e -> handleScoreBoardSelection(cellButton));
            rows.add(cellButton);
        });
        return rows;
    }

    private void handleScoreBoardSelection(Button button) {
        String betValue = button.getText();
        if (bets.containsKey(betValue)) {
            bets.remove(betValue);
            button.removeClassName("selected");
        } else {
            bets.put(betValue, "Score");
            button.addClassName("selected");
        }
    }

    private static RadioButtonGroup<String> createPropBet(String name, String question, List<String> choices) {
        RadioButtonGroup<String> propBet = new RadioButtonGroup<>();
        propBet.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        propBet.setClassName(name);
        propBet.setLabel(question);
        propBet.setItems(choices);
        propBet.addValueChangeListener(e -> handlePropBetSelection(propBet.getClassName(), e.getValue()));
        return propBet;
    }

    private static void handlePropBetSelection(String betType, String betValue) {
        bets.put(betValue, betType);
    }
}
