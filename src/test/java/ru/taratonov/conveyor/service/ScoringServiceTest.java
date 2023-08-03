package ru.taratonov.conveyor.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ScoringServiceTest {


    @Autowired
    private ScoringService scoringService;

    @Value("${base.rate}")
    private BigDecimal baseRate;


    @Test
    void scoringPerson() {

    }

    @Test
    public void testScoringPerson_InsuranceEnabledAndSalaryClient() {
        boolean isInsuranceEnabled = true;
        boolean isSalaryClient = true;

        BigDecimal result = scoringService.scoringPerson(isInsuranceEnabled, isSalaryClient);

        BigDecimal expected = baseRate.subtract(BigDecimal.valueOf(4));

        assertEquals(expected, result);
    }

    @Test
    public void testScoringPerson_InsuranceEnabledAndNotSalaryClient() {
        boolean isInsuranceEnabled = true;
        boolean isSalaryClient = false;

        BigDecimal result = scoringService.scoringPerson(isInsuranceEnabled, isSalaryClient);

        BigDecimal expected = baseRate.subtract(BigDecimal.valueOf(3));

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testScoringPerson_NotInsuranceEnabledAndSalaryClient() {
        boolean isInsuranceEnabled = false;
        boolean isSalaryClient = true;

        BigDecimal result = scoringService.scoringPerson(isInsuranceEnabled, isSalaryClient);

        BigDecimal expected = baseRate.subtract(BigDecimal.valueOf(1));

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testScoringPerson_NotInsuranceEnabledAndNotSalaryClient() {
        boolean isInsuranceEnabled = false;
        boolean isSalaryClient = false;

        BigDecimal result = scoringService.scoringPerson(isInsuranceEnabled, isSalaryClient);

        BigDecimal expected = baseRate;

        Assertions.assertEquals(expected, result);
    }

}