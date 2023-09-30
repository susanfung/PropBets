package com.example.application.views.placebets;

import com.example.application.data.DataService;
import com.example.application.data.UserBet;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Place Bets")
@Route(value = "about", layout = MainLayout.class)
public class PlaceBets extends VerticalLayout {
    private TextField name;
    private Button submit;
    private final DataService dataService;

    public PlaceBets(DataService dataService) {
        this.dataService = dataService;

        name = new TextField("Your name");

        RadioButtonGroup<String> betColourOfGatorade = new RadioButtonGroup<>();
        betColourOfGatorade.setClassName("Gatorade Colour");
        betColourOfGatorade.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        betColourOfGatorade.setLabel("Colour of Gatorade");
        betColourOfGatorade.setItems("Blue / Purple", "Orange / Yellow", "Red", "Green", "Clear / Water");

        submit = new Button("Submit");
        submit.addClickListener(e -> {
            String username = name.getValue();
            String betValue = betColourOfGatorade.getValue();

            submit.setEnabled(false);

            UserBet bet = new UserBet(username, betColourOfGatorade.getClassName(), betValue);

            Notification.show("Bet submitted!");
            this.dataService.addUserBet(bet);
        });

        add(name, betColourOfGatorade, submit);
    }

}
