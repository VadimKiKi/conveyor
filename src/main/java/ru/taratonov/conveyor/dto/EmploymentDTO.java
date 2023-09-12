package ru.taratonov.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.taratonov.conveyor.enums.EmploymentStatus;
import ru.taratonov.conveyor.enums.Position;

import java.math.BigDecimal;

@Data
public class EmploymentDTO {

    @Schema(
            description = "person's working status",
            name = "employmentStatus",
            example = "SELF_EMPLOYED")
    @NotNull(message = "must not be empty")
    private EmploymentStatus employmentStatus;

    @Schema(
            description = "persons inn",
            name = "employerINN",
            example = "1234567890")
    @NotNull(message = "must not be empty")
    @Pattern(regexp = "^(\\d{10}|\\d{12})$", message = "must include only 12 numbers for individuals and 10 to legal entities")
    private String employerINN;

    @Schema(
            description = "persons salary",
            name = "salary",
            example = "12000")
    @NotNull(message = "must not be empty")
    @Min(value = 0, message = "can't be less than 0")
    private BigDecimal salary;

    @Schema(
            description = "the user's position on the job",
            name = "position",
            example = "MANAGER")
    @NotNull(message = "must not be empty")
    private Position position;

    @Schema(
            description = "total work experience of person",
            name = "workExperienceTotal",
            example = "12")
    @NotNull(message = "must not be empty")
    @Min(value = 0)
    private Integer workExperienceTotal;

    @Schema(
            description = "current work experience of person in job",
            name = "workExperienceCurrent",
            example = "5")
    @NotNull(message = "must not be empty")
    @Min(value = 0)
    private Integer workExperienceCurrent;
}
