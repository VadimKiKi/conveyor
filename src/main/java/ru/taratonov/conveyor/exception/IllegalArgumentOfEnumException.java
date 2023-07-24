package ru.taratonov.conveyor.exception;

import java.util.List;

public class IllegalArgumentOfEnumException extends RuntimeException {
    private List<String> values;

    public static IllegalArgumentOfEnumException createWith(List<String> values) {
        return new IllegalArgumentOfEnumException(values);
    }

    private IllegalArgumentOfEnumException(List<String> values) {
        this.values = values;
    }

    private String makeMessage(List<String> values) {
        StringBuilder message = new StringBuilder();
        message.append("Illegal argument, must be one of: ");
        for (int i = 0; i < values.size(); i++) {
            if (i == values.size() - 1) {
                message.append(values.get(i)).append(" ");
            } else {
                message.append(values.get(i)).append(", ");
            }
        }
        message.append("or null");
        return message.toString();
    }

    @Override
    public String getMessage() {
        return makeMessage(values);
    }
}
