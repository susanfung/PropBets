package com.example.application.data;

import java.util.List;

public class PropBet {
    private final String name;
    private final String question;
    private final List<String> choices;

    public PropBet(String name, String question, List<String> choices) {
        this.name = name;
        this.question = question;
        this.choices = choices;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }
}
