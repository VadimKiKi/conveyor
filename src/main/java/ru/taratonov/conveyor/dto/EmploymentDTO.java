package ru.taratonov.conveyor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taratonov.conveyor.enums.EmploymentStatus;
import ru.taratonov.conveyor.enums.Position;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDTO {

    private EmploymentStatus employmentStatus;

    @Pattern(regexp = "^(\\d{10}|\\d{12})$", message = "must include only 12 numbers for individuals and 10 to legal entities")
    private String employerINN;

    @Min(value = 0, message = "can't be less than 0")
    private BigDecimal salary;

    private Position position;

    @Min(value = 0)
    private Integer workExperienceTotal;

    @Min(value = 0)
    private Integer workExperienceCurrent;
}
