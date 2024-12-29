package com.example.application.views.login;

import com.example.application.security.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Login")
@Route("login")
public class Login extends VerticalLayout implements BeforeEnterObserver {
    @Autowired
    private UserService userService;

    private TextField usernameField;
    private Button loginButton;
    private Button createUserButton;

    public Login() {
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setSizeFull();
        getStyle().set("position", "relative");

        Image propBetsLogo = new Image("images/prop-bets-logo.png", "Prop Bets");
        propBetsLogo.getStyle().set("max-width", "100%");
        propBetsLogo.getStyle().set("height", "auto");

        usernameField = new TextField("Username");
        usernameField.addKeyPressListener(Key.ENTER, event -> login());

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        loginButton = new Button("Log in", event -> login());
        createUserButton = new Button("Sign up", event -> saveUser());

        buttonsLayout.add(loginButton, createUserButton);

        Image partnerImage = new Image("images/murfys-bbq.png", "Murfy's BBQ");
        partnerImage.setWidth("25%");

        add(propBetsLogo, usernameField, buttonsLayout, partnerImage);
    }

    private void login() {
        String username = usernameField.getValue();
        if (username.isEmpty()) {
            usernameField.setHelperText("Please enter a username");
        } else {
            Document user = userService.findUserByUsername(username);
            if (user == null) {
                usernameField.setHelperText("User does not exist");
                return;
            }

            getUI().ifPresent(ui -> {
                ui.getSession().setAttribute("username", user.getString("username"));
                ui.getSession().setAttribute("role", user.getString("role"));
                ui.navigate("viewBets");
            });
        }
    }

    private void saveUser() {
        String username = usernameField.getValue();
        if (username.isEmpty()) {
            usernameField.setHelperText("Please enter a username");
        } else {
            Document user = userService.findUserByUsername(username);
            if (user != null) {
                usernameField.setHelperText("User already exists");
                return;
            }

            userService.saveUser(username);

            getUI().ifPresent(ui -> {
                ui.getSession().setAttribute("username", user.getString("username"));
                ui.getSession().setAttribute("role", user.getString("role"));
                ui.navigate("viewBets");
            });
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (getUI().isPresent() && getUI().get().getSession().getAttribute("username") != null) {
            event.forwardTo("viewBets");
        }
    }
}
