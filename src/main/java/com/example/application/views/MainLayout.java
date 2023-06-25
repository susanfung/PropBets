package com.example.application.views;


import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.views.createnewbet.CreateNewBet;
import com.example.application.views.placebets.PlaceBets;
import com.example.application.views.viewbets.ViewBets;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
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

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        Image betsIcon = new Image("images/football-bet.png", "Bets");
        betsIcon.setWidth("24px");
        betsIcon.setHeight("24px");

        Image placeBetsIcon = new Image("images/place-bet.png", "Place Bets");
        placeBetsIcon.setWidth("24px");
        placeBetsIcon.setHeight("24px");

        Image createNewBetIcon = new Image("images/create-new-bet.png", "Create New Bet");
        createNewBetIcon.setWidth("24px");
        createNewBetIcon.setHeight("24px");

        nav.addItem(new AppNavItem("View Bets", ViewBets.class, betsIcon));
        nav.addItem(new AppNavItem("Place Bets", PlaceBets.class, placeBetsIcon));
        nav.addItem(new AppNavItem("Create New Bet", CreateNewBet.class, createNewBetIcon));

        return nav;
    }

    private VerticalLayout createFooter() {
        VerticalLayout layout = new VerticalLayout();

        Anchor anchorBetIcon = new Anchor("https://www.flaticon.com/free-icons/bet", "Bet icons created by Freepik - Flaticon");
        Anchor anchorBettingIcon = new Anchor("https://www.flaticon.com/free-icons/betting", "Betting icons created by Vitaly Gorbachev - Flaticon");

        anchorBetIcon.setTitle("Bet Icons");
        anchorBetIcon.getElement().getStyle().set("font-size", "10px");

        anchorBettingIcon.setTitle("Betting Icons");
        anchorBettingIcon.getElement().getStyle().set("font-size", "10px");

        layout.add(anchorBetIcon, anchorBettingIcon);

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
}
