package com.example.application.data;

import java.util.List;

public record PropBet(String name, String question, List<String> choices) {
    @Override
    public String toString() {
        return getClass().getName() + "[name=" + name + ",question=" + question + ",choices=" + choices + "]";
    }
}
