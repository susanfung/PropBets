package com.example.application.views.viewbets;

import com.example.application.data.DataService;
import com.example.application.data.UserBet;
import com.example.application.data.UserBetsSummary;
import com.example.application.security.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@PageTitle("View Bets")
@Route(value = "viewBets", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ViewBets extends VerticalLayout {
    private Grid<UserBet> grid;
    private final DataService dataService;
    private final UserService userService;

    private Map<String, String> scoreBoardBetsSummary;

    public ViewBets(DataService dataService, UserService userService) {
        this.dataService = dataService;
        this.userService = userService;

        Div scrollableDiv = new Div();
        scrollableDiv.getStyle().set("overflow-x", "hidden");
        scrollableDiv.setWidthFull();
        scrollableDiv.setId("scrollableDiv");

        HorizontalLayout usersLayout = new HorizontalLayout();
        usersLayout.setSpacing(false);
        usersLayout.setWidthFull();

        List<UserBetsSummary> userBetsSummaries = this.dataService.getUserBetsSummary();
        userBetsSummaries.forEach(userBetsSummary -> {
            Document user = userService.findUserByUsername(userBetsSummary.getUsername());
            HorizontalLayout userLayout = createUserLayout(user, userBetsSummary);
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

        H2 scoreBoardTitle = new H2("Score Board Bets");

        HorizontalLayout teams = new HorizontalLayout();

        Button team1 = new Button("Team 1 Score");
        team1.setEnabled(false);
        team1.addClassName("team-1");

        Button team2 = new Button("Team 2 Score");
        team1.setEnabled(false);
        team2.addClassName("team-2");

        teams.add(team1, team2);

        HorizontalLayout columnNamesForScoreBoard = createColumnNamesForScoreBoard();
        add(navigationLayout, scoreBoardTitle, teams, columnNamesForScoreBoard);

        scoreBoardBetsSummary = this.dataService.getScoreBoardBetsSummary();

        IntStream.rangeClosed(0, 9).forEach(row -> {
            HorizontalLayout rowsForScoreBoard = createRowsForScoreBoard(row);
            add(rowsForScoreBoard);
        });

        Map<String, List<String>> propBetsSummaries = this.dataService.getPropBetsSummary();
        propBetsSummaries.forEach((betType, betValues) -> {
            VerticalLayout betSummary = new VerticalLayout();
            betSummary.setSpacing(false);

            betSummary.add(new Div(new Text(betType)));

            betValues.forEach(betValue -> {
                Div betValueDiv = new Div(new Text(betValue));
                betSummary.add(betValueDiv);
            });

            add(betSummary);
        });

        grid = new Grid<>(UserBet.class, false);
        grid.addColumn(UserBet::getUsername).setHeader("Name").setAutoWidth(true);
        grid.addColumn(UserBet::getBetType).setHeader("Bet Type").setAutoWidth(true);
        grid.addColumn(UserBet::getBetValue).setHeader("Bet Value").setAutoWidth(true);
        grid.setItems(dataService.getUserBets());

        add(grid);
    }

    private static HorizontalLayout createUserLayout(Document user, UserBetsSummary userBetsSummary) {
        String name = userBetsSummary.getUsername();
        String profileName = user.getString("name");

        if (profileName != null) {
            name = profileName;
        }

        StreamResource profileImage = new StreamResource("profile-image",
                                          () -> new ByteArrayInputStream(user.get("profileImage", Binary.class).getData()));

        HorizontalLayout userLayout = new HorizontalLayout();

        Avatar avatar = new Avatar();
        avatar.setHeight("64px");
        avatar.setWidth("64px");
        avatar.setImageResource(profileImage);

        VerticalLayout userInformation = new VerticalLayout();
        userInformation.setSpacing(false);
        userInformation.setPadding(false);
        userInformation.setWidth("250px");
        userInformation.getElement().appendChild(ElementFactory.createStrong(name));

        VerticalLayout userStats = new VerticalLayout();
        userStats.setSpacing(false);
        userStats.add(new Div(new Text("Number of Bets Made: " + userBetsSummary.getNumberOfBetsMade().toString())));
        userStats.add(new Div(new Text("Number of Bets Won: "+ userBetsSummary.getNumberOfBetsWon().toString())));
        userStats.add(new Div(new Text("Amount Owing: $" + userBetsSummary.getAmountOwing().toString())));
        userStats.add(new Div(new Text("Amount Won: $" + userBetsSummary.getAmountWon().toString())));

        String netAmount = userBetsSummary.getNetAmount().toString();
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

    private static HorizontalLayout createColumnNamesForScoreBoard() {
        HorizontalLayout columnNames = new HorizontalLayout();

        VerticalLayout blankCell = new VerticalLayout();
        blankCell.setWidth("75px");

        columnNames.add(blankCell);

        for (int col = 0; col <= 9; col++) {
            VerticalLayout columnNumber = new VerticalLayout();
            columnNumber.addClassName("team-2");
            columnNumber.getStyle().set("border-radius", "10px");
            columnNumber.setWidth("100px");
            columnNumber.setAlignItems(Alignment.CENTER);

            Span columnSpan = new Span(String.valueOf(col));
            columnSpan.getStyle().set("text-align", "center");
            columnNumber.add(columnSpan);
            columnNames.add(columnNumber);
        }

        return columnNames;
    }

    private HorizontalLayout createRowsForScoreBoard(int row) {
        HorizontalLayout rows = new HorizontalLayout();

        VerticalLayout rowNumber = new VerticalLayout();
        rowNumber.addClassName("team-1");
        rowNumber.getStyle().set("border-radius", "10px");
        rowNumber.setWidth("75px");
        rowNumber.setAlignItems(Alignment.CENTER);
        rowNumber.setJustifyContentMode(JustifyContentMode.CENTER);

        Span rowSpan = new Span(String.valueOf(row));
        rowSpan.getStyle().set("text-align", "center");
        rowNumber.add(rowSpan);
        rows.add(rowNumber);

        IntStream.rangeClosed(0, 9).forEach(col -> {
            String buttonText = scoreBoardBetsSummary.get(row + "," + col);

            VerticalLayout cellLayout = new VerticalLayout();
            cellLayout.setWidth("100px");
            cellLayout.getStyle().set("border", "1px solid black");
            cellLayout.getStyle().set("border-radius", "10px");

            if (buttonText != null) {
                Span span = new Span(buttonText);
                span.getStyle().set("white-space", "pre-wrap");
                cellLayout.add(span);
            }

            rows.add(cellLayout);
        });

        return rows;
    }
}
