package com.example.application.security;

import com.example.application.views.login.Login;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

public class SecurityFilter implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            uiEvent.getUI().addBeforeEnterListener((BeforeEnterListener) e -> {
                if (!Login.class.equals(e.getNavigationTarget()) &&
                        VaadinSession.getCurrent().getAttribute("username") == null) {
                    e.rerouteTo(Login.class);
                }
            });
        });
    }
}
