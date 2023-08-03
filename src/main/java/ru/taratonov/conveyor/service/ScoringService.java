package ru.taratonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.EmploymentDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;
import ru.taratonov.conveyor.exception.ScoringException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScoringService {

    @Value("${base.rate}")
    private BigDecimal baseRate;
    @Value("${insurance.rate.reduction}")
    private BigDecimal INSURANCE_RATE_REDUCTION;
    @Value("${salary.rate.reduction}")
    private BigDecimal SALARY_RATE_REDUCTION;

    public BigDecimal scoringPerson(ScoringDataDTO scoringData) {
        log.debug("!START SCORING PERSON {} {}!", scoringData.getFirstName(), scoringData.getLastName());
        BigDecimal personalRate = scoringPerson(scoringData.getIsInsuranceEnabled(),
                scoringData.getIsSalaryClient());

        List<String> exceptions = new ArrayList<>();

        EmploymentDTO employment = scoringData.getEmployment();

        // EmploymentStatus
        switch (employment.getEmploymentStatus()) {
            case UNEMPLOYED -> exceptions.add("Don't issue a loan to the unemployed");
            case SELF_EMPLOYED -> personalRate = personalRate.add(BigDecimal.valueOf(1));
            case BUSINESS_OWNER -> personalRate = personalRate.add(BigDecimal.valueOf(3));
        }

        // Position
        switch (employment.getPosition()) {
            case MANAGER -> personalRate = personalRate.subtract(BigDecimal.valueOf(2));
            case TOP_MANAGER -> personalRate = personalRate.subtract(BigDecimal.valueOf(4));
        }

        // Salary
        if (scoringData.getAmount().compareTo(employment.getSalary().multiply(BigDecimal.valueOf(20))) >= 0) {
            exceptions.add("The loan amount is more than 20 salaries");
        }

        // MaritalStatus
        switch (scoringData.getMaritalStatus()) {
            case MARRIED -> personalRate = personalRate.subtract(BigDecimal.valueOf(3));
            case DIVORCED -> personalRate = personalRate.add(BigDecimal.valueOf(1));
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
        switch (scoringData.getGender()) {
            case MALE -> {
                if (age >= 30 && age <= 55) {
                    personalRate = personalRate.subtract(BigDecimal.valueOf(3));
                }
            }
            case FEMALE -> {
                if (age >= 35 && age <= 60) {
                    personalRate = personalRate.subtract(BigDecimal.valueOf(3));
                }
            }
            case NON_BINARY -> personalRate = personalRate.add(BigDecimal.valueOf(3));
        }

        // WorkExperience
        if (employment.getWorkExperienceTotal() < 12) {
            exceptions.add("Total experience less than 12 months");
        }
        if (employment.getWorkExperienceCurrent() < 3) {
            exceptions.add("Current experience less than 3 months");
        }

        log.debug("!FINISH PERSON SCORING!");

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
        log.debug("!BASE PERSON SCORING!");
        BigDecimal personalRate = baseRate;
        log.debug("BASE RATE IN OUR BANK IS {}", baseRate);
        if (isInsuranceEnabled) {
            personalRate = personalRate.subtract(INSURANCE_RATE_REDUCTION);
        }
        if (isSalaryClient) {
            personalRate = personalRate.subtract(SALARY_RATE_REDUCTION);
        }
        if (personalRate.compareTo(BigDecimal.ZERO) <= 0) {
            personalRate = BigDecimal.valueOf(0.1);
        }
        log.debug("!FINISH BASE SCORING. Base personal rate is ready - {}", personalRate);
        return personalRate;
    }
}
