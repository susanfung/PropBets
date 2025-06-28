package com.example.application.views.viewbets;

import com.example.application.data.DataService;
import com.example.application.data.PropBetsSummary;
import com.example.application.data.ScoreBoardBetsSummary;
import com.example.application.data.UserBetsSummary;
import com.example.application.security.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
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

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.application.utils.Utils.ICON_SIZE;
import static com.example.application.utils.Utils.TEAM_1_LOGO_SOURCE;
import static com.example.application.utils.Utils.TEAM_1_NAME;
import static com.example.application.utils.Utils.TEAM_2_LOGO_SOURCE;
import static com.example.application.utils.Utils.TEAM_2_NAME;

@PageTitle("View Bets")
@Route(value = "viewBets", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ViewBets extends VerticalLayout {
    private final DataService dataService;
    private final UserService userService;

    private static final String rowNumberWidth = "75px";
    private static final String cellWidth = "145px";

    private List<ScoreBoardBetsSummary> scoreBoardBetsSummary;

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
            org.json.JSONObject user = userService.findUserByUsername(userBetsSummary.username());
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

        Image team1Icon = new Image(TEAM_1_LOGO_SOURCE, TEAM_1_NAME);
        team1Icon.setWidth(ICON_SIZE);
        team1Icon.setHeight(ICON_SIZE);

        Button team1 = new Button(TEAM_1_NAME);
        team1.setEnabled(false);
        team1.addClassName("team-1");
        team1.setIcon(team1Icon);

        Image team2Icon = new Image(TEAM_2_LOGO_SOURCE, TEAM_2_NAME);
        team2Icon.setWidth(ICON_SIZE);
        team2Icon.setHeight(ICON_SIZE);

        Button team2 = new Button(TEAM_2_NAME);
        team1.setEnabled(false);
        team2.addClassName("team-2");
        team2.setIcon(team2Icon);

        teams.add(team1, team2);

        HorizontalLayout columnNamesForScoreBoard = createColumnNamesForScoreBoard();
        add(navigationLayout, scoreBoardTitle, teams, columnNamesForScoreBoard);

        scoreBoardBetsSummary = this.dataService.getScoreBoardBetsSummary();

        IntStream.rangeClosed(0, 9).forEach(row -> {
            HorizontalLayout rowsForScoreBoard = createRowsForScoreBoard(row);
            add(rowsForScoreBoard);
        });

        List<PropBetsSummary> propBetsSummaries = this.dataService.getPropBetsSummary();
        Map<String, List<PropBetsSummary>> groupedSummaries = propBetsSummaries.stream()
                                                                               .collect(Collectors.groupingBy(PropBetsSummary::question));

        groupedSummaries.forEach(this::createPropBetsSummary);
    }

    private static HorizontalLayout createUserLayout(org.json.JSONObject user, UserBetsSummary userBetsSummary) {
        String name = userBetsSummary.username();
        String firstName = null;
        try {
            firstName = user.getString("firstName");
        } catch (org.json.JSONException e) {
            firstName = null;
        }
        byte[] image = null;
        if (user.has("profileImage")) {
            String base64 = user.optString("profileImage", null);
            image = base64 != null ? java.util.Base64.getDecoder().decode(base64) : null;
        }
        final byte[] finalImage = image;

        HorizontalLayout userLayout = new HorizontalLayout();

        Avatar avatar = new Avatar();
        avatar.setHeight("64px");
        avatar.setWidth("64px");

        if (image != null) {
            StreamResource profileImage = new StreamResource("profile-image",
                                          () -> new java.io.ByteArrayInputStream(finalImage));
            avatar.setImageResource(profileImage);
        }

        VerticalLayout userInformation = new VerticalLayout();
        userInformation.setSpacing(false);
        userInformation.setPadding(false);
        userInformation.setWidth("250px");
        userInformation.getElement().appendChild(ElementFactory.createStrong(name));

        VerticalLayout userStats = new VerticalLayout();
        userStats.setSpacing(false);
        userStats.add(new Div(new Text("Number of Bets Made: " + userBetsSummary.numberOfBetsMade().toString())));
        userStats.add(new Div(new Text("Amount Owing: $" + userBetsSummary.amountOwing().toString())));
        userStats.add(new Div(new Text("Number of Bets Won: "+ userBetsSummary.numberOfBetsWon().toString())));
        userStats.add(new Div(new Text("Amount Won: $" + userBetsSummary.amountWon().toString())));

        String netAmount = userBetsSummary.netAmount().toString();
        if (netAmount.contains("-")) {
            Div div = new Div(new Text("Net Amount: -$" + netAmount.substring(1)));
            div.getStyle().set("font-weight", "bold");
            div.getStyle().set("color", "red");
            userStats.add(div);
        } else {
            Div div = new Div(new Text("Net Amount: $" + netAmount));
            div.getStyle().set("font-weight", "bold");
            div.getStyle().set("color", "green");
            userStats.add(div);
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
        setCellStyle(blankCell, rowNumberWidth, false);

        columnNames.add(blankCell);

        for (int col = 0; col <= 9; col++) {
            VerticalLayout columnNumber = new VerticalLayout();
            columnNumber.addClassName("team-2");
            setCellStyle(columnNumber, cellWidth, true);

            Span columnSpan = new Span(String.valueOf(col));
            setSpanStyle(columnSpan);
            columnNumber.add(columnSpan);
            columnNames.add(columnNumber);
        }

        return columnNames;
    }

    private HorizontalLayout createRowsForScoreBoard(int row) {
        HorizontalLayout rows = new HorizontalLayout();

        VerticalLayout rowNumber = new VerticalLayout();
        rowNumber.addClassName("team-1");
        setCellStyle(rowNumber, rowNumberWidth, true);
        rowNumber.setJustifyContentMode(JustifyContentMode.CENTER);

        Span rowSpan = new Span(String.valueOf(row));
        setSpanStyle(rowSpan);
        rowNumber.add(rowSpan);
        rows.add(rowNumber);

        IntStream.rangeClosed(0, 9).forEach(col -> {
            ScoreBoardBetsSummary summary = scoreBoardBetsSummary.stream()
                                                                 .filter(s -> s.betValue().equals(row + "," + col))
                                                                 .findFirst()
                                                                 .orElse(null);

            VerticalLayout cellLayout = new VerticalLayout();
            setCellStyle(cellLayout, cellWidth, false);
            cellLayout.getStyle().set("border", "1px solid black");

            if (summary != null) {
                Span bettersSpan = new Span(String.join("\n", summary.betters()));
                bettersSpan.getStyle().set("white-space", "pre-wrap");

                HorizontalLayout iconsLayout = new HorizontalLayout();
                iconsLayout.setSpacing(false);

                IntStream.range(0, summary.count().orElse(0)).forEach(i -> {
                    Image footballIcon = new Image("icons/nfl.svg", "NFL Logo");
                    footballIcon.setWidth(ICON_SIZE);
                    footballIcon.setHeight(ICON_SIZE);

                    iconsLayout.add(footballIcon);
                });

                cellLayout.add(bettersSpan, iconsLayout);
            }

            rows.add(cellLayout);
        });

        return rows;
    }

    private static void setCellStyle(VerticalLayout layout, String width, Boolean isLabel) {
        layout.getStyle().set("border-radius", "10px");
        layout.setWidth(width);

        if (isLabel) {
            layout.setAlignItems(Alignment.CENTER);
        }
    }

    private static void setSpanStyle(Span rowSpan) {
        rowSpan.getStyle().set("text-align", "center");
    }

    private void createPropBetsSummary(String question, List<PropBetsSummary> summaries) {
        Hr separator = new Hr();
        separator.getElement().getStyle().set("background-color", "black");

        VerticalLayout betSummary = new VerticalLayout();
        betSummary.setPadding(false);

        Button questionButton = new Button(question);
        setPropBetSummaryButtonStyle(questionButton);
        questionButton.getStyle().set("color", "blue");

        betSummary.add(questionButton);

        summaries.forEach(summary -> {
            String betValueText = summary.betValue() + " - " + String.join(", ", summary.betters());
            Button betValueButton = new Button(betValueText);
            setPropBetSummaryButtonStyle(betValueButton);

            Image winnerIcon = new Image("icons/success.png", "Winner");
            winnerIcon.setWidth(ICON_SIZE);
            winnerIcon.setHeight(ICON_SIZE);

            Image loserIcon = new Image("icons/loser.png", "Loser");
            loserIcon.setWidth(ICON_SIZE);
            loserIcon.setHeight(ICON_SIZE);

            if (summary.isWinner() != null && summary.isWinner()) {
                betValueButton.setIcon(winnerIcon);
            } else if (summary.isWinner() != null) {
                betValueButton.setIcon(loserIcon);
            }

            betSummary.add(betValueButton);
        });

        add(separator, betSummary);
    }

    private static void setPropBetSummaryButtonStyle(Button button) {
        button.setEnabled(false);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("prop-bet-summary-button");
    }
}
