package ru.taratonov.conveyor.exception;

import java.util.List;

public class ScoringException extends RuntimeException {
    private List<String> exceptions;

    public static ScoringException createWith(List<String> exceptions) {
        return new ScoringException(exceptions);
    }

    private ScoringException(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    private String makeMessage(List<String> exceptions){
        StringBuilder message = new StringBuilder();
        int i = 0;
        for (String exception : exceptions) {
            message.append(++i).append(". ").append(exception).append(" ");
        }
        return message.toString();
    }

    @Override
    public String getMessage() {
        return makeMessage(exceptions);
    }
}
