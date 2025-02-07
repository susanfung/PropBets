package com.example.application.views.settings;

import com.example.application.data.DataService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class Settings extends VerticalLayout implements BeforeEnterObserver {
    private final DataService dataService;

    public Settings(DataService dataService) {
        this.dataService = dataService;

        Button lockBetsButton = new Button("Lock Bets");
        lockBetsButton.addClickListener(e -> {
            dataService.lockPropBets();
            dataService.createScoreBoardEventsTracker();

            Notification.show("All open bets are now closed.");

            lockBetsButton.getUI().ifPresent(ui -> ui.navigate(""));
        });

        Button deleteAllDataButton = new Button("Delete all data");
        deleteAllDataButton.setEnabled(false);
        deleteAllDataButton.addClickListener(e -> {
            dataService.deleteAllData();

            Notification.show("All data has been deleted");

            deleteAllDataButton.getUI().ifPresent(ui -> ui.navigate(""));
        });

        add(lockBetsButton, deleteAllDataButton);
    }

    public void beforeEnter(BeforeEnterEvent event) {
        String role = (String) VaadinSession.getCurrent().getAttribute("role");
        if (role == null || !role.equals("admin")) {
            event.forwardTo("viewBets");
            Notification.show("Access denied. Admins only.");
        }
    }
}
