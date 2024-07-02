package com.example.application.views.createnewbet;

import com.example.application.data.DataService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.example.application.data.PropBet.createNewPropBet;

@PageTitle("Create New Bet")
@Route(value = "createNewBet", layout = MainLayout.class)
public class CreateNewBet extends VerticalLayout {
    private final DataService dataService;

    public CreateNewBet(DataService dataService) {
        this.dataService = dataService;

        TextField name = new TextField("Name of Bet");
        name.setRequiredIndicatorVisible(true);

        TextField question = new TextField("Question");
        question.setRequiredIndicatorVisible(true);

        TextArea choices = new TextArea();
        choices.addThemeVariants(TextAreaVariant.LUMO_HELPER_ABOVE_FIELD);
        choices.setRequiredIndicatorVisible(true);
        choices.setLabel("Choices");
        choices.setHelperText("Enter choices separated by commas");
        choices.setWidthFull();

        Button submit = new Button("Submit");
        submit.addClickListener(e -> {
            if (name.isEmpty() || question.isEmpty() || choices.isEmpty()) {
                Notification.show("Please complete all required fields.");
                return;
            }

            if (this.dataService.isPropBetNameTaken(name.getValue())) {
                Notification.show("Name already taken. Please choose another.");
                return;
            }

            this.dataService.addPropBet(createNewPropBet(name.getValue(),
                                                         question.getValue(),
                                                         choices.getValue()));

            Notification.show("Bet created!");
        });

        add(name, question, choices, submit);
    }
}
