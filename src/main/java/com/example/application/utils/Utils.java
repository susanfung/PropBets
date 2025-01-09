package com.example.application.utils;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;

import java.util.List;

public class Utils {
    public static RadioButtonGroup<String> createPropBet(String name, String question, List<String> choices) {
        RadioButtonGroup<String> propBet = new RadioButtonGroup<>();
        propBet.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        propBet.setClassName(name);
        propBet.setLabel(question);
        propBet.setItems(choices);
        return propBet;
    }
}
