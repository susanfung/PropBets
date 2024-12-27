package com.example.application.views.placebets;

import com.example.application.data.DataService;
import com.example.application.data.PropBet;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@PageTitle("Place Bets")
@Route(value = "placeBets", layout = MainLayout.class)
public class PlaceBets extends VerticalLayout {
    public static final int AMOUNT_PER_BET = 2;

    private final DataService dataService;

    private final Map<String, String> scoreBoardBets = new HashMap<>();
    private static final Map<String, String> propBets = new HashMap<>();

    private int betCount = 0;
    private Paragraph totalBets;
    private Paragraph totalAmount;

    public PlaceBets(DataService dataService) {
        this.dataService = dataService;

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

        List<PropBet> propBetList = this.dataService.getPropBets();
        propBetList.forEach(propBet -> {
            RadioButtonGroup<String> bet = createPropBet(propBet.getName(), propBet.getQuestion(), propBet.getChoices());
            add(bet);
        });

        Hr separator = new Hr();

        totalBets = new Paragraph(totalBetsText());
        totalBets.getStyle().set("margin", "0px");
        totalAmount = new Paragraph(totalAmountText());
        totalAmount.getStyle().set("margin", "0px");

        add(separator, totalBets, totalAmount);

        Button submit = new Button("Submit");
        submit.addClickListener(e -> {
            String username = (String) VaadinSession.getCurrent().getAttribute("username");

            if (username.isEmpty()) {
                Notification.show("Please enter your name");
                return;
            }

            this.dataService.saveScoreBoardBets(username, scoreBoardBets);
            this.dataService.savePropBets(username, propBets);

            this.dataService.updateUser(username, betCount, betCount * AMOUNT_PER_BET);

            Notification.show("Bet submitted!");

            scoreBoardBets.clear();
            propBets.clear();

            submit.getUI().ifPresent(ui -> ui.navigate(""));
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
        if (scoreBoardBets.containsKey(betValue)) {
            scoreBoardBets.remove(betValue);
            button.removeClassName("selected");
            decreaseBetCounter();
        } else {
            scoreBoardBets.put(betValue, "Score");
            button.addClassName("selected");
            increaseBetCounter();
        }
    }

    private RadioButtonGroup<String> createPropBet(String name, String question, List<String> choices) {
        RadioButtonGroup<String> propBet = new RadioButtonGroup<>();
        propBet.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        propBet.setClassName(name);
        propBet.setLabel(question);
        propBet.setItems(choices);
        propBet.addValueChangeListener(e -> handlePropBetSelection(propBet.getClassName(), e.getValue()));
        return propBet;
    }

    private void handlePropBetSelection(String betType, String betValue) {
        propBets.put(betType, betValue);
        increaseBetCounter();
    }

    private void increaseBetCounter() {
        betCount++;
        setParagraphTexts();
    }

    private void decreaseBetCounter() {
        betCount--;
        setParagraphTexts();
    }

    private String totalBetsText() {
        return String.format("Total Bets: %d", betCount);
    }

    private void setParagraphTexts() {
        totalBets.setText(totalBetsText());
        totalAmount.setText(totalAmountText());
    }

    private String totalAmountText() {
        return String.format("Total Amount: $%d", betCount * AMOUNT_PER_BET);
    }
}
