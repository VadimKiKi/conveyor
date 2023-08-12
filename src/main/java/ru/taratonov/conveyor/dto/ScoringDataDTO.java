package ru.taratonov.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.taratonov.conveyor.enums.Gender;
import ru.taratonov.conveyor.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDTO {
    @Schema(
            description = "the amount requested by the client",
            name = "amount",
            example = "10000")
    @NotNull(message = "must not be empty")
    @DecimalMin(value = "10000.0", message = "must be greater or equal than 10000")
    private BigDecimal amount;

    @Schema(
            description = "loan term",
            name = "term",
            example = "7")
    @NotNull(message = "must not be empty")
    @Min(value = 6, message = "can't take loan less than 6 month")
    private Integer term;

    @Schema(
            description = "first name of person",
            name = "firstName",
            example = "Vadim")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "must include only letters")
    @Size(min = 2, max = 30, message = "must be in range from 2 to 30 symbols")
    private String firstName;

    @Schema(
            description = "last name of person",
            name = "lastName",
            example = "Taratonov")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "must include only letters")
    @Size(min = 2, max = 30, message = "must be in range from 2 to 30 symbols")
    private String lastName;

    @Schema(
            description = "middle name of person",
            name = "middleName",
            example = "Nikolaevich")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "must include only letters")
    @Size(min = 2, max = 30, message = "must be in range from 2 to 30 symbols")
    private String middleName;

    @Schema(
            description = "person's gender",
            name = "gender",
            example = "MALE")
    @NotNull(message = "must not be empty")
    private Gender gender;

    @Schema(
            description = "birthday of person",
            name = "birthdate",
            example = "2001-10-02")
    @NotNull(message = "must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Schema(
            description = "passport series of person",
            name = "passportSeries",
            example = "2021")
    @NotNull(message = "must not be empty")
    @Pattern(regexp = "\\d+", message = "must include only numbers")
    @Size(min = 4, max = 4, message = "must be 4 digits long")
    private String passportSeries;

    @Schema(
            description = "passport number of person",
            name = "passportNumber",
            example = "111111")
    @NotNull(message = "must not be empty")
    @Pattern(regexp = "\\d+", message = "must include only numbers")
    @Size(min = 6, max = 6, message = "must be 6 digits long")
    private String passportNumber;

    @Schema(
            description = "date of issue of the passport",
            name = "passportIssueDate",
            example = "2010-01-01")
    @NotNull(message = "must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportIssueDate;

    @Schema(
            description = "passport issuing department",
            name = "passportIssueBranch",
            example = "ГУ МВД РОССИИ")
    @NotNull(message = "must not be empty")
    private String passportIssueBranch;

    @Schema(
            description = "person's marital status",
            name = "maritalStatus",
            example = "MARRIED")
    @NotNull(message = "must not be empty")
    private MaritalStatus maritalStatus;

    @Schema(
            description = "number of dependents",
            name = "dependentAmount",
            example = "1")
    @NotNull(message = "must not be empty")
    @Min(value = 0, message = "must be greater or equal than 0")
    private Integer dependentAmount;

    @Schema(
            description = "information about person at work",
            name = "employment")
    @NotNull(message = "must not be empty")
    private EmploymentDTO employment;

    @Schema(
            description = "person's account",
            name = "account",
            example = "124353")
    @NotNull(message = "must not be empty")
    private String account;

    @Schema(
            description = "availability of credit insurance",
            name = "isInsuranceEnabled",
            example = "false")
    @NotNull(message = "must not be empty")
    private Boolean isInsuranceEnabled;

    @Schema(
            description = "salary client",
            name = "isSalaryClient",
            example = "true")
    @NotNull(message = "must not be empty")
    private Boolean isSalaryClient;
}
