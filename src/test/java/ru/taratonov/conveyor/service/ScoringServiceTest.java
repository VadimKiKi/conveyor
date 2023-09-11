package ru.taratonov.conveyor.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.taratonov.conveyor.dto.EmploymentDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;
import ru.taratonov.conveyor.enums.MaritalStatus;
import ru.taratonov.conveyor.exception.ScoringException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ru.taratonov.conveyor.enums.EmploymentStatus.SELF_EMPLOYED;
import static ru.taratonov.conveyor.enums.EmploymentStatus.UNEMPLOYED;
import static ru.taratonov.conveyor.enums.Gender.FEMALE;
import static ru.taratonov.conveyor.enums.Gender.MALE;
import static ru.taratonov.conveyor.enums.Position.MANAGER;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ScoringServiceTest {


    @Autowired
    private ScoringService scoringService;

    @Mock
    private ScoringDataDTO scoringDataDTO;

    @Mock
    private EmploymentDTO employmentDTO;

    @Value("${base.rate}")
    private BigDecimal baseRate;


    @Test
    void scoringPerson_WithoutException() {
        Boolean isInsuranceEnabled = false;
        Boolean IsSalaryClient = false;
        when(scoringDataDTO.getIsInsuranceEnabled()).thenReturn(isInsuranceEnabled);
        when(scoringDataDTO.getIsSalaryClient()).thenReturn(IsSalaryClient);
        when(scoringDataDTO.getEmployment()).thenReturn(employmentDTO);
        when(employmentDTO.getEmploymentStatus()).thenReturn(SELF_EMPLOYED);
        when(employmentDTO.getPosition()).thenReturn(MANAGER);
        when(scoringDataDTO.getAmount()).thenReturn(BigDecimal.valueOf(10));
        when(employmentDTO.getSalary()).thenReturn(BigDecimal.ONE);
        when(scoringDataDTO.getMaritalStatus()).thenReturn(MaritalStatus.DIVORCED);
        when(scoringDataDTO.getDependentAmount()).thenReturn(2);
        when(scoringDataDTO.getBirthdate()).thenReturn(LocalDate.of(1980, 10, 2));
        when(scoringDataDTO.getGender()).thenReturn(MALE);
        when(employmentDTO.getWorkExperienceCurrent()).thenReturn(5);
        when(employmentDTO.getWorkExperienceTotal()).thenReturn(14);

        BigDecimal expected = scoringService.scoringPerson(scoringDataDTO);

        BigDecimal real = BigDecimal.valueOf(6);
        assertEquals(real, expected);
    }

    @Test
    void scoringPerson_WithException() {
        Boolean isInsuranceEnabled = false;
        Boolean IsSalaryClient = false;
        when(scoringDataDTO.getIsInsuranceEnabled()).thenReturn(isInsuranceEnabled);
        when(scoringDataDTO.getIsSalaryClient()).thenReturn(IsSalaryClient);
        when(scoringDataDTO.getEmployment()).thenReturn(employmentDTO);
        when(employmentDTO.getEmploymentStatus()).thenReturn(UNEMPLOYED);
        when(employmentDTO.getPosition()).thenReturn(MANAGER);
        when(scoringDataDTO.getAmount()).thenReturn(BigDecimal.valueOf(30));
        when(employmentDTO.getSalary()).thenReturn(BigDecimal.ONE);
        when(scoringDataDTO.getMaritalStatus()).thenReturn(MaritalStatus.DIVORCED);
        when(scoringDataDTO.getDependentAmount()).thenReturn(2);
        when(scoringDataDTO.getBirthdate()).thenReturn(LocalDate.of(2021, 10, 2));
        when(scoringDataDTO.getGender()).thenReturn(FEMALE);
        when(employmentDTO.getWorkExperienceCurrent()).thenReturn(2);
        when(employmentDTO.getWorkExperienceTotal()).thenReturn(2);

        assertThrows(ScoringException.class, () -> {
            scoringService.scoringPerson(scoringDataDTO);
        });
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