package ru.taratonov.conveyor.dto;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationRequestDTO {

    @NotNull(message = "must not be empty")
    @DecimalMin(value = "10000.0", message = "must be greater or equal than 10000")
    private BigDecimal amount;

    @NotNull(message = "must not be empty")
    @Min(value = 6, message = "must be greater or equal than 6")
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
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}", message = "doesn't match the right format")
    private String email;

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


    @AssertTrue(message = "must be no later than 18 years from the current day")
    public boolean isBirthDateValid() {
        return !birthdate.plusYears(18).isAfter(LocalDate.now());

    }
}
