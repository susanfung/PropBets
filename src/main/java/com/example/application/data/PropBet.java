package com.example.application.data;

import java.util.List;
import java.util.stream.Stream;

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

    public static PropBet createNewPropBet(String name, String question, String choices) {
        return new PropBet(name,
                           question,
                           Stream.of(choices.split(","))
                                 .map(String::trim)
                                 .toList());
    }
}
