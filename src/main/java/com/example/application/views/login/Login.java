package com.example.application.views.login;

import com.example.application.security.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
public class Login extends VerticalLayout implements BeforeEnterObserver {
    @Autowired
    private UserService userService;

    private TextField usernameField;
    private Button loginButton;

    public Login() {
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setSizeFull();

        Image propBetsLogo = new Image("images/prop-bets-logo.png", "Prop Bets");
        propBetsLogo.getStyle().set("max-width", "100%");
        propBetsLogo.getStyle().set("height", "auto");

        usernameField = new TextField("Username");
        loginButton = new Button("Login", event -> login());

        add(propBetsLogo, usernameField, loginButton);
    }

    private void login() {
        String username = usernameField.getValue();
        if (username.isEmpty()) {
            Notification.show("Please enter a username");
        } else {
            Document user = userService.findUserByUsername(username);
            if (user == null) {
                userService.saveUser(username);
            }

            getUI().ifPresent(ui -> {
                ui.getSession().setAttribute("username", username);
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
