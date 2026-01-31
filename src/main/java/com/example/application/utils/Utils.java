package com.example.application.utils;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;

import java.util.List;
import java.util.Optional;

public class Utils {
    public static final String TEAM_1_NAME = "New England Patriots";
    public static final String TEAM_1_TABLE_NAME = "patriots";
    public static final String TEAM_1_LOGO_SOURCE = "icons/new-england-patriots.png";
    public static final String TEAM_2_NAME = "Seattle Seahawks";
    public static final String TEAM_2_TABLE_NAME = "seahawks";
    public static final String TEAM_2_LOGO_SOURCE = "icons/seattle-seahawks.png";

    public static final int AMOUNT_PER_BET = 2;

    public static final String ICON_SIZE = "24px";

    public static RadioButtonGroup<String> createPropBet(String name, String question, List<String> choices) {
        RadioButtonGroup<String> propBet = new RadioButtonGroup<>();
        propBet.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        propBet.setClassName(name);
        propBet.setLabel(question);
        propBet.setItems(choices);
        return propBet;
    }

    public static Optional<RadioButtonGroup<String>> findRadioButtonGroup(VerticalLayout layout, String className) {
        return layout.getChildren()
                     .filter(RadioButtonGroup.class::isInstance)
                     .map(component -> (RadioButtonGroup<String>) component)
                     .filter(group -> className.equals(group.getClassName()))
                     .findFirst();
    }
}
