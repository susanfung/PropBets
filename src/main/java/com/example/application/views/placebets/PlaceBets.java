package com.example.application.views.placebets;

import com.example.application.data.DataService;
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

import java.util.HashSet;
import java.util.Set;

@PageTitle("Place Bets")
@Route(value = "about", layout = MainLayout.class)
public class PlaceBets extends VerticalLayout {
    private TextField name;
    private Button submit;
    private final DataService dataService;

    private Set<String> selectedCells = new HashSet<>();

    public PlaceBets(DataService dataService) {
        this.dataService = dataService;

        name = new TextField("Your name");
        name.setRequiredIndicatorVisible(true);
        add(name);

        H2 scoreBoardTitle = new H2("Score Board");
        add(scoreBoardTitle);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(new Button());
        for (int col = 0; col <= 9; col++) {
            Button columnButton = new Button(String.valueOf(col));
            columnButton.setEnabled(false);
            headerLayout.add(columnButton);
        }
        add(headerLayout);

        for (int row = 0; row <= 9; row++) {
            HorizontalLayout rowLayout = new HorizontalLayout();
            Button rowButton = new Button(String.valueOf(row));
            rowButton.setEnabled(false);
            rowLayout.add(rowButton);
            for (int col = 0; col <= 9; col++) {
                Button cellButton = new Button(row + "," + col);
                cellButton.addClickListener(e -> handleCellSelection(cellButton));
                rowLayout.add(cellButton);
            }
            add(rowLayout);
        }

        H2 propBetsTitle = new H2("PropBets");

        RadioButtonGroup<String> betColourOfGatorade = new RadioButtonGroup<>();
        betColourOfGatorade.setClassName("Gatorade Colour");
        betColourOfGatorade.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        betColourOfGatorade.setLabel("Colour of Gatorade");
        betColourOfGatorade.setItems("Blue / Purple", "Orange / Yellow", "Red", "Green", "Clear / Water");

        submit = new Button("Submit");
        submit.addClickListener(e -> {
            String username = name.getValue();

            if (username.isEmpty()) {
                Notification.show("Please enter your name");
                return;
            }

            for (String cell : selectedCells) {
                UserBet bet = new UserBet(username, "Score", cell);
                this.dataService.addUserBet(bet);
            }

            String betValue = betColourOfGatorade.getValue();

            submit.setEnabled(false);

            UserBet bet = new UserBet(username, betColourOfGatorade.getClassName(), betValue);

            Notification.show("Bet submitted!");
            this.dataService.addUserBet(bet);
        });

        add(propBetsTitle,
            betColourOfGatorade,
            submit);
    }

    private void handleCellSelection(Button cellButton) {
        String cellId = cellButton.getText();
        if (selectedCells.contains(cellId)) {
            selectedCells.remove(cellId);
            cellButton.removeClassName("selected");
        } else {
            selectedCells.add(cellId);
            cellButton.addClassName("selected");
        }
    }
}
