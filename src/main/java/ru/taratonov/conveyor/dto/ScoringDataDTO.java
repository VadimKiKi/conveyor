package ru.taratonov.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taratonov.conveyor.enums.Gender;
import ru.taratonov.conveyor.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDTO {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private EmploymentDTO employment;
    private String account;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}
