package ru.taratonov.conveyor.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.taratonov.conveyor.exception.IllegalArgumentOfEnumException;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EmploymentStatus {
    UNEMPLOYED("unemployed"),
    SELF_EMPLOYED("self-employed"),
    BUSINESS_OWNER("business-owner"),
    EMPLOYED("employed");

    @JsonCreator
    static EmploymentStatus findValue(String findValue) {
        return Arrays.stream(EmploymentStatus.values())
                .filter(value -> value.name().equalsIgnoreCase(findValue))
                .findFirst()
                .orElseThrow(() -> IllegalArgumentOfEnumException.createWith(
                        Arrays.stream(EmploymentStatus.values())
                                .map(EmploymentStatus::getTitle)
                                .collect(Collectors.toList())));
    }

    private String title;

    EmploymentStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
