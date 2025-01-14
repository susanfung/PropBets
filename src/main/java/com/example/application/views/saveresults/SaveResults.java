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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.application.utils.Utils.createPropBet;

@PageTitle("Save Results")
@Route(value = "results", layout = MainLayout.class)
public class SaveResults extends VerticalLayout implements BeforeEnterObserver {
    private final DataService dataService;

    private static final Map<String, String> propBetsResults = new HashMap<>();

    public SaveResults(DataService dataService) {
        this.dataService = dataService;

        H2 scoreBoardTitle = new H2("Score Board Bets");
        Hr separator1 = new Hr();
        add(scoreBoardTitle, separator1);

        H2 propBetsTitle = new H2("PropBets");
        add(propBetsTitle);

        List<PropBet> propBetList = this.dataService.getPropBets();
        propBetList.forEach(propBet -> {
            RadioButtonGroup<String> bet = createPropBet(propBet.name(), propBet.question(), propBet.choices());
            bet.addValueChangeListener(e -> handlePropBetSelection(bet.getClassName(), e.getValue()));
            add(bet);
        });

        List<Result> results = this.dataService.findResults();
        displayResults(results);

        Hr separator2 = new Hr();

        Button submit = new Button("Submit");
        submit.addClickListener(e -> {
            propBetsResults.forEach(this.dataService::saveResult);

            Notification.show("Results saved!");

            propBetsResults.clear();

            submit.getUI().ifPresent(ui -> ui.navigate(""));
        });

        add(separator2, submit);
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
