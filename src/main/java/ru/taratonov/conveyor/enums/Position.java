package ru.taratonov.conveyor.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.taratonov.conveyor.exception.IllegalArgumentOfEnumException;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Position {
    MANAGER("manager"),
    TOP_MANAGER("top-manager");

    @JsonCreator
    static Position findValue(String findValue) {
        return Arrays.stream(Position.values())
                .filter(value -> value.name().equalsIgnoreCase(findValue))
                .findFirst()
                .orElseThrow(() -> IllegalArgumentOfEnumException.createWith(
                        Arrays.stream(Position.values())
                        .map(Position::getTitle)
                        .collect(Collectors.toList())));

    }

    private String title;

    Position(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
