package com.example.application.data;

import java.util.List;

public record PropBet(String name, String question, List<String> choices, java.util.Optional<Boolean> isLocked) {
    @Override
    public String toString() {
        return getClass().getName() + "[name=" + name + ",question=" + question + ",choices=" + choices + ",isLocked=" + isLocked.orElse(false) + "]";
    }
}
