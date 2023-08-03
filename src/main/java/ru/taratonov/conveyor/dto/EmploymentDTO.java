package ru.taratonov.conveyor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.taratonov.conveyor.enums.EmploymentStatus;
import ru.taratonov.conveyor.enums.Position;

import java.math.BigDecimal;

@Data
public class EmploymentDTO {

    @NotNull(message = "must not be empty")
    private EmploymentStatus employmentStatus;

    @NotNull(message = "must not be empty")
    @Pattern(regexp = "^(\\d{10}|\\d{12})$", message = "must include only 12 numbers for individuals and 10 to legal entities")
    private String employerINN;

    @NotNull(message = "must not be empty")
    @Min(value = 0, message = "can't be less than 0")
    private BigDecimal salary;

    @NotNull(message = "must not be empty")
    private Position position;

    @NotNull(message = "must not be empty")
    @Min(value = 0)
    private Integer workExperienceTotal;

    @NotNull(message = "must not be empty")
    @Min(value = 0)
    private Integer workExperienceCurrent;
}
