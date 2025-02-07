package com.example.application.views.placebets;

import com.example.application.data.DataService;
import com.example.application.data.PropBet;
import com.example.application.data.UserBet;
import com.example.application.utils.Utils;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.example.application.utils.Utils.AMOUNT_PER_BET;
import static com.example.application.utils.Utils.ICON_SIZE;
import static com.example.application.utils.Utils.TEAM_1_LOGO_SOURCE;
import static com.example.application.utils.Utils.TEAM_1_NAME;
import static com.example.application.utils.Utils.TEAM_2_LOGO_SOURCE;
import static com.example.application.utils.Utils.TEAM_2_NAME;
import static com.example.application.utils.Utils.createPropBet;

@PageTitle("Place Bets")
@Route(value = "placeBets", layout = MainLayout.class)
public class PlaceBets extends VerticalLayout {
    private final DataService dataService;

    private Boolean isScoreBoardBetsLocked = false;

    private final Map<String, String> scoreBoardBets = new HashMap<>();
    private final Map<String, String> propBets = new HashMap<>();

    private int betCount = 0;
    private Paragraph totalBets;
    private Paragraph totalAmount;

    public PlaceBets(DataService dataService) {
        this.dataService = dataService;
        Document scoreBoardEventsTracker = dataService.getIsScoreBoardEventsTracker();

        if (scoreBoardEventsTracker != null) {
            isScoreBoardBetsLocked = true;
        }

        H2 scoreBoardTitle = new H2("Score Board");

        HorizontalLayout teams = new HorizontalLayout();

        Image team1Icon = new Image(TEAM_1_LOGO_SOURCE, TEAM_1_NAME);
        team1Icon.setWidth(ICON_SIZE);
        team1Icon.setHeight(ICON_SIZE);

        Button team1 = new Button(TEAM_1_NAME);
        team1.setEnabled(false);
        team1.addClassName("team-1");
        team1.setIcon(team1Icon);

        Image team2Icon = new Image(TEAM_2_LOGO_SOURCE, TEAM_2_NAME);
        team2Icon.setWidth(ICON_SIZE);
        team2Icon.setHeight(ICON_SIZE);

        Button team2 = new Button(TEAM_2_NAME);
        team1.setEnabled(false);
        team2.addClassName("team-2");
        team2.setIcon(team2Icon);

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
            RadioButtonGroup<String> bet = createPropBet(propBet.name(), propBet.question(), propBet.choices());
            bet.setEnabled(!propBet.isLocked().orElse(false));
            bet.addValueChangeListener(e -> {
                if (bet.isEnabled()) {
                    handlePropBetSelection(bet.getClassName(), e.getValue());
                }
            });
            add(bet);
        });

        Hr separator = new Hr();

        totalBets = new Paragraph(totalBetsText());
        totalBets.getStyle().set("margin", "0px");
        totalAmount = new Paragraph(totalAmountText());
        totalAmount.getStyle().set("margin", "0px");

        add(separator, totalBets, totalAmount);

        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        if (username != null && !username.isEmpty()) {
            List<UserBet> userBets = this.dataService.findUserBetsByUsername(username);
            displayUserBets(userBets);
        }

        Button submit = new Button("Submit");
        submit.addClickListener(e -> {
            this.dataService.deletePreviousBets(username);

            this.dataService.saveScoreBoardBets(username, scoreBoardBets);
            this.dataService.savePropBets(username, propBets);

            this.dataService.updateUserBetsSummary(username, betCount);

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
            cellButton.setEnabled(!isScoreBoardBetsLocked);
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

    private void handlePropBetSelection(String betType, String betValue) {
        if (!propBets.containsKey(betType)) {
            increaseBetCounter();
        }

        propBets.put(betType, betValue);
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

    private String totalAmountText() {
        return String.format("Total Amount: $%d", betCount * AMOUNT_PER_BET);
    }

    private void setParagraphTexts() {
        totalBets.setText(totalBetsText());
        totalAmount.setText(totalAmountText());
    }

    private void displayUserBets(List<UserBet> userBets) {
        userBets.forEach(userBet -> {
            if (userBet.betType().equals("Score")) {
                findButtonByValue(userBet.betValue()).ifPresent(button -> {
                    button.addClassName("selected");

                    if (button.isEnabled()) {
                        scoreBoardBets.put(userBet.betValue(),
                                           userBet.betType());
                        increaseBetCounter();
                    }
                });
            } else {
                Utils.findRadioButtonGroup(this, userBet.betType()).ifPresent(group -> {
                    group.setValue(userBet.betValue());
                });
            }
        });
    }

    private Optional<Button> findButtonByValue(String value) {
        return getChildren()
                .filter(HorizontalLayout.class::isInstance)
                .flatMap(Component::getChildren)
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(button -> value.equals(button.getText()))
                .findFirst()
                .map(button -> {
                    button.addClassName("selected");
                    return button;
                });
    }
}
