package ru.taratonov.conveyor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.taratonov.conveyor.enums.Gender;
import ru.taratonov.conveyor.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDTO {
    @NotNull(message = "must not be empty")
    @DecimalMin(value = "10000.0", message = "must be greater or equal than 10000")
    private BigDecimal amount;

    @NotNull(message = "must not be empty")
    @Min(value = 6, message = "can't take loan less than 6 month")
    private Integer term;

    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "must include only letters")
    @Size(min = 2, max = 30, message = "must be in range from 2 to 30 symbols")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "must include only letters")
    @Size(min = 2, max = 30, message = "must be in range from 2 to 30 symbols")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "must include only letters")
    @Size(min = 2, max = 30, message = "must be in range from 2 to 30 symbols")
    private String middleName;

    @NotNull(message = "must not be empty")
    private Gender gender;

    @NotNull(message = "must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @NotNull(message = "must not be empty")
    @Pattern(regexp = "\\d+", message = "must include only numbers")
    @Size(min = 4, max = 4, message = "must be 4 digits long")
    private String passportSeries;

    @NotNull(message = "must not be empty")
    @Pattern(regexp = "\\d+", message = "must include only numbers")
    @Size(min = 6, max = 6, message = "must be 6 digits long")
    private String passportNumber;

    @NotNull(message = "must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportIssueDate;

    @NotNull(message = "must not be empty")
    private String passportIssueBranch;

    @NotNull(message = "must not be empty")
    private MaritalStatus maritalStatus;

    @NotNull(message = "must not be empty")
    @Min(value = 0, message = "must be greater or equal than 0")
    private Integer dependentAmount;

    @NotNull(message = "must not be empty")
    private EmploymentDTO employment;

    @NotNull(message = "must not be empty")
    private String account;

    @NotNull(message = "must not be empty")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "must not be empty")
    private Boolean isSalaryClient;
}
