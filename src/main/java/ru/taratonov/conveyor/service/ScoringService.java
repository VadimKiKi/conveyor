package ru.taratonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.EmploymentDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;
import ru.taratonov.conveyor.enums.Gender;
import ru.taratonov.conveyor.exception.ScoringException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static ru.taratonov.conveyor.enums.EmploymentStatus.BUSINESS_OWNER;
import static ru.taratonov.conveyor.enums.EmploymentStatus.SELF_EMPLOYED;
import static ru.taratonov.conveyor.enums.EmploymentStatus.UNEMPLOYED;
import static ru.taratonov.conveyor.enums.MaritalStatus.DIVORCED;
import static ru.taratonov.conveyor.enums.MaritalStatus.MARRIED;
import static ru.taratonov.conveyor.enums.Position.MANAGER;
import static ru.taratonov.conveyor.enums.Position.TOP_MANAGER;

@Slf4j
@Service
public class ScoringService {

    @Value("${base.rate}")
    private BigDecimal baseRate;

    public BigDecimal scoringPerson(ScoringDataDTO scoringData) {
        log.info("!START SCORING PERSON {} {}!", scoringData.getFirstName(), scoringData.getLastName());
        BigDecimal personalRate = scoringPerson(scoringData.getIsInsuranceEnabled(),
                scoringData.getIsSalaryClient());

        List<String> exceptions = new ArrayList<>();

        EmploymentDTO employment = scoringData.getEmployment();

        // EmploymentStatus
        if (employment.getEmploymentStatus().equals(UNEMPLOYED)) {
            exceptions.add("Don't issue a loan to the unemployed");
        } else if (employment.getEmploymentStatus().equals(SELF_EMPLOYED)) {
            personalRate = personalRate.add(BigDecimal.valueOf(1));
        } else if (employment.getEmploymentStatus().equals(BUSINESS_OWNER)) {
            personalRate = personalRate.add(BigDecimal.valueOf(3));
        }

        // Position
        if (employment.getPosition().equals(MANAGER)) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(2));
        }
        if (employment.getPosition().equals(TOP_MANAGER)) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(4));
        }

        // Salary
        if (scoringData.getAmount().compareTo(employment.getSalary().multiply(BigDecimal.valueOf(20))) >= 0) {
            exceptions.add("The loan amount is more than 20 salaries");
        }

        // MaritalStatus
        if (scoringData.getMaritalStatus().equals(MARRIED)) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringData.getMaritalStatus().equals(DIVORCED)) {
            personalRate = personalRate.add(BigDecimal.valueOf(1));
        }

        // DependentAmount
        if (scoringData.getDependentAmount() > 1) {
            personalRate = personalRate.add(BigDecimal.valueOf(1));
        }

        // Age
        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
        if (age < 20) {
            exceptions.add("Age less than 20");
        }
        if (age > 60) {
            exceptions.add("Age more than 60");
        }

        // Gender
        if (scoringData.getGender().equals(Gender.FEMALE) && age >= 35 && age <= 60) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringData.getGender().equals(Gender.MALE) && age >= 30 && age <= 55) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringData.getGender().equals(Gender.NON_BINARY)) {
            personalRate = personalRate.add(BigDecimal.valueOf(3));
        }

        // WorkExperience
        if (employment.getWorkExperienceTotal() < 12) {
            exceptions.add("Total experience less than 12 months");
        }
        if (employment.getWorkExperienceCurrent() < 3) {
            exceptions.add("Current experience less than 3 months");
        }

        log.info("!FINISH PERSON SCORING!");

        if (exceptions.size() > 0) {
            throw ScoringException.createWith(exceptions);
        }

        if (personalRate.compareTo(BigDecimal.ZERO) <= 0) {
            personalRate = BigDecimal.valueOf(0.1);
        }

        log.info("Personal rate after scoring is {}", personalRate);
        return personalRate;
    }

    public BigDecimal scoringPerson(boolean isInsuranceEnabled, boolean isSalaryClient) {
        log.info("!BASE PERSON SCORING!");
        BigDecimal personalRate = baseRate;
        log.info("BASE RATE IN OUR BANK IS {}", baseRate);
        if (isInsuranceEnabled) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(3));
        }
        if (isSalaryClient) {
            personalRate = personalRate.subtract(BigDecimal.valueOf(1));
        }
        log.info("!FINISH BASE SCORING. Base personal rate is ready - {}", personalRate);
        return personalRate;
    }
}
