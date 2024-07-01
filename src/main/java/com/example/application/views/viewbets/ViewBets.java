package com.example.application.views.viewbets;

import com.example.application.data.DataService;
import com.example.application.data.UserBet;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("View Bets")
@Route(value = "viewBets", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ViewBets extends VerticalLayout {
    private Grid<UserBet> grid;
    private final DataService dataService;

    public ViewBets(DataService dataService) {
        this.dataService = dataService;

        grid = new Grid<>(UserBet.class, false);
        grid.addColumn(UserBet::getUsername).setHeader("Name").setAutoWidth(true);
        grid.addColumn(UserBet::getBetType).setHeader("Bet Type").setAutoWidth(true);
        grid.addColumn(UserBet::getBetValue).setHeader("Bet Value").setAutoWidth(true);
        grid.setItems(dataService.getUserBets());

        add(grid);
    }
}
