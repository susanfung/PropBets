package com.example.application.views;


import com.example.application.views.createnewbet.CreateNewBet;
import com.example.application.views.placebets.PlaceBets;
import com.example.application.views.profile.Profile;
import com.example.application.views.saveresults.SaveResults;
import com.example.application.views.viewbets.ViewBets;
import com.vaadin.flow.component.Text;
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
        String iconSize = "24px";

        Image betsIcon = new Image("icons/football-bet.png", "Bets");
        betsIcon.setWidth(iconSize);
        betsIcon.setHeight(iconSize);

        Image placeBetsIcon = new Image("icons/place-bet.png", "Place Bets");
        placeBetsIcon.setWidth(iconSize);
        placeBetsIcon.setHeight(iconSize);

        Image createNewBetIcon = new Image("icons/create-new-bet.png", "Create New Bet");
        createNewBetIcon.setWidth(iconSize);
        createNewBetIcon.setHeight(iconSize);

        Image userIcon = new Image("icons/user.png", "Profile");
        userIcon.setWidth(iconSize);
        userIcon.setHeight(iconSize);

        Image saveResultsIcon = new Image("icons/win.png", "Save Results");
        saveResultsIcon.setWidth(iconSize);
        saveResultsIcon.setHeight(iconSize);

        nav.addItem(new SideNavItem("View Bets", ViewBets.class, betsIcon));
        nav.addItem(new SideNavItem("Place Bets", PlaceBets.class, placeBetsIcon));

        String role = (String) VaadinSession.getCurrent().getAttribute("role");
        if ("admin".equals(role)) {
            nav.addItem(new SideNavItem("Create New Bet", CreateNewBet.class, createNewBetIcon));
            nav.addItem(new SideNavItem("Save Results", SaveResults.class, saveResultsIcon));
        }

        nav.addItem(new SideNavItem("Profile", Profile.class, userIcon));

        return nav;
    }

    private VerticalLayout createFooter() {
        VerticalLayout layout = new VerticalLayout();

        Text partnerText = new Text("Partnered with:");

        Image partnerImage = new Image("images/murfys-bbq.png", "Murfy's BBQ");
        partnerImage.setWidth("100%");

        Button logoutButton = new Button("Logout", event -> logout());

        Anchor anchorBetIcon = new Anchor("https://www.flaticon.com/free-icons/bet", "Bet icons created by Freepik - Flaticon");
        Anchor anchorBettingIcon = new Anchor("https://www.flaticon.com/free-icons/betting", "Betting icons created by Vitaly Gorbachev - Flaticon");
        Anchor anchorProfileIcon = new Anchor("https://www.flaticon.com/free-icons/user", "User icons created by Freepik - Flaticon");
        Anchor anchorWinnerIcon = new Anchor("https://www.flaticon.com/free-icons/success", "Winner icons created by Freepik - Flaticon");
        Anchor anchorSuccessIcon = new Anchor("https://www.flaticon.com/free-icons/winner", "Success icons created by Wichai.wi - Flaticon");
        Anchor anchorLoserIcon = new Anchor("https://www.flaticon.com/free-icons/loser", "Loser icons created by surang - Flaticon");

        anchorBetIcon.setTitle("Bet Icons");
        anchorBetIcon.getElement().getStyle().set("font-size", "10px");

        anchorBettingIcon.setTitle("Betting Icons");
        anchorBettingIcon.getElement().getStyle().set("font-size", "10px");

        anchorProfileIcon.setTitle("User Icons");
        anchorProfileIcon.getElement().getStyle().set("font-size", "10px");

        anchorWinnerIcon.setTitle("Winner Icons");
        anchorWinnerIcon.getElement().getStyle().set("font-size", "10px");

        anchorSuccessIcon.setTitle("Success Icons");
        anchorSuccessIcon.getElement().getStyle().set("font-size", "10px");

        anchorLoserIcon.setTitle("Loser Icons");
        anchorLoserIcon.getElement().getStyle().set("font-size", "10px");

        layout.add(partnerText, partnerImage, logoutButton, anchorBetIcon, anchorBettingIcon, anchorProfileIcon, anchorWinnerIcon, anchorSuccessIcon, anchorLoserIcon);

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
