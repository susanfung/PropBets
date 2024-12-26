package com.example.application.views;


import com.example.application.views.createnewbet.CreateNewBet;
import com.example.application.views.placebets.PlaceBets;
import com.example.application.views.profile.Profile;
import com.example.application.views.viewbets.ViewBets;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Image propBetsLogo = new Image("images/prop-bets-logo.png", "Prop Bets");
        propBetsLogo.setWidth("100%");
        propBetsLogo.setHeight("100%");

        Header header = new Header(propBetsLogo);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        Image betsIcon = new Image("icons/football-bet.png", "Bets");
        betsIcon.setWidth("24px");
        betsIcon.setHeight("24px");

        Image placeBetsIcon = new Image("icons/place-bet.png", "Place Bets");
        placeBetsIcon.setWidth("24px");
        placeBetsIcon.setHeight("24px");

        Image createNewBetIcon = new Image("icons/create-new-bet.png", "Create New Bet");
        createNewBetIcon.setWidth("24px");
        createNewBetIcon.setHeight("24px");

        Image userIcon = new Image("icons/user.png", "Profile");
        userIcon.setWidth("24px");
        userIcon.setHeight("24px");

        nav.addItem(new SideNavItem("View Bets", ViewBets.class, betsIcon));
        nav.addItem(new SideNavItem("Place Bets", PlaceBets.class, placeBetsIcon));

        String role = (String) VaadinSession.getCurrent().getAttribute("role");
        if ("admin".equals(role)) {
            nav.addItem(new SideNavItem("Create New Bet", CreateNewBet.class, createNewBetIcon));
        }

        nav.addItem(new SideNavItem("Profile", Profile.class, userIcon));

        return nav;
    }

    private VerticalLayout createFooter() {
        VerticalLayout layout = new VerticalLayout();

        Button logoutButton = new Button("Logout", event -> logout());

        Anchor anchorBetIcon = new Anchor("https://www.flaticon.com/free-icons/bet", "Bet icons created by Freepik - Flaticon");
        Anchor anchorBettingIcon = new Anchor("https://www.flaticon.com/free-icons/betting", "Betting icons created by Vitaly Gorbachev - Flaticon");
        Anchor anchorProfileIcon = new Anchor("https://www.flaticon.com/free-icons/user", "User icons created by Freepik - Flaticon");

        anchorBetIcon.setTitle("Bet Icons");
        anchorBetIcon.getElement().getStyle().set("font-size", "10px");

        anchorBettingIcon.setTitle("Betting Icons");
        anchorBettingIcon.getElement().getStyle().set("font-size", "10px");

        anchorProfileIcon.setTitle("User Icons");
        anchorProfileIcon.getElement().getStyle().set("font-size", "10px");

        layout.add(logoutButton, anchorBetIcon, anchorBettingIcon, anchorProfileIcon);

        return layout;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private void logout() {
        getUI().ifPresent(ui -> {
            ui.getSession().close();
            ui.getPage().setLocation("login");
        });
    }
}
