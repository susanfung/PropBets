package com.example.application.views.viewbets;

import com.example.application.data.DataService;
import com.example.application.data.User;
import com.example.application.data.UserBet;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;

@PageTitle("View Bets")
@Route(value = "viewBets", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ViewBets extends VerticalLayout {
    private Grid<UserBet> grid;
    private final DataService dataService;

    public ViewBets(DataService dataService) {
        this.dataService = dataService;

        Div scrollableDiv = new Div();
        scrollableDiv.getStyle().set("overflow-x", "hidden");
        scrollableDiv.setWidthFull();
        scrollableDiv.setId("scrollableDiv");

        HorizontalLayout usersLayout = new HorizontalLayout();
        usersLayout.setSpacing(false);
        usersLayout.setWidthFull();

        List<User> users = this.dataService.getUsers();
        users.forEach(user -> {
            HorizontalLayout userLayout = createUserLayout(user);
            usersLayout.add(userLayout);
        });

        scrollableDiv.add(usersLayout);

        Button leftButton = new Button(new Icon(VaadinIcon.ARROW_LEFT), event -> scrollLeft());
        leftButton.setHeight("200px");

        Button rightButton = new Button(new Icon(VaadinIcon.ARROW_RIGHT), event -> scrollRight());
        rightButton.setHeight("200px");

        HorizontalLayout navigationLayout = new HorizontalLayout(leftButton, scrollableDiv, rightButton);
        navigationLayout.setWidthFull();
        navigationLayout.setAlignItems(Alignment.CENTER);

        add(navigationLayout);

        grid = new Grid<>(UserBet.class, false);
        grid.addColumn(UserBet::getUsername).setHeader("Name").setAutoWidth(true);
        grid.addColumn(UserBet::getBetType).setHeader("Bet Type").setAutoWidth(true);
        grid.addColumn(UserBet::getBetValue).setHeader("Bet Value").setAutoWidth(true);
        grid.setItems(dataService.getUserBets());

        add(grid);
    }

    private static HorizontalLayout createUserLayout(User user) {
        HorizontalLayout userLayout = new HorizontalLayout();

        Avatar avatar = new Avatar(user.getUsername());
        avatar.setHeight("64px");
        avatar.setWidth("64px");

        VerticalLayout userInformation = new VerticalLayout();
        userInformation.setSpacing(false);
        userInformation.setPadding(false);
        userInformation.setWidth("250px");
        userInformation.getElement().appendChild(ElementFactory.createStrong(user.getUsername()));

        VerticalLayout userStats = new VerticalLayout();
        userStats.setSpacing(false);
        userStats.add(new Div(new Text("Number of Bets Made: " + user.getNumberOfBetsMade().toString())));
        userStats.add(new Div(new Text("Number of Bets Won: "+ user.getNumberOfBetsWon().toString())));
        userStats.add(new Div(new Text("Amount Owing: $" + user.getAmountOwing().toString())));
        userStats.add(new Div(new Text("Amount Won: $" + user.getAmountWon().toString())));

        String netAmount = user.getNetAmount().toString();
        if (netAmount.contains("-")) {
            Div div = new Div(new Text("Net Amount: -$" + netAmount.substring(1)));
            div.getStyle().set("color", "red");
            userStats.add(div);
        } else {
            userStats.add(new Div(new Text("Net Amount: $" + netAmount)));
        }

        userInformation.add(userStats);

        userLayout.add(avatar, userInformation);
        return userLayout;
    }

    private void scrollLeft() {
        getElement().executeJs("document.getElementById('scrollableDiv').scrollBy({ left: -330, behavior: 'smooth' });");
    }

    private void scrollRight() {
        getElement().executeJs("document.getElementById('scrollableDiv').scrollBy({ left: 330, behavior: 'smooth' });");
    }
}
