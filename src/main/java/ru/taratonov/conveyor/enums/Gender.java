package ru.taratonov.conveyor.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.taratonov.conveyor.exception.IllegalArgumentOfEnumException;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    NON_BINARY("non-binary");

    @JsonCreator
    static Gender findValue(String findValue) {
        return Arrays.stream(Gender.values())
                .filter(value -> value.name().equalsIgnoreCase(findValue))
                .findFirst()
                .orElseThrow(() -> IllegalArgumentOfEnumException.createWith(
                        Arrays.stream(Gender.values())
                        .map(Gender::getTitle)
                        .collect(Collectors.toList())));
    }

    private String title;

    Gender(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
