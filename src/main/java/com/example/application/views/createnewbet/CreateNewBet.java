package com.example.application.views.createnewbet;

import com.example.application.data.DataService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Create New Bet")
@Route(value = "createNewBet", layout = MainLayout.class)
public class CreateNewBet extends VerticalLayout implements BeforeEnterObserver {
    private final DataService dataService;

    private final TextField name;
    private final TextField question;
    private final TextArea choices;

    public CreateNewBet(DataService dataService) {
        this.dataService = dataService;

        name = new TextField("Name of Bet");
        name.setRequiredIndicatorVisible(true);

        question = new TextField("Question");
        question.setRequiredIndicatorVisible(true);

        choices = new TextArea();
        choices.addThemeVariants(TextAreaVariant.LUMO_HELPER_ABOVE_FIELD);
        choices.setRequiredIndicatorVisible(true);
        choices.setLabel("Choices");
        choices.setHelperText("Enter choices separated by commas");
        choices.setWidthFull();

        Button submit = getSubmitButton();

        add(name, question, choices, submit);
    }

    private Button getSubmitButton() {
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

            this.dataService.createNewPropBet(name.getValue(), question.getValue(), choices.getValue());

            Notification.show("Bet created!");
            clearForm();
        });
        return submit;
    }

    private void clearForm() {
        name.clear();
        question.clear();
        choices.clear();
    }

    public void beforeEnter(BeforeEnterEvent event) {
        String role = (String) VaadinSession.getCurrent().getAttribute("role");
        if (role == null || !role.equals("admin")) {
            event.forwardTo("viewBets");
            Notification.show("Access denied. Admins only.");
        }
    }
}
