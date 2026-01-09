package com.example.application.views.saveresults;

import com.example.application.data.DataService;
import com.example.application.data.PropBet;
import com.example.application.data.Result;
import com.example.application.utils.Utils;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.application.utils.Utils.TEAM_1_NAME;
import static com.example.application.utils.Utils.TEAM_1_TABLE_NAME;
import static com.example.application.utils.Utils.TEAM_2_NAME;
import static com.example.application.utils.Utils.TEAM_2_TABLE_NAME;
import static com.example.application.utils.Utils.createPropBet;

@PageTitle("Save Results")
@Route(value = "results", layout = MainLayout.class)
public class SaveResults extends VerticalLayout implements BeforeEnterObserver {
    private final DataService dataService;

    private TextField team1Score;
    private TextField team2Score;

    private static final Map<String, String> propBetsResults = new HashMap<>();

    public SaveResults(DataService dataService) {
        this.dataService = dataService;

        H2 scoreBoardTitle = new H2("Score Board Bets");

        HorizontalLayout scoreFields = new HorizontalLayout();

        team1Score = new TextField(TEAM_1_NAME);
        team2Score = new TextField(TEAM_2_NAME);

        Button submitScore = new Button("Submit Score");
        submitScore.addClickListener(e -> {
            dataService.saveScore(TEAM_1_TABLE_NAME, team1Score.getValue(), TEAM_2_TABLE_NAME, team2Score.getValue());

            Notification.show("Score saved!");

            submitScore.getUI().ifPresent(ui -> ui.navigate(""));
        });

        scoreFields.add(team1Score, team2Score);

        Hr separator1 = new Hr();
        add(scoreBoardTitle, scoreFields, submitScore, separator1);

        H2 propBetsTitle = new H2("PropBets");
        add(propBetsTitle);

        List<PropBet> propBetList = this.dataService.getPropBets("");
        propBetList.forEach(propBet -> {
            RadioButtonGroup<String> bet = createPropBet(propBet.name(), propBet.question(), propBet.choices());
            bet.addValueChangeListener(e -> handlePropBetSelection(bet.getClassName(), e.getValue()));
            add(bet);
        });

        List<Result> results = this.dataService.findResults();
        displayResults(results);
        propBetsResults.clear();

        Button submitPropBets = new Button("Submit PropBets");
        submitPropBets.addClickListener(e -> {
            propBetsResults.forEach(this.dataService::saveResult);

            Notification.show("Results saved!");

            propBetsResults.clear();

            submitPropBets.getUI().ifPresent(ui -> ui.navigate(""));
        });

        add(submitPropBets);
    }

    private void handlePropBetSelection(String betType, String winningValue) {
        propBetsResults.put(betType, winningValue);
    }

    private void displayResults(List<Result> results) {
        results.forEach(result -> {
            if (result.betType().equals("Score")) {
            } else {
                Utils.findRadioButtonGroup(this, result.betType()).ifPresent(group -> {
                    group.setValue(result.winningBetValue());
                    group.setEnabled(false);
                });
            }
        });
    }

    public void beforeEnter(BeforeEnterEvent event) {
        String role = (String) VaadinSession.getCurrent().getAttribute("role");
        if (role == null || !role.equals("admin")) {
            event.forwardTo("viewBets");
            Notification.show("Access denied. Admins only.");
        }
    }
}
