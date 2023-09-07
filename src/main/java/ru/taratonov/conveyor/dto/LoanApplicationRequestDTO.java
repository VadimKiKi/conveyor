package ru.taratonov.conveyor.dto;


import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(
            description = "the amount requested by the client",
            name = "amount",
            example = "10000")
    @DecimalMin(value = "10000.0", message = "must be greater or equal than 10000")
    private BigDecimal amount;

    @Schema(
            description = "loan term",
            name = "term",
            example = "4")
    @NotNull(message = "must not be empty")
    @Min(value = 6, message = "must be greater or equal than 6")
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
            description = "email of person",
            name = "email",
            example = "taratonovv8@bk.ru")
    @NotNull(message = "must not be empty")
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}", message = "doesn't match the right format")
    private String email;

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

    @AssertTrue(message = "must be no later than 18 years from the current day")
    public boolean isBirthDateValid() {
        return !birthdate.plusYears(18).isAfter(LocalDate.now());

    }
}
